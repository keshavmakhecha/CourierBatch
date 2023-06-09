package com.planet.courier.config;

import static com.planet.courier.constant.CourierConstant.CHUNK_SIZE;
import static com.planet.courier.constant.CourierConstant.CORE_POOL_SIZE;
import static com.planet.courier.constant.CourierConstant.FILE_NAME;
import static com.planet.courier.constant.CourierConstant.FOLDER_PATH;
import static com.planet.courier.constant.CourierConstant.GRID_SIZE;
import static com.planet.courier.constant.CourierConstant.MAX_POOL_SIZE;
import static com.planet.courier.constant.CourierConstant.QUEUE_POOL_SIZE;

import java.text.ParseException;
import java.util.List;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.support.TaskExecutorJobLauncher;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.repository.support.JobRepositoryFactoryBean;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.UnexpectedInputException;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.batch.item.support.ClassifierCompositeItemWriter;
import org.springframework.batch.support.transaction.ResourcelessTransactionManager;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.task.TaskExecutor;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.transaction.PlatformTransactionManager;

import com.planet.courier.mapper.RecordMappper;
import com.planet.courier.model.Courier;
import com.planet.courier.processor.CourierItemProcessor;
import com.planet.courier.service.CsvResourcePartitioner;
import com.planet.courier.service.FileVerificationSkipper;
import com.planet.courier.service.RecordProcessingSkipper;
import com.planet.courier.writer.ClassifierWriter;

import jakarta.annotation.Resource;

@Configuration
public class CourierBatchConfig {

	private static final Logger logger = LoggerFactory.getLogger(CourierBatchConfig.class);
	
	@Resource
	private ClassifierWriter classifierWriter;
	
	@Resource
	private FileVerificationSkipper fileVerificationSkipper;
	
	@Resource
	private RecordProcessingSkipper processingSkipper;
	
	@Resource
	private CsvResourcePartitioner csvResourcePartitioner;

	@Bean(name = "partitionerJob")
	public Job partitionerJob(JobRepository jobRepository, PlatformTransactionManager transactionManager)
			throws UnexpectedInputException, ParseException {
		return new JobBuilder("partitionerJob", jobRepository).start(partitionStep(jobRepository, transactionManager))
				.build();
	}

	@Bean
	public Step partitionStep(JobRepository jobRepository, PlatformTransactionManager transactionManager)
			throws UnexpectedInputException, ParseException {
		return new StepBuilder("partitionStep", jobRepository).partitioner("slaveStep", csvResourcePartitioner)
				.step(slaveStep(jobRepository, transactionManager)).taskExecutor(taskExecutor()).gridSize(GRID_SIZE)
				.build();
	}

	@Bean
	public Step slaveStep(JobRepository jobRepository, PlatformTransactionManager transactionManager)
			throws UnexpectedInputException, ParseException {
		return new StepBuilder("slaveStep", jobRepository).<Courier, Courier>chunk(CHUNK_SIZE, transactionManager)
				.reader(itemReader(null, null, null))
				.faultTolerant().skipPolicy(fileVerificationSkipper)
				.processor(itemProcessor())
				.faultTolerant()
				.skipPolicy(processingSkipper)
				.writer(classifierCustomerCompositeItemWriter())
				.taskExecutor(taskExecutor()).build();
	}

	@Bean
	@StepScope
	public FlatFileItemReader<Courier> itemReader(
			@Value("#{stepExecutionContext[partition_number]}") final Long partitionNumber,
			@Value("#{stepExecutionContext[first_line]}") final Long firstLine,
			@Value("#{stepExecutionContext[last_line]}") final Long lastLine)
			throws UnexpectedInputException, ParseException {

		logger.info("Partition Number : {}, Reading file from line : {}, to line: {} ", partitionNumber, firstLine,
				lastLine);

		FlatFileItemReader<Courier> reader = new FlatFileItemReader<Courier>();
		DelimitedLineTokenizer tokenizer = new DelimitedLineTokenizer();
		String[] tokens = { "id", "email", "phone_number", "parcel_weight" };
		tokenizer.setNames(tokens);
		DefaultLineMapper<Courier> lineMapper = new DefaultLineMapper<Courier>();
		lineMapper.setLineTokenizer(tokenizer);
		lineMapper.setFieldSetMapper(new RecordMappper());
		reader.setLinesToSkip(Math.toIntExact(firstLine));
		reader.setMaxItemCount(Math.toIntExact(lastLine));
		reader.setResource(new ClassPathResource(FOLDER_PATH.concat(FILE_NAME)));
		reader.setLineMapper(lineMapper);
		return reader;
	}

	@Bean
	public TaskExecutor taskExecutor() {
		ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
		taskExecutor.setMaxPoolSize(MAX_POOL_SIZE);
		taskExecutor.setCorePoolSize(CORE_POOL_SIZE);
		taskExecutor.setQueueCapacity(QUEUE_POOL_SIZE);
		taskExecutor.afterPropertiesSet();
		return taskExecutor;
	}

	@Bean
	public ClassifierCompositeItemWriter<Courier> classifierCustomerCompositeItemWriter() {
		ClassifierCompositeItemWriter compositeItemWriter = new ClassifierCompositeItemWriter();
		compositeItemWriter.setClassifier(classifierWriter);
		return compositeItemWriter;
	}

	@Bean(name = "jobRepository")
	public JobRepository getJobRepository() throws Exception {
		JobRepositoryFactoryBean factory = new JobRepositoryFactoryBean();
		factory.setDataSource(dataSource());
		factory.setTransactionManager(transactionManager());
		factory.afterPropertiesSet();
		return factory.getObject();
	}

	@Bean(name = "dataSource")
	public DataSource dataSource() {
		EmbeddedDatabaseBuilder builder = new EmbeddedDatabaseBuilder();
		return builder.setType(EmbeddedDatabaseType.H2)
				.addScript("classpath:org/springframework/batch/core/schema-drop-h2.sql")
				.addScript("classpath:org/springframework/batch/core/schema-h2.sql")
				.addScript("classpath:courier.sql").build();
	}

	@Bean(name = "transactionManager")
	public PlatformTransactionManager transactionManager() {
		return new ResourcelessTransactionManager();
	}

	@Bean(name = "jobLauncher")
	public JobLauncher getJobLauncher() throws Exception {
		TaskExecutorJobLauncher jobLauncher = new TaskExecutorJobLauncher();
		jobLauncher.setJobRepository(getJobRepository());
		jobLauncher.afterPropertiesSet();
		return jobLauncher;
	}

	@Bean(destroyMethod = "")
	@StepScope
	public JdbcBatchItemWriter<List<Courier>> cameroonWriter() {
		JdbcBatchItemWriter<List<Courier>> cameroonWriter= new JdbcBatchItemWriter<List<Courier>>();
		cameroonWriter.setDataSource(dataSource());
		cameroonWriter.setSql("INSERT INTO CAMEROON (ID, EMAIL, PHONE_NUMBER, PARCEL_WEIGHT) VALUES (:id, :email, :phoneNumber, :parcelWeight)");
		cameroonWriter.setItemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<List<Courier>>());
	    return cameroonWriter;
	}

	@Bean(destroyMethod = "")
	@StepScope
	public JdbcBatchItemWriter<List<Courier>> ethipoiaWriter() {
		JdbcBatchItemWriter<List<Courier>> ethipoiaWriter= new JdbcBatchItemWriter<List<Courier>>();
		ethipoiaWriter.setDataSource(dataSource());
		ethipoiaWriter.setSql("INSERT INTO ETHIOPIA (ID, EMAIL, PHONE_NUMBER, PARCEL_WEIGHT) VALUES (:id, :email, :phoneNumber, :parcelWeight)");
		ethipoiaWriter.setItemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<List<Courier>>());
	    return ethipoiaWriter;
	}

	@Bean(destroyMethod = "")
	@StepScope
	public JdbcBatchItemWriter<List<Courier>> morocooWriter() {
		JdbcBatchItemWriter<List<Courier>> morocooWriter= new JdbcBatchItemWriter<List<Courier>>();
		morocooWriter.setDataSource(dataSource());
		morocooWriter.setSql("INSERT INTO MOROCOO (ID, EMAIL, PHONE_NUMBER, PARCEL_WEIGHT) VALUES (:id, :email, :phoneNumber, :parcelWeight)");
		morocooWriter.setItemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<List<Courier>>());
	    return morocooWriter;
	}

	@Bean( destroyMethod = "")
	@StepScope
	public JdbcBatchItemWriter<List<Courier>> ugandaWriter() {
		JdbcBatchItemWriter<List<Courier>> ugandaWriter= new JdbcBatchItemWriter<List<Courier>>();
		ugandaWriter.setDataSource(dataSource());
		ugandaWriter.setSql("INSERT INTO UGANDA (ID, EMAIL, PHONE_NUMBER, PARCEL_WEIGHT) VALUES (:id, :email, :phoneNumber, :parcelWeight)");
		ugandaWriter.setItemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<List<Courier>>());
	    return ugandaWriter;
	}

	@Bean(destroyMethod = "")
	@StepScope
	public JdbcBatchItemWriter<List<Courier>> mozambiqueWriter() {
		JdbcBatchItemWriter<List<Courier>> mozambiqueWriter= new JdbcBatchItemWriter<List<Courier>>();
		mozambiqueWriter.setDataSource(dataSource());
		mozambiqueWriter.setSql("INSERT INTO MOZAMBIQUE (ID, EMAIL, PHONE_NUMBER, PARCEL_WEIGHT) VALUES (:id, :email, :phoneNumber, :parcelWeight)");
		mozambiqueWriter.setItemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<List<Courier>>());
	    return mozambiqueWriter;
	}

	@Bean(destroyMethod = "")
	@StepScope
	public CourierItemProcessor itemProcessor() {
		return new CourierItemProcessor();
	}
}