package com.planet.courier.config;

import static com.planet.courier.constant.CourierConstant.FILE_NAME;
import static com.planet.courier.constant.CourierConstant.FOLDER_PATH;
import static com.planet.courier.constant.CourierTestConstant.INCORRECT_DATA_FILE_NAME;

import java.nio.file.Path;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.batch.test.JobRepositoryTestUtils;
import org.springframework.batch.test.context.SpringBatchTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import com.planet.courier.CourierApplication;

@SpringBatchTest
@SpringJUnitConfig({ CourierApplication.class, CourierBatchConfig.class })
public class CourierBatchConfigTest {

	private static final Path INPUT_DIRECTORY = Path.of(FOLDER_PATH.concat(FILE_NAME));
	private static final Path INCORRECT_DATA_DIRECTORY = Path.of(FOLDER_PATH.concat(INCORRECT_DATA_FILE_NAME));

	@Autowired
	private JobLauncherTestUtils jobLauncherTestUtils;

	@Autowired
	private JobRepositoryTestUtils jobRepositoryTestUtils;

	@Autowired
	private JdbcTemplate jdbcTemplate;

	@BeforeEach
	void setUp() {
		jobRepositoryTestUtils.removeJobExecutions();
	}

	@AfterEach
	void tearDown() {
	}

	@Test
	@DisplayName("GIVEN a directory with valid file WHEN jobLaunched THEN records persisted into DB")
	void shouldReadFromFileAndPersistIntoDataBaseAndMoveToProcessedDirectory() throws Exception {

		// WHEN
		var jobParameters = new JobParametersBuilder().addString("input.file.name", INPUT_DIRECTORY.toString())
				.toJobParameters();

		JobExecution jobExecution = jobLauncherTestUtils.launchJob(jobParameters);

		// THEN
		Assertions.assertEquals(ExitStatus.COMPLETED, jobExecution.getExitStatus());

		Integer totalRowsInsertedForCameroon = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM cameroon",
				Integer.class);
		Assertions.assertEquals(1, totalRowsInsertedForCameroon);

		Integer totalRowsInsertedForUganda = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM uganda", Integer.class);
		Assertions.assertEquals(2, totalRowsInsertedForUganda);
	}

	@Test
	@DisplayName("GIVEN a directory with invalid files WHEN jobLaunched THEN exit status if FAILED and file is moved into failed directory")
	void shouldFailWhenInputFileContainsInvalidData() throws Exception {

		var jobParameters = new JobParametersBuilder().addString("input.file.name", INCORRECT_DATA_DIRECTORY.toString())
				.toJobParameters();

		JobExecution jobExecution = jobLauncherTestUtils.launchJob(jobParameters);

		// WHEN
		boolean fromFileToDatabaseFailed = jobExecution.getStepExecutions().stream()
				.filter(stepExecution -> "partitionStep".equalsIgnoreCase(stepExecution.getStepName()))
				.anyMatch(stepExecution -> ExitStatus.FAILED.getExitCode()
						.equalsIgnoreCase(stepExecution.getExitStatus().getExitCode()));

		Assertions.assertTrue(fromFileToDatabaseFailed);
		Assertions.assertEquals(ExitStatus.FAILED.getExitCode(), jobExecution.getExitStatus().getExitCode());

		Integer totalRowsForCameroon = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM cameroon", Integer.class);
		Assertions.assertEquals(1, totalRowsForCameroon);
		
		Integer totalRowsForUganda = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM uganda", Integer.class);
		Assertions.assertEquals(2, totalRowsForUganda);
	}
}