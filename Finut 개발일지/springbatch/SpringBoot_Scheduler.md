# Spring Scheulder



## 1-1 Scheduling

스케줄링은 특정 기간 동안 작업을 실행하는 프로세스이다.

Spring에서 지원하는 Scheduler를 편리하게 사용할수있다.



## 1-2 Scheduler사용하기

```java
@SpringBootApplication
@EnableScheduling
public class SpringBatchSchedulerApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringBatchSchedulerApplication.class, args);
	}

} 
```



```java
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
	public void cronJobSchedulerPerOneSecond() {
		log.info("==========START CRON JOB SCHEDULER PER ONE_SECOND==========");
		log.info("1초마다 Schedule 실행");
		logger.info("Current Thread = {}", Thread.currentThread().getName());
		log.info("==========END CRON JOB SCHEDULER PER ONE_SECOND==========");
		;
	}

	@Scheduled(cron = "*/1 * * * * *")
	public void cronJobSchedulerPerOneSecond2() {
		log.info("==========START CRON JOB SCHEDULER PER ONE_SECOND2==========");
		log.info("1초마다 Schedule 실행");
		logger.info("Current Thread = {}", Thread.currentThread().getName());
		log.info("==========END CRON JOB SCHEDULER PER ONE_SECOND2==========");
		// System.out.println(new Date().toString());
		;
	}
}

```

- `Scheduler`를 사용해주기위해 Bean으로 등록
- `@Scheduled`의 Cron옵션을 통해서 shcduler를 실행시켜줄 시간을 설정해준다.
  - 앞에서부터 `초, 분, 시, 일, 월, 요일`, 년 으로 `*`으로 표현시 해당 시간이 될떄마다 실행시켜주는 것이다
  - 만약 초에 `*`표현시 매초마다 스케줄러를 실행시켜주는것.

![image](https://user-images.githubusercontent.com/57162257/137587547-b5f3a60f-e32a-45b8-b2c7-b07cffea8c42.png)

실행시켜주면 매 초마다, 5초마다 스케줄러를 실행시켜주는것을 확인해줄수 있다.

근데 맨처음에 5초 마다 실행시켜준 스케줄러가 먼저 실행시켜준다..?



스케줄러의 시간을 설정해줄때 다른방법으로는 `@Schedule`에 `fixedDelay`라는 옵션을 사용해주는것이다 `천단위 1초`이다.

```java
	@Scheduled(fixedDelay = 1000)
	public void run() {
	 	log.info("hello1----");
	}
```



## 1-2 multi thread

위 스케줄러의 실행결과에서 보면 로그로 `Thread.currentTreahd().getName()`을 보여준 것을 보면 모두 `scheduling-1`이라고 적혀있다. 이것의 뜻은 이 스케줄러에 사용된 `thread`가 동일한` thread`로 작동되었다는것을 확인할수있다.

여기서 잠깐!

여기서 `@Scheduled`작업은 Spring에서 생성된 하나의 `Thread Pool`에서 실행된다. 그렇기 떄문에 하나의 `스케줄러`가 돌고 있다면 그 thread를 이용한 `스케줄러`가 끝나야 다음 `스케줄러가` 실행될수 있다는 것이다.

그렇게 된다면 여러개의 `스케줄러`가 있을때 하나의 `thread`를 사용하게 된다면은 엄청나게 시간이 오래걸리게 될것이다.

그러면 thread갯수를 늘리면 되지 않는가?

그렇다 그렇기 떄문에 `Thread Pool`에서 사용 가능한 `thread`갯수를 늘려보자.

```java
@Configuration
public class SchedulerConfiguration implements SchedulingConfigurer {
	private final int POOL_SIZE = 10;

	@Override
	public void configureTasks(ScheduledTaskRegistrar scheduledTaskRegistrar) {
		ThreadPoolTaskScheduler threadPoolTaskScheduler = new ThreadPoolTaskScheduler();

		threadPoolTaskScheduler.setPoolSize(POOL_SIZE);
		threadPoolTaskScheduler.setThreadNamePrefix("my-scheduled-task-pool-");
		threadPoolTaskScheduler.initialize();

		scheduledTaskRegistrar.setTaskScheduler(threadPoolTaskScheduler);
	}
}
```

위 코드를 통해 `Thread Pool`의 크기를 지정해주고 사용해줄수 있는 thread의 크기를 늘려보자.

`POOL_SIZE`가 사용가능한 `thread의 갯수`이다.

![image](https://user-images.githubusercontent.com/57162257/137587489-a4d792c8-0b57-4864-8c71-60cf8073b779.png)

위의 사진을 보면 아까전에는 `scheduling-1`이였는데, `Thread.currentThread().getName()`의 로그를 확인해보면 `my-scheduled-task-pool-[넘버]`인것을 확인할수 있다. 

즉, 스케줄러를 사용함에 있어 하나의 `thread`를 사용한것이 아니라. `POOL_SIZE`로 설정해준만큼의 `thread`를 스케줄러를 돌릴때 사용할수 있다는것이다.
