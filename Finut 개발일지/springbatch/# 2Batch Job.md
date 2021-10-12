# #2 Batch Job



## 2. Job과 Step

![image](https://user-images.githubusercontent.com/57162257/136757039-92fb2d75-63c0-4ce2-ac68-f952e1f172c5.png)

Sppring Batch에서 Job은 **하나의 배치 작업 단위**이다.

Job안에는 **여러개의 Step**이 있고 Step안에는 **Tasklet 또는 Reader &  Processor & Writer**묶음이 존재한다.

Step안의 Tasklet과 Reader &  Processor & Writer는 한 묶음이 같은 레벨이기 때문에 Reader &  Processor & Writer가 끝나고 Tasklet으로 마무리 짓는 등으로 만들수 없다.

Tasklet은 Spring MVC의 `@Component`, `@Bean`과 비슷한 역할이라고 생각하면 된다고 한다.



## 2-1 Job 생성하기

![image](https://user-images.githubusercontent.com/57162257/136758055-a02598ad-f80e-48c7-8b40-3bdd6284bb8e.png)

SpringBatchApplication.java에 Spring Batch기능 활성화 어노테이션 `@EnableBatchProcessing을 활성화 시킨다`.(**필수**)



```java
//SimpleJobConfiguration.java
@Slf4j // log 사용을 위한 lombok 어노테이션
@RequiredArgsConstructor // 생성자 DI를 위한 lombok 어노테이션
@Configuration
public class SimpleJobConfiguration {
    private final JobBuilderFactory jobBuilderFactory; // 생성자 DI 받음
    private final StepBuilderFactory stepBuilderFactory; // 생성자 DI 받음

    @Bean
    public Job simpleJob() {
        return jobBuilderFactory.get("simpleJob")
                .start(simpleStep1())
                .build();
    }

    @Bean
    public Step simpleStep1() {
        return stepBuilderFactory.get("simpleStep1")
                .tasklet((contribution, chunkContext) -> {
                    log.info(">>>>> This is Step1");
                    return RepeatStatus.FINISHED;
                })
                .build();
    }
}
```

- **@Configuration**
  Spring Batch의 모든 Job은 `@Configuration`으로 등록해서 사용.
- **jobBuilderFactory.get("simpleJob")**
  - simpleJob이란 이름으로 `Batch Job`생성
- **stepBuilderFactory.get("simpleStep1")**
  - simpleStep1이란 이름의 `Batch Step`생성.
- **.tasklet((contribution, chunkContext))**
  - Step안에서 수행될 기능들을 명시
  - Tasklet은 `Step안에서 단일로 수행될 커스텀한 기능들을 선언`할 때 사용.



이렇게 생성했으면 실행을 해보자.

![image](https://user-images.githubusercontent.com/57162257/136760645-dc1b97a2-1995-4a82-a506-c6ec018ebaa2.png)





## 2-2 MySQL환경에서 Spring Batch 실행시키기

Spring Batch에선 **메타 데이터** 테이블들이 필요하다.

메타 데이터란, **데이터를 설명하는 데이터**이다.

### 메타데이터의 종류

- 기술용 메타데이터
  - 정보자원의 검색을 목적으로 한 메타데이터.
- 관리용 메타데이터
  - 자원의 관리를 어떻게든 용이하게 하기 위한 메타데이터
- 구조용 메타데이터
  - 복합적인 디지털 객체들을 함께 묶어주기 위한 메타데이터

### Spring Batch의 메타 데이터

- 이전에 실행한 Job이 어떤 것들이 있는지
- 최근 실패한 Batch Parameter가 어떤것들이 있고, 성공한 Job은 어떤것들이 있는지
- 다시 실행한다면 어디서 부터 시작하면 되는지
- 어떤 Job에 어떤 Step들이 있었고, Step들 중 성공한 Step과 실패한 Step들은 어떤것들이 있는지

### 메타 테이블 구조

![image](https://user-images.githubusercontent.com/57162257/136761698-0b2584d2-dd16-4a2e-8767-b4cd25606cbe.png)

이 테이블들이 있어야만 Spring Batch가 정상 작동한다.

특히 H2 DB를 사용한다면 어플리케이션을 실행할때 자동으로 생성하지만, MySQL과 같은 DB는 테이블을 개발자가 직접 생성해야한다.

mysql을 메타 테이블 데이터 없이 실행시킨다면

![image](https://user-images.githubusercontent.com/57162257/136894251-cf7ee315-d3e0-414d-ac19-43153cd4fd3c.png)

이렇게 `batch_job_instance`가 존재하지 않아 실행이 되지않는다.

이럴때는 

![image](https://user-images.githubusercontent.com/57162257/136894517-04b099af-809a-4ec3-be11-c32bdea13076.png)

Schema-mysql.sql 파일을 찾은뒤

![image](https://user-images.githubusercontent.com/57162257/136894650-d51b8fb0-ea6a-439b-bc94-4b50da4c0f03.png)

이 파일에 있는 스키마를 모두 복사해서 로컬 MySQL에서 실행하면

![image](https://user-images.githubusercontent.com/57162257/136894730-79721d94-85b2-4c32-b127-972712357ee9.png)

![image](https://user-images.githubusercontent.com/57162257/136895072-ef2bf119-9f74-4872-b300-ed525d7c36ce.png)

실행 된것을 확인할수있다.



## 참고

https://jojoldu.tistory.com/325?category=902551