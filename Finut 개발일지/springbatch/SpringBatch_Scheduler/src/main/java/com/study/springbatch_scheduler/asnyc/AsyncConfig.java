package com.study.springbatch_scheduler.asnyc;

import java.util.concurrent.Executor;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurerSupport;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
@EnableAsync
public class AsyncConfig extends AsyncConfigurerSupport {
	@Override
	public Executor getAsyncExecutor() {
		ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
		executor.setCorePoolSize(5);	//기본적으로 대기하고 있는 Thread의 갯수
		executor.setMaxPoolSize(10);	//동시 동작하는 최대 Thread 수
		executor.setQueueCapacity(500);	//MaxPoolSize를 초과하는 요청하는 Thread생성 요청시 해당 내용을 Queue에 저장하고, 사용할수 있는 Thread 여유자리가 발생하면 하나씩 꺼내서 동작하게됨
		executor.setThreadNamePrefix("hanumoka-async-"); //spring이 생성하면 Thread의 접두사
		executor.initialize();
		return executor;
	}
}
