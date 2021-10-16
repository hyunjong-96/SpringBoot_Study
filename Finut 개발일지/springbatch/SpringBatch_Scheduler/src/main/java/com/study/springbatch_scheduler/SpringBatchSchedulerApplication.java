package com.study.springbatch_scheduler;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class SpringBatchSchedulerApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringBatchSchedulerApplication.class, args);
	}

}
