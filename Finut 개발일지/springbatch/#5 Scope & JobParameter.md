# #5 Scope & Job Parameter

SpringBatch에서 가장 중요하다고 말할수 있는 Spring Batch의 Scope라고 한다.

`Scope`란 `@StepScope`와` @JobScope`를 이야기한다.



## 5-1. JobParameter와 Scope

Spring Batch에서 외부 혹은 내부에서 파라미터를 받아 Batch컴포넌트에 사용할수 있게 지원하는데, 이 파라미터를 `Job Parameter`라고 한다.(메타 테이블 공부할때 잠깐 맛보았었다.)

이 `Job Parameter`를 사용하기 위해서는 항상 Spring Batch전용 Scope를 선언해야하는데 위 에서 얘기한 `@StepScope`와` @JobScope`가 있다.

사용법은 SpEL로 선언해서 사용하면된다.

```java
@Value("#{jobParameters[파라미터명]}")
```



```java
@Slf4j
@RequiredArgsConstructor
@Configuration
public class ScopeJobConfiguration {
	private final JobBuilderFactory jobBuilderFactory;
	private final StepBuilderFactory stepBuilderFactory;
	
	@Bean
	public Job scopeJob(){
		return jobBuilderFactory.get("scopeJob")
			.start(scopeStep1(null))
			.next(scopeStep2())
			.build();
	}
	
  //JobScope
	@Bean
	@JobScope
	public Step scopeStep1(@Value("#{jobParameters[requestData]}")String requestData){
		return stepBuilderFactory.get("scopeStep1")
			.tasklet(((contribution, chunkContext) -> {
				log.info(">>>>> This is scopeStep1");
				log.info(">>>>> requestData = {}",requestData);
				return RepeatStatus.FINISHED;
			}))
			.build();
	}
	
	@Bean
	public Step scopeStep2(){
		return stepBuilderFactory.get("scopeStep2")
			.tasklet(scopeStep2Tasklet(null))
			.build();
	}
	
  //StepScope
	@Bean
	@StepScope
	public Tasklet scopeStep2Tasklet(@Value("#{jobParameters[requestData]}") String requestData){
		return ((contribution, chunkContext) -> {
			log.info(">>>>> This is scopeStep2");
			log.info(">>>>> requestData = {}", requestData);
			return RepeatStatus.FINISHED;
		});
	}
}

```

위의 코드에서  jobParameter를 받을때 각각 Scope를 사용해주었는데.

`Step선언문`에서는 `@JobScope`를, `Tasklet`이나 `ItemReader, ItemWriter, ItemProcessor`에서는` @StepScope`를 사용했다.

현재 jobParameter에 사용가능한 타입은 `Double, Long, Date, String` 이고 `LocalDate`와 `LocalDateTime`은 지원이 되지 않는다.

그리고 **Step을 호출하는 부분에서 파라미터를 null로 보내주고있는데, 이는 jobParameter의 할당이 어플리케이션 실행시에 하지 않기 떄문이다.**



## 5-2. @StepScope & @JobScope

Spring Batch는 `@StepScope`와 `@JobScpe`라는 특별한 Bean Scope를 지원한다.

보통 `Spring Bean`의 기본 Scope는 `singletone`인데` Spring Batch컴포넌트(Tasklet, ItemReader, ItemWriter, ItemProcessor 등)` 에 `@StepScope`나 `@JobScope`를 사용하게 되면 Spring 컨테이너에 각각 Step의 실행시점과 Job의 실행시점에 해당 컴포넌트를 `Spring Bean`으로 생성한다.

**즉, Bean의 생성시점을 지정된 Scope가 실행되는 시점으로 지연시키는것이다.**

비슷한 예로 MVC의 request scope와 비슷한데. request scope가 request가 왔을때 생성되고 response를 반환하면 삭제되는 것과 유사하다.

마찬가지로 job scope와 step scope도 job이 실행되고 끝날때, step이 실행되고 끝날때 Bean으로 생성되고 삭제된다.



### * job과 step Bean의 생성시점을 어플리케이션 실행시점이 아닌, step혹은 job의 실행시점으로 지연시키는것에 대한 장점

- JobParameter의 Late Binding이 가능하다.
  - 어플리케이션이 실행되는 시점이아니라, **Controller나 Service와 같은 비즈니스 로직 처리단계에서 JobParameter를 할당해줄수있다.**
- 동일한 컴포넌트를 병렬 혹은 동시에 사용할때 유용하다.
  - Step안에 tasklet이 있고, 이 tasklet이 멤버 변수와 멤버변수를 변경하는 로직이 있다고 한다.
    @StepScope없이 Step을 병렬로 실행시키면 서로 다른 Step에서 하나의 Tasklet을 두고 상태를 막 변경할수 있다.
    하지만 **@StepScope가 있다면 각각의 Step에서 별도의 Tasklet을 생성하고 관리하기 때문에 서로의 상태를 침범할수 없다.**



## 5-3 JobParameter

JobParameter는 `@JobScope`나 `@StepScope`가 선언된 메소드나 클래스에서 만 `@Value("#{jobParameters[파라미터명]}")`를 받을수 있다.

@JobScope나 @StepScope를 선언해주지 않고 singletone으로 Bean을 생성할 경우 `'jobParameters' canoot be found` 에러가 발생한다.



## 5-4 JobParameter vs 시스템 변수

### 시스템변수

- 시스템 변수로 사용하려면 `java jar application.jar -D파라미터`로 지정해서 사용해야한다.

- 시스템 변수를 사용하게 되면 Spring Batch의 Job Parameter관련 기능을 사용하지 못하게된다.
  (같은 JobParameter로 같은 Job을 두번 실행할수 없지만, 시스템 변수 사용시 이 기능이 작동하지 않게 된다.)

  Job Parameter를 사용하지 못한다는것은 `Late Binding`을 못하게 된다는 것이다.

### JobParameter

```java
@RequiredArgsConstructor
@RestController
public class JobLauncherController{
  private final JobLauncher jobLauncher;
  private final Job job;
  
  @GetMapping("/launchjob")
  public String handle(@RequestParam(value="fileName") String fileName) throws Exception{
    
    try{
      JobParameters jobParameters = new JobParametersBuilder()	//1
        .addString("input.file.name",fileName)
        .addLong("time", System.currentTimeMillis())
        .toJobParameters();
      
      jobJauncher.run(job, jobParameters);	//2
    }catch(Exception e){
      log.info(e.getMessage());
    }
    return "Done";
  }
}
```

1. RequestParam으로 받은 값으로 Job Parameter를 생성.
2. Job Parameter로 Job을 수행.

즉 이렇게 MVC의 비즈니스 로직 구현 부분에서 JobParameter를 생성하고 Job을 수행할수 있다.

**하지만 웹 서버에서 Batch를 관리하는 것은 권장하지 않는다고한다.**



## 5-5 주의사항

![image](https://user-images.githubusercontent.com/57162257/137237824-26a8c0a9-593b-4757-820b-9efc0bd28a30.png)

위의 코드에서 @Bean과 @StepScope, @JobScope를 함께 사용한다면 에러가 발생한다고한다.

-------

jojoldus님의 코드를 빌려쓰자면 (https://github.com/jojoldu/blog-code)

![image](https://user-images.githubusercontent.com/57162257/137238755-d387b9c7-141d-4b50-a7db-86d7bba1035d.png)

![image-20211014111047336](/Users/flab1/Library/Application Support/typora-user-images/image-20211014111047336.png)

![image](https://user-images.githubusercontent.com/57162257/137239364-bb60bd25-61b9-4d0e-bc76-03ea761c7b59.png)

![image](https://user-images.githubusercontent.com/57162257/137238888-db6c2fbd-57ed-4f08-8dad-f95e1b3e557b.png)

reader()에서 itemReader를 통해 데이터를 가져오는 로직을 구현했을때 `@StepScope의 proxyMode = scopedProxyMode.TARGET_CLASS`로 인해서 ItemReader 인터페이스의 프록시 객체를 생성하여 리턴한다고 한다.

하지만 ItemReader의 인터페이스에는 read()메소드 밖에 없고 ItemStream인터페이스에서 stream을 open/close하는 메소드를 가지고 있기때문에 프록시 객체를 가진 reader()가 ItemReader타입이지만, ItemStream타입이 아니기 때문에 reader()가 stream으로 등록되지 못한것이였다고 한다.

stream으로 등록되지 못해서 null이 발생한 이유는

**EntityManagerFactory에서 EntityManager를 생성하는 것은 등록된 stream이 진행하는데 stream이 없다보니 open메소드를 수행하지못하고 EntityManager가 생성되지 않아 null point exceptin이 발생한거였다고 한다.**



### 해결책

![image](https://user-images.githubusercontent.com/57162257/137239551-3fa53bf9-d1af-4e6e-aa34-7be4041fa75b.png)

**Reader()의 리턴 타입을 구현체의 타입을 직접 사용하면 된다고한다.**

