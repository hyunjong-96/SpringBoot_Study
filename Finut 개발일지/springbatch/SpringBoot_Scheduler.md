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



## 1-3 @Async

스케줄러를 구현함에 있어서 스케줄러에서 작동하는 기능이 스케줄러의 시간단위 보다 오래 걸릴 가능성은 없지않다.

예를 들어

```java
@Scheduld("1 * * * * * ")
public void timeOutScheduler(){
  Thread.sleep(6000);
}
```

스케줄러는 1초마다 작동이 되는데, timeOutScheduler는 6초의 딜레이를 가지게 되는 코드이다.

코드를 실행시켜본다면 

![image](https://user-images.githubusercontent.com/57162257/137625868-80410796-c074-4023-8dc6-12e042e82a05.png)

timeOutScheduler스케줄러는 6초마다 로그가 찍히는 것을 확인할수 있다. 이렇게 되면 지금 코드는 단순하지만 복잡한 비즈니스 로직을 가지고 있는 코드라면 코드가 완전히 값을 반환하는데 6초나 딜레이가 되는것이라. 많은 양의 자원을 잡아먹게 되는것이다.

그헣기 떄문에 사용할수 있는것이 `@Asnyc` `비동기` 이다.

> 비동기를 쉽게 설명하자만 아침에 우리 어머니들이 출근을 해야하는데 출근 하는 과정에서 해야하는 것들이 씻기, 청소, 아침밥하기, 옷입기 라고할때 하나하나 다 해결한다음 다음것을 해결하면서 출근을 하게 되면 분명 지각을 할것이다.
>
> 하지만 아침밥을 하면서 씻고, 청소하고, 옷까지 입는것처럼 한꺼번에 일을 하게되면 출근시간을 훨씬 빨라지는것처럼.

코드에서도 하나의 메소드가 일을 마치지 못했더라도 `Thread`를 새로 할당해줘서 다른일을 할수 있도록 해준다.

코드에 비동기 기능을 사용하기 위해서는 `@Asnyc`어노테이션을 사용해주어야 하는데, 이 어노테이션을 사용하기 위해서는 메인 어플리케이션 코드에서 스케줄러와 같이 `@EnableAsync`어노테이션을 추가해준다.

![image](https://user-images.githubusercontent.com/57162257/137629187-530bf7bd-8eb0-409e-b8d1-b28103a2e364.png)

Spring Boot에서 비동기 기능을 활성화 시키기고 thread pool설정을 하기 위해서는 `AsyncConfig.java`를 통해 설정을 해줘야한다.

![image](https://user-images.githubusercontent.com/57162257/137629601-0e85e03b-8601-46df-9dea-d6f84a8d2d0b.png)

- `@EnableAsnyc` : spring의 메소드의 비동기 기능을 활성화 해준다
- `ThreadPoolTaskExecutor`로 비동기로 호출하는 Thread에 대한 설정을 한다.
  - `CorePoolSize` : 기본적으로 실행을 대기하고 있는 `Thread`의 갯수
    - 1초마다 스케줄러를 실행시키는 테스트를 해보니까 `corePoolSize`로 설정해놓은 만큼 한번에 실행되었다.
  - `MaxPoolSize` : 동시에 시작하는,  최대 Thread갯수
    - 여러 스케줄러를 실행시킬때 사용가능한 Thread갯수 제한인거 같다.
  - `QueueCapacity` : `MaxPollSize`를 초과하는 요청이 Thread생성 요청시 해당 내용을 `Queue`에 저장하게 되고, 사용할 수 있는 Thread여유 자리가 발생하면 하나씩 꺼내져서 동작하게 된다.
  - `ThreadNamePrefix` : Spring이 생성하는 쓰레드의 접두사를 지정한다.
    ![image](https://user-images.githubusercontent.com/57162257/137629920-3691d6e1-c292-4d0f-95e2-dd415714621f.png)



그런다음 5초마다 하나 1초마다 하나씩 작동하는 스케줄러를 만들고 1초마다 작동하는 스케줄러에 비동기를 적용해서 6초 뒤에 로그를 발생시키는 메소드도 만들어서 돌려보았다.

![image](https://user-images.githubusercontent.com/57162257/137629941-fd65f259-0cf7-48b3-866f-6e04578dcb54.png)

![image](https://user-images.githubusercontent.com/57162257/137629034-56bbcb92-ff04-4ad1-9cf0-11e31cece1e7.png)

결과를 확인해보면 1초마다 `CorePoolSize`로 설정해놓은만큼 한번에 스케줄러가 실행되고 1초마다 실행되는 스케줄러가 6초뒤에 로그를 발생시켰을때 사용하고 있는 Thread를 확인해보면 동일한 Thread를 사용하는 것을 확인할수있다.

성공



## 참고

https://www.callicoder.com/spring-boot-task-scheduling-with-scheduled-annotation/

https://m.blog.naver.com/PostView.naver?isHttpsRedirect=true&blogId=dg110&logNo=221589812687

https://ayoteralab.tistory.com/entry/Spring-Boot-08-Scheduler

http://jmlim.github.io/spring/2018/11/27/spring-boot-schedule/

https://www.hanumoka.net/2020/07/02/springBoot-20200702-sringboot-async-service/
