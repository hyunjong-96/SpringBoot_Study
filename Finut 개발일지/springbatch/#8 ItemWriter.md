# #8 ItemWriter



`ChunkOrientedTasklet`을 구성하는 3요소에는 `Reader`,` Processor`, `Writer`가 있다.

그중 `Reader`와 `Writer`는 `ChunkOrientedTasklet`의 필수요소이다.(`Processor`는 필수사항이 아니다.)



## 8-1 ItemWriter소개

ItemWriter는 Spring Batch에서 사용하는 **출력** 기능

앞에 공부했던 `ItemReader`는 각 **item단위**로 하나씩 읽지만 `ItemWriter`는 **Chunk단위**로 묶인 `item List`를 다루게된다.

![image](https://user-images.githubusercontent.com/57162257/137424086-a6e8d854-774b-4ed2-9330-7ac38636bcf5.png)

1. ItemReader를 통해 각 데이터를 개별적으로 읽고 이를 가공하기 위해 ItemProcessor에 전달
2. 가공되는 프로세스는 Chunk의 item개수 만큼 처리
3. Chunk단위 만큼 처리가 완료된다면 Write로 전달되어 Writer에 명시되어있는대로 일괄처리.

위 그림은 하나의 Chunk(단위)의 흐름을 보여준 그림.



## 8-2 Database Writer

Spring Batch에서는 JDBC와 ORM 모두 Writer를 제공한다.

`Writer`는 **Chunk단위의 마지막**이므로 **영속성관련해서는 항상 마지막에 Flush를 해줘야한다.**

![image](https://user-images.githubusercontent.com/57162257/137424928-dd270ff9-fcbd-4974-b7ac-dfe90e8f315e.png)

- JpaItemWriter의 write

- 위 사진에서도 보면 매개변수로 item을 list로 받는다.

  

## 8-3 JpaItemWriter

`JpaItemWriter`는 ORM을 사용할수 있는 Writer라고 한다.

```java
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
```

- `JpaItemWriterJob`에서 `ItemProcessor`를 통해 `Pay2`인 새로운 엔티티를 만들어주고 반환해준다.
  - `ItemProcessor<Pay,Pay2>`에서 첫번째 파라미터는 `Reader`의 반환 타입
  - 두번째 파라미터는 `Writer`에 들어가는 매개변수 타입
- JpaItemWriter
  - `JpaPagingItemReader`를 사용했다면 `JpaItemWriter`를 사용해줘야한다.
    - Processor를 통해 가공한 데이터를 다시 write해줄때 pageSize에 딱맞지 않는 데이터는 변환되지 않는 이슈가 있다.
    - 나는 `JpaPagingItemReader`를 사용했는데 `ItemWriter`를 사용했는데 그런 이슈가 있었다.
  - `JpaItemWriter`는 JPA를 사용하기 떄문에 영속성 관리를 위해 `EntityManager`를 할당해줘야한다.
    - `Spring-boot-starter-data-jpa`를 의존성에 등록하면 Entity Manager가 Bean으로 자동생성되어 DI코드만 추가해주면 된다
    - DI로 추가한 entityManager만 set해주면된다.



## 8-4 CustomItemWriter

- Reader에서 읽어온 데이터를 RestTemplate으로 외부 API로 전달해야할때
- 임시저장을 하고 비교하기 위해 싱글톤 객체에 값을 넣어야할때
- 여러 Entity를 동시에 save해야할때

등과 같은 여러가지 상황에 Custom Writer를 구현해야할 때가 있다. 이럴땐 ItemWriter인터페이스를 구현하면된다.

```java
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
		return new ItemWriter<Pay2>(){
			@Override
			public void write(List<? extends Pay2> items)throws Exception{
				for(Pay2 item : items){
					log.info("customItemWriter item : {}",item);
				}
			}
		};
	}
}
```

ItemWriter의 write메소드를 override 해서 커스텀해주면된다.

참고로 JpaItemWriter에서 ItemWriter의 write를 구현한 코드는

![image](https://user-images.githubusercontent.com/57162257/137441590-44c8ddef-962c-48eb-9188-be0fa4bab285.png)

![image](https://user-images.githubusercontent.com/57162257/137441689-9911e7a4-ac25-4eca-a96a-ced9a919c826.png)

요거당.

## 참고

https://jojoldu.tistory.com/339?category=902551