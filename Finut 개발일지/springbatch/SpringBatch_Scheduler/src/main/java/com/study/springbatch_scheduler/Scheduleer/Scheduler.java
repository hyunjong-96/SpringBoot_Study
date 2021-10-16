package com.study.springbatch_scheduler.Scheduleer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class Scheduler {

	private static final Logger logger = LoggerFactory.getLogger(Scheduler.class);

	// @Scheduled(cron = "5 * * * * ?")
	@Scheduled(fixedDelay = 5000)
	public void cronJobScheduler() {
		log.info("==========START CRON JOB SCHEDULER==========");
		log.info("5초마다 Schedule 실행");
		logger.info("Current Thread = {}", Thread.currentThread().getName());
		log.info("==========END CRON JOB SCHEDULER==========");
	}

	@Scheduled(cron = "*/1 * * * * *")
	// @Scheduled(fixedDelay = 1000)
	public void cronJobSchedulerPerOneSecond() {
		log.info("==========START CRON JOB SCHEDULER PER ONE_SECOND==========");
		log.info("1초마다 Schedule 실행");
		logger.info("Current Thread = {}", Thread.currentThread().getName());
		log.info("==========END CRON JOB SCHEDULER PER ONE_SECOND==========");
		// System.out.println(new Date().toString());
		;
	}

	@Scheduled(cron = "*/1 * * * * *")
	// @Scheduled(fixedDelay = 1000)
	public void cronJobSchedulerPerOneSecond2() {
		log.info("==========START CRON JOB SCHEDULER PER ONE_SECOND2==========");
		log.info("1초마다 Schedule 실행");
		logger.info("Current Thread = {}", Thread.currentThread().getName());
		log.info("==========END CRON JOB SCHEDULER PER ONE_SECOND2==========");
		// System.out.println(new Date().toString());
		;
	}

	// @Scheduled(fixedDelay = 1000)
	// public void run() {
	// 	log.info("hello1----");
	// }
}
