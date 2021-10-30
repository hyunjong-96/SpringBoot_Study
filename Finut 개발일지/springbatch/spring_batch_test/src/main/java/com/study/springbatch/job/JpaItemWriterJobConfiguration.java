package com.study.springbatch.job;

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

import com.study.springbatch.pay.Pay;
import com.study.springbatch.pay.Pay2;
import com.study.springbatch.pay.PlusCount;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Configuration
public class JpaItemWriterJobConfiguration {
	private final JobBuilderFactory jobBuilderFactory;
	private final StepBuilderFactory stepBuilderFactory;
	private final EntityManagerFactory entityManagerFactory;

	private static final int chunkSize = 5;

	@Bean
	public Job jpaItemWriterJob(){
		return jobBuilderFactory.get("jpaItemWriterJob")
			.start(jpaItemWriterStep())
			.build();
	}

	@Bean
	public Step jpaItemWriterStep(){
		return stepBuilderFactory.get("jpaItemWriterStep")
			.<Pay, Pay2>chunk(chunkSize)
			.reader(jpaItemWriterReader())
			.processor(jpaItemProcessor())
			.writer(jpaItemWriter())
			.build();
	}

	@Bean
	public JpaPagingItemReader<Pay> jpaItemWriterReader(){
		JpaPagingItemReader<Pay> reader = new JpaPagingItemReader<Pay>(){
			@Override
			public int getPage(){
				return 0;
			}
		};

		reader.setName("jpaItemWriterReader");
		reader.setEntityManagerFactory(entityManagerFactory);
		reader.setPageSize(chunkSize);
		reader.setQueryString("SELECT p FROM Pay p where p.success = false");

		return reader;
	}

	@Bean
	public ItemProcessor<Pay, Pay2> jpaItemProcessor(){
		return pay -> new Pay2(pay.getAmount(), pay.getTxDateTime(), pay.getSuccess());
	}


	@Bean
	public JpaItemWriter<Pay2> jpaItemWriter(){
		JpaItemWriter<Pay2> jpaItemWriter = new JpaItemWriter<>();
		jpaItemWriter.setEntityManagerFactory(entityManagerFactory);
		return jpaItemWriter;
	}
}
