package com.planet.courier.config;

import static com.planet.courier.constant.CourierConstant.CHUNK_SIZE;
import static com.planet.courier.constant.CourierConstant.CORE_POOL_SIZE;
import static com.planet.courier.constant.CourierConstant.GRID_SIZE;
import static com.planet.courier.constant.CourierConstant.MAX_POOL_SIZE;
import static com.planet.courier.constant.CourierConstant.QUEUE_POOL_SIZE;

import java.text.ParseException;

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
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.batch.item.support.ClassifierCompositeItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.task.TaskExecutor;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.transaction.PlatformTransactionManager;

import com.planet.courier.mapper.RecordMappper;
import com.planet.courier.model.Courier;
import com.planet.courier.processor.CourierProcessor;
import com.planet.courier.service.CsvResourcePartitioner;
import com.planet.courier.service.FileVerificationSkipper;
import com.planet.courier.service.RecordProcessingSkipper;
import com.planet.courier.writer.ClassifierWriter;

@Configuration
public class CourierBatchConfig {

	private static final Logger logger = LoggerFactory.getLogger(CourierBatchConfig.class);

	@Autowired
	private ClassifierWriter classifierWriter;

	@Autowired
	private FileVerificationSkipper fileVerificationSkipper;

	@Autowired
	private RecordProcessingSkipper processingSkipper;

	@Autowired
	private DataSource datasource;

	@Bean(name = "partitionerJob")
	public Job partitionerJob(JobRepository jobRepository, PlatformTransactionManager transactionManager)
			throws UnexpectedInputException, ParseException {
		return new JobBuilder("partitionerJob", jobRepository).start(partitionStep(jobRepository, transactionManager))
				.build();
	}

	@Bean
	public Step partitionStep(JobRepository jobRepository, PlatformTransactionManager transactionManager)
			throws UnexpectedInputException, ParseException {
		return new StepBuilder("partitionStep", jobRepository).partitioner("slaveStep", csvResourcePartitioner(null))
				.step(slaveStep(jobRepository, transactionManager)).taskExecutor(taskExecutor()).gridSize(GRID_SIZE)
				.build();
	}

	@Bean
	public Step slaveStep(JobRepository jobRepository, PlatformTransactionManager transactionManager)
			throws UnexpectedInputException, ParseException {
		return new StepBuilder("slaveStep", jobRepository).<Courier, Courier>chunk(CHUNK_SIZE, transactionManager)
				.reader(itemReader(null, null, null, null)).faultTolerant().skipPolicy(fileVerificationSkipper)
				.processor(itemProcessor()).faultTolerant().skipPolicy(processingSkipper)
				.writer(classifierCompositeItemWriter()).taskExecutor(taskExecutor()).build();
	}

	@Bean
	@StepScope
	public FlatFileItemReader<Courier> itemReader(
			@Value("#{jobParameters['input.file.name']}") String resource,
			@Value("#{stepExecutionContext[partition_number]}") final Long partitionNumber,
			@Value("#{stepExecutionContext[first_line]}") final Long firstLine,
			@Value("#{stepExecutionContext[last_line]}") final Long lastLine)
			throws UnexpectedInputException, ParseException {
		logger.info("Reading input from source : {}", resource);
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
		reader.setResource(new ClassPathResource(resource));
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
	public ClassifierCompositeItemWriter<Courier> classifierCompositeItemWriter() {
		ClassifierCompositeItemWriter<Courier> compositeItemWriter = new ClassifierCompositeItemWriter<Courier>();
		compositeItemWriter.setClassifier(classifierWriter);
		return compositeItemWriter;
	}

	@Bean(name = "jobRepository")
	public JobRepository getJobRepository() throws Exception {
		JobRepositoryFactoryBean factory = new JobRepositoryFactoryBean();
		factory.setDataSource(datasource);
		factory.setTransactionManager(jpaTransactionManager());
		factory.afterPropertiesSet();
		return factory.getObject();
	}
	
	@Bean(name = "transactionManager")
	@Primary
	public JpaTransactionManager jpaTransactionManager() {
        final JpaTransactionManager tm = new JpaTransactionManager();
        tm.setDataSource(datasource);
        return tm;
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
	public CourierProcessor itemProcessor() {
		return new CourierProcessor();
	}
	
	@Bean(destroyMethod = "")
	@StepScope
	public CsvResourcePartitioner csvResourcePartitioner(@Value("#{jobParameters['input.file.name']}") String resource) {
		return new CsvResourcePartitioner(resource);
	}
}