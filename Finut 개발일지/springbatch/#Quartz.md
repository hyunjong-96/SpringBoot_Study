# # Spring Boot Quartz

`Quartz`는 다양한 java 애플리케이션에 통합 될 수 있는 **작업 스케줄링 라이브러리**.



## 1. 구조

### Job

- `Quartz API`에서는 단 하나의 메서드 `execute(JobExecutionContext context)`를 가진 Job인터페이스를 제공한다. `Quartz`를 사용할때 실제 작업을 `execute메소드`를 구현해서 사용하면 될것이다.
- `JobExecutionContext`는 `Scheduler`, `Trigger`, `JobDetail` 등을 포함해서 `Job`인터페이스에 대한 정보를 제공하는 객체이다.

### JobDetail

- `Job`을 실행시키기 위한 정보를 담고있는 객체.
- `Job`의 이름, 그룹, `JobDataMap` 속성 등을 지정할 수 있다.
- `Trigger`가 `Job`을 수행할때 이 정보를 기반으로 스케줄링을 수행한다.

### JobDataMap

- `JobDataMap`은 `Job인스턴스`가 `execute`로 실행할 때 원하는 정보를 담을수 있는 객체.
- `JobDetail`을 생성 및 설정할때 `JobDataMap`도 같이 세팅해준다.

### Trigger

- `Trigger`는 `Job`을 실행시킬 스케줄링 조건 등을 담고 있고 `Scheduler`는 이 정보를 기반으로 `Job`을 수행시킨다.
- 하나의 `Trigger`는 반드시 하나의 `Job`을 지정할 수 있다.
- `SimpleTrigger` : 특정 시간에 `Job`을 수행할 때 사용되며, 반복 횟수와 실행 간격등을 지정할수 있다.
- `CronTrigger` : `cron`표현식으로 `Trigger`를 정의하는 방식이다.



### SchedulerFactory, Scheduler

- `SchedulerFactory` : Scheduler를 빌드하는 역할, Quartz관련 속성을 기반으로 스케줄러 모델을 빌드하는 역할을 한다.
  - Quartz관련 속성은` application.yml`에서 설정가능.
- `Scheduler` : 등록된 Job과 Trigger를 관리
  - 연관된 Trigger의 발사시점을 보고있다가 관련 Job을 실행시키는 역할.

![image](https://user-images.githubusercontent.com/57162257/137835552-83b98c8e-06a1-4869-b329-5969322dbd84.png)

`Quartz Scheduler`를 위 그림을 보다싶 `Job`, `Trigger`, `Job Store`와 같은 리소스를 관리한다.

그리고 Trigger의 발사시점에 따라 `ThreadPool`에 있는 `Worker노드`에 해당 Job을 실행하도록 명령한다.



## 2. DB설치

Quartz를 사용하기 위해서는 사용하려는 DB에 Quartz와 관련된 데이터 테이블이 필요하다.

![image](https://user-images.githubusercontent.com/57162257/137836048-6168b13a-298c-4167-81c5-b7de7659231b.png)

요런 친구들이 필요한데 이 테이블들은 application.yml에 설정만해주면 자동으로 만들어준다.

```
spring:
	  quartz:
    	job-store-type: jdbc
    	jdbc:
      	initialize-schema: always
```

-----------------------

![image](https://user-images.githubusercontent.com/57162257/137838808-1b76ae48-44bf-4425-b7c3-415c15609e1b.png)

- job을 설정해주는 부분이다.

------------------

![image](https://user-images.githubusercontent.com/57162257/137838890-f2bfb606-4b6d-44e9-8bc2-d6d94dc40819.png)

- Quartz DB에 스케줄러가 존재한다면 지워주는 코드

-----------------------

![image](https://user-images.githubusercontent.com/57162257/137838874-c4fe93b3-86ff-453d-a0b3-f5da81dd871e.png)

- jobDetail을 설정하는 곳으로 jobDataMap을 함께 설정해준다.
- JobBuilder를 통해 Job으로 설정할 job, 이름, 설명, jobDataMap을 만들어서 반환해준다.

-----------------------

![image](https://user-images.githubusercontent.com/57162257/137838856-090759ee-cd79-4ad6-a78e-962c54a6e4b9.png)

- Trigger를 설정해주는 곳으로 `CronTrigger`와 `SimpleJobTrigger`가 있는데 Cron은 초,분,시,일,월,요일 까지 스케줄등록이 가능한데, SimpleJobTrigger에는 시,분,초,밀리초 밖에 없었다.



## 참고

https://kouzie.github.io/spring/Spring-Boot-%EC%8A%A4%ED%94%84%EB%A7%81-%EB%B6%80%ED%8A%B8-Quartz/#%EC%84%A4%EC%A0%95

https://hanke-r.tistory.com/101