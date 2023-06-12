package com.planet.courier;

import static com.planet.courier.constant.CourierConstant.FILE_NAME;
import static com.planet.courier.constant.CourierConstant.FOLDER_PATH;
import static com.planet.courier.util.CourierUtil.setParamsByFileSize;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableBatchProcessing
@EnableAutoConfiguration
public class CourierApplication implements CommandLineRunner {

	private static final Logger LOGGER = LoggerFactory.getLogger(CourierApplication.class);
	
	@Autowired
	private JobLauncher jobLauncher;
	
	@Autowired
	private Job partitionerJob;
	
	public static void main(String[] args) {
		//Intialize job params according to csv file size.
		setParamsByFileSize();
		SpringApplication.run(CourierApplication.class, args);
	}
	

	@Override
	public void run(String... args) throws Exception {
		
		LOGGER.info("Starting the batch job");
		try {
			JobParametersBuilder jobParametersBuilder =new JobParametersBuilder();
			jobParametersBuilder.addString("input.file.name", FOLDER_PATH.concat(FILE_NAME));
			final JobExecution execution = jobLauncher.run(partitionerJob, jobParametersBuilder.toJobParameters());
			LOGGER.info("Job Status : {}", execution.getStatus());
		} catch (final Exception e) {
			e.printStackTrace();
			LOGGER.error("Job failed {}", e.getMessage());
		}
	}
}
