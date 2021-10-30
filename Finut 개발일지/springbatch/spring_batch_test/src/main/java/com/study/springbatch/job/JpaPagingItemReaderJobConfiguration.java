package com.study.springbatch.job;

import javax.persistence.EntityManagerFactory;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JpaItemWriter;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.batch.item.database.builder.JpaPagingItemReaderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.study.springbatch.pay.Pay;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Configuration
public class JpaPagingItemReaderJobConfiguration {
	private final JobBuilderFactory jobBuilderFactory;
	private final StepBuilderFactory stepBuilderFactory;
	private final EntityManagerFactory entityManagerFactory;

	private int chunkSize = 10;
	private int pageSize = 10;

	@Bean
	public Job jpaPagingItemReaderJob(){
		return jobBuilderFactory.get("jpaPagingItemReaderJob")
			.start(jpaPagingItemReaderStep())
			.build();
	}

	@Bean
	@JobScope
	public Step jpaPagingItemReaderStep(){
		return stepBuilderFactory.get("jpaPagingItemReaderStep")
			.<Pay, Pay>chunk(chunkSize)
			.reader(jpaPagingItemReader())
			.processor(payPagingProcessor())
			.writer(jpaPagingItemWriter())
			.build();
	}

	@Bean
	@StepScope
	public JpaPagingItemReader<Pay> jpaPagingItemReader(){
		JpaPagingItemReader<Pay> reader = new JpaPagingItemReader<Pay>(){
			@Override
			public int getPage(){
				return 0;
			}
		};

		reader.setQueryString("SELECT p FROM Pay p WHERE p.success = false");
		reader.setPageSize(pageSize);
		reader.setEntityManagerFactory(entityManagerFactory);
		reader.setName("jpaPagingItemReader");

		return reader;
		// return new JpaPagingItemReaderBuilder<Pay>()
		// 	.name("jpaPagingItemReader")
		// 	.entityManagerFactory(entityManagerFactory)
		// 	.pageSize(chunkSize)
		// 	.queryString("SELECT p FROM Pay p WHERE p.success = false")
		// 	.build();
	}

	@Bean
	@StepScope
	public ItemProcessor<Pay, Pay> payPagingProcessor(){
		return item -> {
			item.success();
			return item;
		};
	}

	@Bean
	@StepScope
	public JpaItemWriter<Pay> jpaPagingItemWriter(){
		JpaItemWriter<Pay> writer = new JpaItemWriter<>();
		writer.setEntityManagerFactory(entityManagerFactory);
		return writer;
	}

	private ItemWriter<Pay> jpaPagingItemLogWriter(){
		return list->{
			for(Pay pay : list){
				log.info("Current Pay = {}",pay);
			}
		};
	}
}
