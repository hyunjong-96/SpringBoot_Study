# AOP

aop는 Spring 삼각형 중 하나로 Spring에서 가장 중요한 3가지 개념 중 하나이다.

***Spring 삼각형 : DI, AOP, PSA**

## 1. AOP 란

AOP는 Aspect Oriented Programming의 약자로 `관점지향프로그램`이라는 의미의 개념이다.

관점지향이란, **어떤 로직을 기준으로 핵심적인 관점, 부가적인 관점으로 나누어서 보고 그 관점을 기준으로 각각 모듈화 하겠다는 것**이다.

- 핵심적인 관점 : 비즈니스 로직에서 적용하고자 하는 것. 
- 부가적인 관점 : 핵심적인 관점을 실행하기 위해서 행해지는 것.(로깅, 데이터베이스 연결 등..)

![](https://user-images.githubusercontent.com/57162257/147521851-f0d8e4c0-569e-499c-8148-0c139785295f.png)

[출처]https://engkimbs.tistory.com/746?category=767795

AOP에서 모듈화 한다는 것은 공통적으로 사용되는 코드들을 부분적으로 나누어서 관리하겠다는 것이다. 위의 그림에서와 같이 각 클래스에서 공통적으로 사용되는 A,B,C 코드들을 AspectX, Y, Z 로 모듈화해서 관리를 한다. 예를 들어 노란색, 파란색, 빨간색 코드들은 중복적으로 사용되어 따로 분리해서 사용하게 되는데 AOP에서 말하는 'aspect'라고 할수 있다.

이렇게 모듈화를 해준다면 하나의 코드에서 수정이 일어나게 된다면 수정되는 코드가 사용되는 클래스를 모두 찾아서 전부 다 수정을 해주지 않고 모듈화되어있는 곳에서 한 번만 수정을 할수 있어 유지보수에 큰 도움이 된다.

그리고 부가적인 코드들을 모듈화 헀기때문에 개발자는 비즈니스 로직에 몰두할수 있게 된다.



## 2. 프록시 객체

![image](https://user-images.githubusercontent.com/57162257/147523332-26b3913d-92b4-45d4-92c3-271a6327b140.png)

[출처]https://sallykim5087.tistory.com/158

AOP는 Spring 영역에서 사용되는데 어떤 클래스가 Spring AOP의 대상이라면 해당 클래스의 빈이 만들어질때 Spring AOP가 프록시(기능이 추가된 클래스)를 자동으로 만들고 원본 클래스 대신 프록시를 빈으로 등록한다.

Filter, Interceptor, AOP간의 관계는 다음에 다루도록 하겠다.

https://sallykim5087.tistory.com/158

Spring AOP는 프록시 패턴이라는 디자인 패턴을 사용해서 어떤 기능을 추가하려할때 기존 코드를 수정하지않고 기능을 추가할수있다.

가장 대표적인 예로 @Transactional 어노테이션이 있다. @Transactional 어노테이션을 사용함으로써 commit, rollback 관련 기능들을 데이터베이스에서 데이터를 가져올때마다 신경쓸 필요없이 @Transactional이 해당 클래스 타입의 프록시를 만들어서 반복, 중복되는 코드를 생략할수 있게 되는것이다.



## 3. AOP 주요개념

- Aspect : 공통되는 부가적인 기능을 모듈화
- Target : Aspect를 적용하는 곳(클래스, 매서드)
- Advice : 실질적으로 어떤 일을 해야할 지에 대한것, 실질적인 부가기능을 담은 구현체
- JoinPoint : Advice가 적용될 위치
- PointCut : JoinPoint의 상세한 스펙을 정의한 것. ex) A메소드의 진입 시점에 Advice가 실행될 지점을 정함.



## 4. 스프링 AOP 특징

- 프록시 패턴 기반의 AOP 구현체
  - 프록시 객체를 사용하는 이유는 접근 제어 및 부가기능을 추가하기 위함
- 스프링 빈에만 AOP 적용 가능
- 모든 AOP기능을 제공하는 것이 아닌, IoC와 연동해서 애플리케이션에서의 문제(중복코드, 프록시 클래스 작성의 번거로움, 객체들 간 관계 복잡도 증가..)에 대한 해결책을 지원하는 것이 목적



## 5. Spring AOP

테스트해볼 내용은, 각 비즈니스 로직을 시킬때 Service layer에서 얼만큼의 시간이 걸렸는지에 대한 성능테스트 코드를 작성하려고한다.

```java
@Service
public class UserService {
	private final UserRepository userRepository;

	Logger logger = LoggerFactory.getLogger(UserService.class);

	public void createUser(String name) {
		StopWatch stopWatch = new StopWatch();
		stopWatch.start();

		User newUser = User.builder()
			.name(name)
			.build();

		userRepository.save(newUser);

		stopWatch.stop();

		logger.info(stopWatch.prettyPrint());
	}

	public String allUser(){
		StopWatch stopWatch = new StopWatch();
		stopWatch.start();

		List<User> userList = userRepository.findAll();
		List<String> nameList = userList.stream()
			.map(User::getName)
			.collect(Collectors.toList());

		stopWatch.stop();
		logger.info(stopWatch.prettyPrint());
		
		return nameList.toString();
	}
}
```

위의 코드는 각 엔티티를 저장하는 로직인 `createUser` 메소드, 모든 사용자를 불러오는 로직인 `allUser`메소드에서 성능테스트를 중복적으로 사용하고 각 메소들의 핵심 기능에 집중하지 못하는 문제가 있다.

![image](https://user-images.githubusercontent.com/57162257/147538228-d94e0447-58a4-4df2-8dbd-822d02004337.png)

여기서 부가적인 기능이고 중복적인 기능인 성능테스트 코드를 모듈화 해보자.



#### 의존성 추가

```gradle
dependencies{
	implementation 'org.springframework.boot:spring-boot-starter-aop'
}
```

```java
@Target(ElementType.METHOD)	//1
@Retention(RetentionPolicy.RUNTIME)	//2
public @interface LogExecutionTime {
}
```

- LogExecutionTime 어노테이션을 정의해준다.
  1. @Target(ElementType.METHOD)
     해당 어노테이션을 메소드에 사용될것이다.
  2. @Retention(RetentionPolicy.RUNTIME)
     해당 어노테이션이 RUNTIME까지 유지도되록 설정한다.
- LogExecutionTime 어노테이션은 해당 Aspect가 실행되는 곳을 지정해주는 기능을 한다.

```java
@Component
@Aspect
public class LogAspect {
	Logger logger = LoggerFactory.getLogger(LogAspect.class);

	@Around("@annotation(LogExecutionTime)")	//1
	public Object logExecutionTIme(ProceedingJoinPoint joinPoint) throws Throwable{
		StopWatch stopWatch = new StopWatch();
		stopWatch.start();

		Object proceed = joinPoint.proceed();	//2

		stopWatch.stop();
		logger.info(stopWatch.prettyPrint());

		return proceed;
	}
}
```

- 코드 성능 테스트 코드가 있는 Aspect
  1. @Around("@annotation(LogExecutionTIme)")
     - Around 어노테이션의 value에 아까 설정했던 LogExecutionTime 어노테이션을 지정함으로써 LogExecutionTime 어노테이션이 사용되었을때의 메소드가 Aspect의 Target메소드임을 정의한다.
     - 즉, JoinPoint가 LogExecutionTime 어노테이션을 사용한 메소드가 되는것이다.
  2. Object proceed = joinPoint.proceed()
     - 타겟 메소드를 실행시킨다.
     - 타겟 메소드를 실행시키기 전과 후에 성능 코드를 작성해서 타겟 메소드가 종료되었을때 로그를 확인할 수 있다.

```java
public class UserService {
	private final UserRepository userRepository;

	@LogExecutionTime	//1
	public void createUser(String name) {
		User newUser = User.builder()
			.name(name)
			.build();

		userRepository.save(newUser);
	}

	@LogExecutionTime	//1
	public String allUser(){
		List<User> userList = userRepository.findAll();
		List<String> nameList = userList.stream()
			.map(User::getName)
			.collect(Collectors.toList());

		return nameList.toString();
	}
}
```

실행 시켜보면.

![image](https://user-images.githubusercontent.com/57162257/147538043-890d4235-d9f7-47e9-9595-99cf75abeac4.png)

1. 맨 처음의 코드에 비해 각 비즈니스 로직의 핵심 만 구현되어있고 성능 코드는 로직상 존재하지 않지만 로그는 확인할수 있다.

Service layer에는 성능코드를 모듈화한 Aspect를 실행시켜주는 LogExecutionTime 어노테이션만 추가되었을뿐 다른 부가 기능의 코드는 존재 하지 않는다.



AOP를 사용하는 방법으로 어노테이션이 아닌 타겟 메소드의 경로를 통한 방법도 있다.

```java
@Component
@Aspect
public class LogAspect {
	Logger logger = LoggerFactory.getLogger(LogAspect.class);

	@Pointcut("execution(* com.example.aopproject.user.UserService.createUser(..))")	//1
	public void createUser(){}

	@Pointcut("execution(* com.example.aopproject.user.UserService.allUser(..))")	//2
	public void allUser(){}

	@Around("createUser() || allUser()")	//3
	public Object logExecutionTimeByPath(ProceedingJoinPoint joinPoint) throws Throwable{
		Object result = null;
		try{
			StopWatch stopWatch = new StopWatch();
			stopWatch.start();

			result = joinPoint.proceed();

			stopWatch.stop();

			logger.info(stopWatch.prettyPrint());
		}catch(Throwable throwable){
			System.out.println("exception!!!");
		}

		return result;
	}
}
```

1. Pointcut 어노테이션을 통해 value에 정의된 경로의 메소드를 createUser메소드로 정의.
2. Pointcut 어노테이션을 통해 타겟 메소드를 allUser메소드로 정의
3. Pointcut으로 정의한 타겟 메소드를 Advice의 실행 지점으로 정의해준다.

어노테이션을 사용하든 경로로 설정하든 개바개로 사용하면 될것같다.

AOP를 사용함으로써 객체의 기능적 부담을 덜어주고 유연하게 유지보수를 할수 있음을 확인했다.



## 출처

https://jojoldu.tistory.com/72

https://atoz-develop.tistory.com/entry/Spring-%EC%8A%A4%ED%94%84%EB%A7%81-AOP-%EA%B0%9C%EB%85%90-%EC%9D%B4%ED%95%B4-%EB%B0%8F-%EC%A0%81%EC%9A%A9-%EB%B0%A9%EB%B2%95

https://engkimbs.tistory.com/746?category=767795

https://sallykim5087.tistory.com/158