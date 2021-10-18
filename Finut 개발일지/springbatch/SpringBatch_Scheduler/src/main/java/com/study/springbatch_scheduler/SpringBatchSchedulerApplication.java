package com.study.springbatch_scheduler;

import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableAsync
@EnableScheduling
@EnableBatchProcessing
@SpringBootApplication
public class SpringBatchSchedulerApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringBatchSchedulerApplication.class, args);
	}

}
