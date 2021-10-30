package com.study.springbatch_scheduler.quartz;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Component
public class QuartzBatchJob extends QuartzJobBean {
	private final Job job;
	private final JobLauncher jobLauncher;

	@Async
	@Override
	protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
		log.info("========== QuartzBatchJob ==========");
		try {
			startBatchJob();
		} catch (Exception e) {
			log.info(e.getMessage());
			log.info("QuartzBatchJob 종료");
		}
	}

	public void startBatchJob() throws
		JobInstanceAlreadyCompleteException,
		JobExecutionAlreadyRunningException,
		JobParametersInvalidException,
		JobRestartException {
		JobParameters jobParameters = new JobParametersBuilder()
			.addLong("time", System.currentTimeMillis())
			.toJobParameters();

		jobLauncher.run(job, jobParameters);
	}
}
