package com.study.springbatch.job;

import java.util.List;

import javax.persistence.EntityManagerFactory;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JpaItemWriter;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.batch.item.database.builder.JpaPagingItemReaderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.study.springbatch.pay.Pay;
import com.study.springbatch.pay.Pay2;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Configuration
public class CustomItemWriterJobConfiguration {
	private final JobBuilderFactory jobBuilderFactory;
	private final StepBuilderFactory stepBuilderFactory;
	private final EntityManagerFactory entityManagerFactory;

	private static final int chunkSize = 5;

	@Bean
	public Job customItemWriterJob(){
		return jobBuilderFactory.get("customItemWriterJob")
			.start(customItemWriterStep())
			.build();
	}

	@Bean
	public Step customItemWriterStep(){
		return stepBuilderFactory.get("customItemWriterStep")
			.<Pay, Pay2>chunk(chunkSize)
			.reader(customItemWriterReader())
			.processor(customItemWriterProcessor())
			.writer(customItemWriter())
			.build();
	}

	@Bean
	public JpaPagingItemReader<Pay> customItemWriterReader(){
		return new JpaPagingItemReaderBuilder<Pay>()
			.name("customItemWriterReader")
			.entityManagerFactory(entityManagerFactory)
			.pageSize(chunkSize)
			.queryString("SELECT p FROM Pay p")
			.build();
	}

	@Bean
	public ItemProcessor<Pay,Pay2> customItemWriterProcessor(){
		return pay -> new Pay2(pay.getAmount(), pay.getTxDateTime(), pay.getSuccess());
	}

	@Bean
	public ItemWriter<Pay2> customItemWriter(){
		return items -> {
			for(Pay2 item : items){
				log.info("customItemWriter item : {}",item);
			}
		};
	}
}
