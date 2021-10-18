package com.study.springbatch_scheduler.job;

import javax.persistence.EntityManagerFactory;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.database.JpaItemWriter;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.batch.item.database.builder.JpaPagingItemReaderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.study.springbatch_scheduler.pay.Pay;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Configuration
public class SpringBatchWithSchedulerJobConfiguration {
	private final JobBuilderFactory jobBuilderFactory;
	private final StepBuilderFactory stepBuilderFactory;
	private final EntityManagerFactory entityManagerFactory;

	private static final int chunkSize = 5;

	@Bean
	public Job springBatchWithSchedulerJob(){
		return jobBuilderFactory.get("springBatchWithSchedulerJob")
			.start(springBatchWithSchedulerStep())
			.build();
	}

	@Bean
	public Step springBatchWithSchedulerStep(){
		return stepBuilderFactory.get("springBatchWithSchedulerStep")
			.<Pay, Pay>chunk(chunkSize)
			.reader(springBatchWithSchedulerReader())
			.processor(springBatchWIthSchedulerProcessor())
			.writer(springBatchWithSchedulerWriter())
			.build();
	}

	@Bean
	public JpaPagingItemReader<Pay> springBatchWithSchedulerReader(){
		log.info("========== SchedulerReader ==========");
		return new JpaPagingItemReaderBuilder<Pay>()
			.name("springBatchWithSchedulerReader")
			.entityManagerFactory(entityManagerFactory)
			.pageSize(chunkSize)
			.queryString("SELECT p FROM Pay p WHERE id < 8")
			.build();
	}

	@Bean
	public ItemProcessor<Pay, Pay> springBatchWIthSchedulerProcessor(){
		return item -> {
			log.info("========== item id : {} ==========",item.getId());
			item.plusCount(10);
			return item;
		};
	}

	@Bean
	public JpaItemWriter<Pay> springBatchWithSchedulerWriter(){
		log.info("~~~~~~~~~~ Scheduler Writer ~~~~~~~~~~");
		JpaItemWriter<Pay> jpaItemWriter = new JpaItemWriter<>();
		jpaItemWriter.setEntityManagerFactory(entityManagerFactory);
		return jpaItemWriter;
	}
}
