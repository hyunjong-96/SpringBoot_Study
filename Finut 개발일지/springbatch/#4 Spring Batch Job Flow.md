# #4 Spring Batch Job Flow

Spring Batch에는 Job이 있고 Job을 구성하는데 Step이 있다. **Step은 실제 Batch작업을 수행**하는 역할을 한다.

실제로 Batch 비지니스 로직을 처리하는 (ex : log.info()) 기능은 Step에 구현이 되어있다. 즉, **Step에서는 Batch로 실제 처리하고자 하는 기능과 설정을 모두포함하는 장소**라고 생각하면 된다.

그렇기 때문에 **Step들간의 순서 혹은 흐름을 제어할 필요**가 있다.



## 4-1. Next

```java
@Slf4j
@RequiredArgsConstructor
@Configuration
public class StepNextJobConfiguration {
	private final JobBuilderFactory jobBuilderFactory;
	private final StepBuilderFactory stepBuilderFactory;

	@Bean
	public Job stepNextJob(){
		return jobBuilderFactory.get("stepNextJob")
			.start(step1())
			.next(step2())
			.next(step3())
			.build();

	}

	@Bean
	public Step step1(){
		return stepBuilderFactory.get("step1")
			.tasklet(((contribution, chunkContext) -> {
				log.info(">>>>> This is Step1");
				return RepeatStatus.FINISHED;
			}))
			.build();
	}

	@Bean
	public Step step2(){
		return stepBuilderFactory.get("step2")
			.tasklet(((contribution, chunkContext) -> {
				log.info(">>>>> This is Step2");
				return RepeatStatus.FINISHED;
			}))
			.build();
	}

	@Bean
	public Step step3(){
		return stepBuilderFactory.get("step3")
			.tasklet(((contribution, chunkContext) -> {
				log.info(">>>>> This is Step3");
				return RepeatStatus.FINISHED;
			}))
			.build();
	}
}

```

![image](https://user-images.githubusercontent.com/57162257/136904253-9e1ad6ba-725b-47a9-887d-4ca9ad310caa.png)

job을 생성해주고 첫번째 step을 실행시키고 next()를 사용하면 자연스럽게 next에 선언해준 step으로 흐름을 제어해줄수있다.



*여기서 보이지는 않지만 이 전에 수행했던 simpleJob도 함께 실행이 되었다. 그렇기 떄문에 지정한 Batch만 수행되도록 설정을 해야한다.

![image](https://user-images.githubusercontent.com/57162257/136904723-e70f51db-17fd-444c-8dc3-33fcd0386c09.png)

`yml`에서 batch를 실행시켜줄 job을 설정해주는데 Spring Batch가 실행될때, `Program arguments`로 `job.name`값이 넘어오면 해당 값과 일치하는 Job만 실행하는 설정이다.

`${job.name:NONE}`에서 `job.name` 과 `NONE` 사이에 `:`가 있는데 이 코드의 뜻은 `job.name`이 있으면 `job.name`값을 할당하고, 없으면 `NONE`을 할당하겠다는 것이다.(**NONE을 할당하게 되면 어떤 배치도 실행시키지 않겠다는 뜻이다.**)

**즉, 값이 없을때 모든 배치가 실행되지 않도록 막는 역할을 하는 설정이다.**

IDE의 실행 환경에서는

![image](https://user-images.githubusercontent.com/57162257/136905661-d641ac8a-6d1b-44ab-a7d2-3161e97d5229.png)

`--job.name = [실행시킬 job 이름]`으로 job.name을 설정해줄수 있고 실행시키고

![image](https://user-images.githubusercontent.com/57162257/136905523-07ed680c-e7f2-462d-880f-a0066ee39f35.png)

위의 BATCH_JOB_INSTANCE를 확인해보면 `빨간색`은 job.name 설정전, 파란색은 job.name 설정후의 결과이다.

성공적으로 job.name으로 설정해준 stepNextJob만 실행된것을 확인할수 있다.

> 실제 운영 환경에서는 `java -jar batch-application.jar --job.name=simpleJob`과 같이 배치를 실행한다고 한다.



## 4-2. 조건별 흐름 제어(Flow)

Next가 순차적으로 Step의 순서를 제어한다면, 조건에 따라 Step의 순서를 제어할수도 있어야한다.

예를 들어 StepA를 실행하다가 오류가 발생하면 StepC로, 그렇지 않고 성공한다면 StepB로 수행해야한다.

```java
@Slf4j
@RequiredArgsConstructor
@Configuration
public class StepNextConditionalJobConfiguration {

	private final JobBuilderFactory jobBuilderFactory;
	private final StepBuilderFactory stepBuilderFactory;

	@Bean
	public Job stepNextConditionalJob(){
		return jobBuilderFactory.get("stepNextConditionalJob")
			.start(conditionalJobStep1())
				.on("FAILED")
				.to(conditionalJobStep3())
				.on("*")
				.end()
			.from(conditionalJobStep1())
				.on("*")
				.to(conditionalJobStep2())
				.next(conditionalJobStep3())
				.on("*")
				.end()
			.end()
			.build();
	}

	@Bean
	public Step conditionalJobStep1(){
		return stepBuilderFactory.get("step1")
			.tasklet(((contribution, chunkContext) -> {
				log.info(">>>>> This is stepNextConditionalJob Step1");
				contribution.setExitStatus(ExitStatus.FAILED);
				return RepeatStatus.FINISHED;
			}))
			.build();
	}

	@Bean
	public Step conditionalJobStep2(){
		return stepBuilderFactory.get("conditionalJobStep2")
			.tasklet(((contribution, chunkContext) -> {
				log.info(">>>>> This is stepNextConditionalJob Step2");
				return RepeatStatus.FINISHED;
			}))
			.build();
	}

	@Bean
	public Step conditionalJobStep3() {
		return stepBuilderFactory.get("conditionalJobStep3")
			.tasklet((contribution, chunkContext) -> {
				log.info(">>>>> This is stepNextConditionalJob Step3");
				return RepeatStatus.FINISHED;
			})
			.build();
	}
}
```

위 코드의 시나리오

- step1 실패 시나리오 : step1 -> step3
- step1 성공 시나리오 : step1 -> step2 -> step3

이 Job Flow를 관리하는 코드는 stepNextConditionalJob이다.

![image](https://user-images.githubusercontent.com/57162257/136908492-a5f14742-6dab-4dea-ae8b-1b6f3990a2c2.png)

- on
  - 캐치할 `ExitStatus`지정
  - `*`일 경우 모든 `ExitStatus`가 지정
- to
  - 다음으로 이동할 `Step`지정
- end
  - end는 FolwBuilder를 반환하는 end와 FlowBuilder를 종료하는 end 두개가 있다.
  - 3번째 depth에 있는 `on("*")` 다음에 있는 `end()`는 FolwBuilder를 반환하는 end
  - 두번째 depth에 있는 `build()`앞에 있는 `end()`는 FlowBuilder를 종료하는 end
- from
  - 일종의 이벤트 리스너
  - 상태값을 보고 일치하는 상태라면 `to()`에 포함된 `step`을 호출
  - 앞의 `start()`에서 `FAILED`상태에 대한 flow를 수행했기때문에 `from()`에서의 `on("*")`은 `FAILED`상태를 제외한 상태일때 `to()`를 통해 `step`을 호출하게된다.

stepNextConditionalJob에서 flow는 conditionalJobStep1에서 에러를 발생시켰기 떄문에 step1과 step3가 수행된다.

![image](https://user-images.githubusercontent.com/57162257/136909952-5ca20b63-1a24-499b-a98c-f20dc0486ecb.png)



## * Batch Status vs Exit Status

`Batch Status`와 `Exit Status`의 차이를 아는것이 중요하다고 하기 때문에 한번 짚고 넘어가려고 한다.

Batch Status는 Job 또는 Step의 실행 결과를 Spring에서 기록할 때 사용하는 Enum이다.

![image](https://user-images.githubusercontent.com/57162257/136910428-bd43bb40-a7b7-4a7e-a967-9870ab6f8750.png)

확인해보면 `CONTINUABLE`과 `FINUSHED`로 구성되어있다.

위의 Job Flow에서 `on("FAILED")`메소드가 참조하는 것은 Batch Stauts가 아니라 실제 참조되는 것은 `Step`의 `ExitStauts`이다

step1에서 exitStatus를 설정해줬다. `contribution.setExitStatus(ExitStatus.FAILED)`

`ExitStatus`는 **`Step`의 실행 후 상태**를 얘기한다.

![image](https://user-images.githubusercontent.com/57162257/136911118-050feeea-9bab-47e3-a4ed-0261e2c7cb1e.png)

위의 설명으로 `.start(conditionalStep1()).on("FAILED").to(stepB())`를 풀이해보자면 `conditionalStep1`을 수행했을때 `step`의 `상태(exitCode)`가` FAILED`일때 `stepB`를 수행하라는 뜻이다.

Spring Batch는 기본적으로 **ExitStatus의 exitCode는 Step의 BatchStatus와 같도록 설정**되어있다.
하지만 `A job instance already exists and is complete for parameters={~~}`예외 발생시에는 Status는 `COMPLETE`, ExitStatus는 `NOOP`이 였었다.



## 4-3. Decide

Step의 결과에 따라 서로 다른 Step으로 이동하는 방법

![image](https://user-images.githubusercontent.com/57162257/136908492-a5f14742-6dab-4dea-ae8b-1b6f3990a2c2.png)

위의 코드 처럼 분기 처리를 하는 방식은 2가지 문제가 있다고한다.

- Step이 담당하는 역할이 2가지 이상.
  - 실제 해당 Step이 처리해야할 로직외에도 분기처리를 시키기 위해 ExitStatus조작이 필요하다.
- 다양한 분기 로직 처리의 어려움.
  - ExitStatus를 커스텀하게 고치기 위해선 Listener를 생성하고 Job Flow에 등록하는 등 번거로움이 존재한다.

Step들간의 Flow분기만 담당하면서 다양한 분기처리가 가능한 타입인 `JobExecutionDecider`가 있다고한다.

```java
@Slf4j
@RequiredArgsConstructor
@Configuration
public class DeciderJobConfiguration {
	private final JobBuilderFactory jobBuilderFactory;
	private final StepBuilderFactory stepBuilderFactory;

	@Bean
	public Job deciderJob(){
		return jobBuilderFactory.get("deciderJob")
			.start(startStep())
			.next(decider())
			.from(decider())
				.on("ODD")
				.to(oddStep())
			.from(decider())
				.on("EVEN")
				.to(evenStep())
			.end()
			.build();
	}

	@Bean
	public Step startStep() {
		return stepBuilderFactory.get("startStep")
			.tasklet((contribution, chunkContext) -> {
				log.info(">>>>> Start!");
				return RepeatStatus.FINISHED;
			})
			.build();
	}

	@Bean
	public Step evenStep() {
		return stepBuilderFactory.get("evenStep")
			.tasklet((contribution, chunkContext) -> {
				log.info(">>>>> 짝수입니다.");
				return RepeatStatus.FINISHED;
			})
			.build();
	}

	@Bean
	public Step oddStep() {
		return stepBuilderFactory.get("oddStep")
			.tasklet((contribution, chunkContext) -> {
				log.info(">>>>> 홀수입니다.");
				return RepeatStatus.FINISHED;
			})
			.build();
	}

	@Bean
	public JobExecutionDecider decider() {
		return new OddDecider();
	}

	public static class OddDecider implements JobExecutionDecider {

		@Override
		public FlowExecutionStatus decide(JobExecution jobExecution, StepExecution stepExecution) {
			Random rand = new Random();

			int randomNumber = rand.nextInt(50) + 1;
			log.info("랜덤숫자: {}", randomNumber);

			if(randomNumber % 2 == 0) {
				return new FlowExecutionStatus("EVEN");
			} else {
				return new FlowExecutionStatus("ODD");
			}
		}
	}
}
```

```java
//DeciderJob
@Bean
	public Job deciderJob(){
		return jobBuilderFactory.get("deciderJob")
			.start(startStep())	//startStep 실행
			.next(decider())	//decider 실행(짝수 | 홀수 구분)
			.from(decider())	//decider의 상태가
				.on("ODD")	//ODD라면
				.to(oddStep())	//oddStep 실행
			.from(decider()) //decider의 상태가
				.on("EVEN")	//EVEN이라면
				.to(evenStep())	//evenStep 실행
			.end() // builder종료
			.build();
	}
```

위에 Job Flow를 위해 next, from, on, to, end 메소드를 사용해서 분기처리하여 step을 실행시켜준다.

```java
//Decider
@Bean
	public JobExecutionDecider decider() {
		return new OddDecider();
	}

	public static class OddDecider implements JobExecutionDecider {

		@Override
		public FlowExecutionStatus decide(JobExecution jobExecution, StepExecution stepExecution) {
			Random rand = new Random();

			int randomNumber = rand.nextInt(50) + 1;
			log.info("랜덤숫자: {}", randomNumber);

			if(randomNumber % 2 == 0) {
				return new FlowExecutionStatus("EVEN");
			} else {
				return new FlowExecutionStatus("ODD");
			}
		}
	}
```

Job Flow의 분기처리를 위한 타입을 로직을 `decider`메소드를 통해 step의 할일을 줄여줄수있다.

**핵심은 `JobExecutionDecider`를 구현한 `OddDecider`이다.**

`JobExecutionDecider`인터페이스를 구현한 `OddDecider`를 통해서 여기서는 랜덤하게 숫자를 생성해서 `홀/짝` 여부를 통해 서로 다른 상태를 반환해준다.

이때 여기서는 `Step`으로 처리하는 것이 아니기 때문에 `ExitStatus`를 다뤄주는것이 아닌 `FlowExecutionStatus`로 상태를 관리하게 된다.

아래는 각 상태를 수행한 결과의 로그이다.

![image](https://user-images.githubusercontent.com/57162257/136924976-4f359471-fc7a-4ed4-b547-466ee7bfa07f.png)

![image](https://user-images.githubusercontent.com/57162257/136925589-1fd871ad-d0ec-478f-a3e5-b5d8e544053c.png)

`FlowExecutionStatus`로 상태를 관리해주어도 on()메소드가 잘 작동하는것을 확인해 볼수있다.

`JobExecutionDecider`를 통해 각 Step에서 `contribution.setExitStatus(ExitStatus.FAILED)`를 통해 step의 `exitStatus`를 직접 변경시켜 분기처리를 하여 Step의 할일을 늘리지 않고 분기처리를 따로 하여 Step의 일을 덜어줄수있다.



## 마무리

Job Flow를 공부하면서 step의 역할에 대해 조금이나마 더 이해할수 있게 된것같다.

상황에 따라 step을 자유롭기 분기처리 해줄수 있어 나중에 유용하게 사용해줄수 있을거 같다.

