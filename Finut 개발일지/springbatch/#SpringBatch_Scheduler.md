# # SpringBatch_Scheduler

회사에서 개발해야할 기능이 한달에 한번씩 회원의 수업 정보를 데이터 가공처리를 통해 분석해야 했기때문에, SpringBatch를 공부했었고, 이제 한달에 한번씩 실행할수 있는 스케줄러를 공부했었다.

이제 이 두개의 기능을 합치기만 하면된다.

테스트 내용은 Pay엔티티에 있는 row들중 id 1~7까지의 row들만 count필드에 job이 실행될때마다 10식 더해주는 것으로 구현해봤다.



[SpringBatchWithSchedulerJobConfiguration.java]

```java
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
```

job을 위의 코드처럼 구현해놓고 scheduler를 구현한다.

```java
@Slf4j
@RequiredArgsConstructor
@Component
public class Scheduler {

	private static final Logger logger = LoggerFactory.getLogger(Scheduler.class);
	private final Job job;
	private final JobLauncher jobLauncher;

	@Scheduled(cron = "*/10 * * * * *")
	@Async
	public void cronJobScheduler(){
		log.info("!!!!! Cron Job Scheduler !!!!!");
		try{
			JobParameters jobParameters = new JobParametersBuilder()
				.addLong("time", System.currentTimeMillis())
					.toJobParameters();

			jobLauncher.run(job, jobParameters);
		}catch(Exception e){
			log.info(e.getMessage());
		}
	}
}

```

- 매 10초마다 스케줄러를 실행시켜주도록 cron을 이용
- job이 처리를 하는데 오래걸릴수도 있기때문에 비동기를 위한 @Async이용
- job은 동일한 jobParameter를 실행시킬수 없기때문에 jobParameter에 현재 시간을 long으로 넣어서 동일한 jobParameter로 job을 실행시키지 않도록 해준다.



![image](https://user-images.githubusercontent.com/57162257/137666200-83b75de0-b075-433e-8a18-ab2d50eaf3d8.png)

1~7까지의 row들이 잘 더해진게 확인되었다.

성공



## *이슈

기능적인 문제는 없다고 생각했다. 근데 스케줄러를 돌렸을때 `All steps already completed or no steps configured for this job.`에러가 하나 나있었다.

![image](https://user-images.githubusercontent.com/57162257/137666792-e2c6d768-4c4e-4f5a-89ae-490bfcc8e035.png)

그래서 여러번 spring batch를 실행시켜봤는데, 맨 처음에 실행시킬때 스케줄러에서 실행시키는 `springBatchWithSchedulerJob`과 그냥 프로그램을 실행시켰을때 동작하는 `springBatchWithSchedulerJob`이 Step이 겹쳐서 실행되서 발생하는 에러인거같다.

에러 코드를 확인해보니 id가 42인 step이 이미 사용했다고 떴으니 메타테이블을 확인해봤다.

**[BATCH_STEP_EXECUTION]**

![image](https://user-images.githubusercontent.com/57162257/137677873-c39de83f-2ec2-4233-ab3a-474e9ea6dc9a.png)

**[BATCH_JOB_EXECUTION]**

![image-20211018151137043](/Users/flab1/Library/Application Support/typora-user-images/image-20211018151137043.png)

**[BATCH_JOB_EXECUTION_PARAMS]**

![image](https://user-images.githubusercontent.com/57162257/137678292-2eeb9b88-35d5-47b7-aeb3-918df87b0308.png)

에러를 발생시키는 Step을 찾아서 쭉 따라와보니 Job을 실행시킨 Params에는 Job id가 없었다.

그렇다는것은 jobParameter없이 실행시킨건데 params를 확인해보니 Job id가 32인 job이 파라미터 없이 작동된것의 처음으로 기록이 되어있었다.

즉, 파라미터 없이 job을 작동시키는것은 job을 작동시킬수없다. 결국 애플리케이션을 실행되었지만 해당 Job은 정상적으로 수행되지 않았기 떄문에 발생한것이고, 스케줄러를 통해 `System.currentTimeMillis()`을 파라미터로 받은 Job들은 수행이 된것이다.