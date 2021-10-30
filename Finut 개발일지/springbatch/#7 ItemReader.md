# #6 ItemReader



## 7-1 ItemReader이란

![image](https://user-images.githubusercontent.com/57162257/137275981-8e752ce9-54bc-48de-ac97-0b5619abd1a7.png)

Spring Batch의 Chunk Tasklet은 위와 같은 과정을 거치게 된다.

Spring Batch의 ItemReader는 데이터(DB, File, XML, JSON)들을 읽어드린다.

![image](https://user-images.githubusercontent.com/57162257/137276353-7abba87e-b626-4474-bb20-45ae90459e5a.png)

나는 JPA를 사용하니 JPA에서 가장 많이 사용하는 JpaPaginItemReader의 다이어그램이다.

JpaPagingItemReader에서 가장 최상위의 인터페이스는 ItemReader와 ItemStream이다.

![image](https://user-images.githubusercontent.com/57162257/137276846-bdcf51b6-0bc0-4429-b24f-3cfe16a2f961.png)

ItemReader에는 read()메소드만 존재하는데 ItemStream은 열고, 닫고, 업데이트하고 많은 일을한다. 이건 무슨 역할인고 하고 보니 배치 프로세스의 실행 컨텍스트와 연계해서 ItemReader의 상태를 저장하고 실패한 곳에서 다시 실행할 수 있게 해주는 역할이다.

> (그래서 앞에 정리할때 jojoldu님께서 보여주신 itemReader에서 반환받을 객체로 타입을 지정하지 않으면 stream으로 변환되지 못해 null을 받는 에러를 받을때, 꼭 stream으로 변경해주어야 데이터를 받을수 있는것이. entityFactory를 통해 데이터를 읽어올때 실패했을때 그곳에서 다시 실행할수 있도록 하기 위함일까? 라는 초보개발자의 뇌피셜..?)



## 7-2 Database Reader

보통은 DB에서 데이터를 가져오겠지? 하고 일단 바로 구현을 해야하는 피쳐가 있으니 핵심만 집고 넘어가기로 했다.

고맙게도 생각보다 어렵지않게 `Spring Batch`에서 바로 Database에서 데이터를 읽어올수있도록 잘 만들어놔주었었다.

Database에서 데이터를 읽어올때 2가지 방법이있다. `Cursor`와 `Paging`이 있다.

![image](https://user-images.githubusercontent.com/57162257/137278014-339d21ba-7b23-40db-ac97-3e6fce6fb574.png)

위의 그림을 보면 이해가 된다.

`Cursor`방법은 한번에 데이터를 요청하는 쿼리를 보내서 한번에 받아내고,

`Paging`방식은 `Pagination`과 같이 `pageSize`를 이용해 잘라서 데이터를 가져오는 식이다(그림에서는 10 Row라고 나와있는데 이는, PaginItemReader에서 정해둔 default pageSize가 10이기 떄문이다)

---------

나는 전체 회원을 JPA를 통해 가져와야하므로 `JpaPagingItemReader`를 사용해보겠다.

```java
//JpaPaginItemRaderJobConfiguration
@Slf4j
@RequiredArgsConstructor
@Configuration
public class JpaPagingItemReaderJobConfiguration {
	private final JobBuilderFactory jobBuilderFactory;
	private final StepBuilderFactory stepBuilderFactory;
	private final EntityManagerFactory entityManagerFactory;

	private int chunkSize = 10;

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
			.writer(jpaPagingItemLogWriter())
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
		reader.setPageSize(chunkSize);
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
```

--------------------

```java
//JpaPgingItemReaderStep
	@Bean
	@JobScope
	public Step jpaPagingItemReaderStep(){
		return stepBuilderFactory.get("jpaPagingItemReaderStep")
			.<Pay, Pay>chunk(chunkSize)
			.reader(jpaPagingItemReader())
			.processor(payPagingProcessor())
			.writer(jpaPagingItemLogWriter())
			.build();
	}
```

- chunk
  - <Pay,Pay>chunk에서 **첫번째 파라미터는 Reader에서 반환할 타입**
  - **두번쨰 파라미터는 Writer에서 파라미터로 넘어올 타입**
  - 매개변수로 넣는 chunkSize는 chunk의 단위 갯수.

--------

```java
//JpaPaginItemReader
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
		reader.setPageSize(chunkSize);
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
```

- setQueryString("[쿼리문]")
  - entityManager를 통해 데이터를 가져올 쿼리문
- setPageSize([한 페이지에 가져올 데이터 갯수])
  - 매개변수에 한 페이지에 가져올 데이터 갯수를 넣어준다
  - pagenationd의 limit같은것
- setEntityManagerFactory(entityManagerFactory)
  - DI해준 entityManagerFactory를 넣어줌
- setName("[itemReader이름]")
  - Bean의 이름이 아닌Spring Batch의 ExecutionContext에 저장되어질 이름

#### *쿼리 문을 작성할때 WHERE(조건문)을 작성하지 않으면 무한으로 불러온다..(아직 해결못함)

밑에 주석처리 해둔것처럼 builder를 이용해 읽을수 있지만 JpaPaginItemReader 객체를 생성해서 사용한 이유는

나중에 process를 통해 데이터를 가공한뒤 write할때 paging을 통해 read하면서 이슈가 생기기 때문이다.

![image](https://user-images.githubusercontent.com/57162257/137281655-9b284857-117e-4cf2-9660-72f3f8703422.png)

![image](https://user-images.githubusercontent.com/57162257/137281700-1c966820-786c-4082-a23b-fa97ce5a7bae.png)

위의 사진처럼 30개의 데이터를 10개씩 paging 할때 첫번째 10개의 데이터는 offset0, limit10 무사히 가져와 successStatus를 변경시켜줬다.

하지만 두번째 paging 할때는 offset 11, limit10이 될것이다. 그래서 11~20까지의 데이터를 가져올거라 생각했지만, 21~30번째의 데이터를 가져와서 수정을 해버리는 것이다. 

그렇기 때문에 이 문제의 해결책으로 **`JpaPagingItemReader`의 offset을 가져오는 getPage()를 항상 0을 반환하게 구현**해서 두번째, 세번째 paging을 할때도 offset 0, limit 10이 되도록 해주면 모든 데이터를 잘 paging해줄수 있게 되기떄문이다.

--------------------

```java
//PayPaginProcessor
	@Bean
	@StepScope
	public ItemProcessor<Pay, Pay> payPagingProcessor(){
		return item -> {
			item.success();
			return item;
		};
	}
```

payPagingProcessor에서는 Pay Entity의 `success`필드를 true로 변경시켜주는 `success()`함수를 실행시켜준다 (데이터 가공)

-----------------

```java
//JpaPaginItemLogWriter
	private ItemWriter<Pay> jpaPagingItemLogWriter(){
		return list->{
			for(Pay pay : list){
				log.info("Current Pay = {}",pay);
			}
		};
	}
```

ItemWriter는 다음에 정리할텐데, 쉽게 얘기해서 캐시에 모인 쿼리들을 다시 반환해주는것(?)

jpaPagingItemLogWriter에서 payPagingProcessor를 통해 변경해준 pay의 log를 확인해보면 

![image](https://user-images.githubusercontent.com/57162257/137275709-96ed390b-318c-4a72-a0fd-fe0292049652.png)

![image](https://user-images.githubusercontent.com/57162257/137275801-3837c1b7-9330-4ba7-8bce-88c3842b1386.png)

변경은 잘되었다**(`where success=false`쿼리를 확인하기 위해 id=1,19번 행은 true로 해놨음)**

근데 데이터베이스를 확인해보면

![image](https://user-images.githubusercontent.com/57162257/137283628-5fe7ccfd-9fda-40e3-bbeb-94fdca38d424.png)

분명 id=12 ~ 18번까지의 행이 success=true인 로그을 확인했는데 변경이 안되어있었다.

확인해보니 reader는 JpaPagingItemReader인데 writer는 ItemWriter였다.

```java
//JpaPaginItemWriter
	@Bean
	@StepScope
	public JpaItemWriter<Pay> jpaPagingItemWriter(){
		JpaItemWriter<Pay> writer = new JpaItemWriter<>();
		writer.setEntityManagerFactory(entityManagerFactory);
		return writer;
	}
```

writer를 JpaPagingItemWriter로 변경해주고 실행시키니

![image](https://user-images.githubusercontent.com/57162257/137275298-5299b251-c6dc-43e3-9e3b-e86a07abd10a.png)

![image](https://user-images.githubusercontent.com/57162257/137284406-379b2266-af3a-4127-9fa1-a89c1fe65c12.png)

아주 잘 실행되었다.(이것땜에 한시간을 날린...)



## 7-3 JpaPagingItemReader주의점

**정렬(Order)가 무조건 포함되어 있어야 한다는점이다.**

paging을 하니 각각의 쿼리가 실행된다

```sql
select o 
from ShopOrder o join fetch o.customer c 
where c.id=1
limit 시작포인트, CHUNK_SIZE
```

```sql
select o 
from ShopOrder o join fetch o.customer c 
where c.id=1
limit 0, 10000
```

```sql
select o 
from ShopOrder o join fetch o.customer c 
where c.id=1
limit 10000, 10000
```

```sql
select o 
from ShopOrder o join fetch o.customer c 
where c.id=1
limit 10000, 10000
```

이런식으로 각각 실행되기 때문에 정렬 기준이 정해져 있지않으면 쿼리마다 본인들만의 정렬기준을 만들어 실행하게 된다고한다.

그러다보니 앞에서 **조회했던 데이터가 다음 조회 쿼리의 정렬기준에 포함되기도하고 빠지기도 해서 데이터를 가공할때 오류가 발생하기 떄문이다.**



### 해결책

1. `Order by`
   - `order by id`와 같이 고유한 정렬기준을 포함시키는방법
2. `CursorItemReader`
   - 굳이 정렬기준을 포함시키지 않는다면 CursorItemReader를 통해 이슈를 해결할수 있다고한다.

난 `order by`를 사용.



## 참조

https://jojoldu.tistory.com/336?category=902551

https://jojoldu.tistory.com/166

https://jojoldu.tistory.com/146