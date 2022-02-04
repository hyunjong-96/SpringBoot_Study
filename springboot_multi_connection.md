# 스프링부트의 다중 유저



## 스프링의 다중 유저 처리 흐름

1. 스프링 부트의 Tomcat을 사용
2. Tomcat은 다중 유저의 요청을 처리하기 위해 Thread Pool생성
3. Client의 요청시 서블릿 컨테이너에서 Thread Pool의 스레드를 할당
4. 요청 사항을 다시 Client에게 반환후 Thread Pool에 할당한 스레드 반환



## 스프링 부트와 내장 톰캣

spring 과 spring boot의 차이점 중 하나는 spring boot에서는 내장 서블릿 컨테이너(Tomcat)을 지원한다는 것이다.

서블릿 컨테이너란 서블릿의 생성, 초기화, 종료등의 생명주기를 관리해주는 것.

서블릿 & 서블릿 컨테이너 정리 : https://github.com/hyunjong-96/SpringBoot_Study/blob/main/Finut%20%EA%B0%9C%EB%B0%9C%EC%9D%BC%EC%A7%80/servlet%26servlet_container.md

```ini
# application.yml (적어놓은 값은 default)
server:
  tomcat:
    threads:
      max: 200 // 생성할 수 있는 thread의 총 개수
      min-spare: 10 // 항상 활성화 되어있는(idle) thread의 개수
    max-connections: 8192 // 수립가능한 connection의 총 개수
    accept-count: 100 // 작업큐의 사이즈
    connection-timeout: 20000 // timeout 판단 기준 시간, 20초
  port: 8080 // 서버를 띄울 포트번호
```

application.yml에 Tomcat의 설정을 바꿔줄수 있는데, 위의 설정은 tomcat의 thread와 connection, 작업 큐의 크기를 설정한 것이다.

### Thread Pool이란?

프로그램에 필요한 스레드들을 미리 생성해 놓는 곳이다.

#### Thread Pool Flow

1. 작업이 들어오게 되면 core-size(min-spare)의 크기만큼 스레드를 생성
2. 모든 스레드가 작업중이라 Idle한 스레드가 없다면 client요청들은 작업 큐에 쌓인다.
   1. 다른 작업이 끝난 스레드는 작업 큐에 저장되어있는 요청에 할당된다.
3. 2와 같은 방법이 반복되다보면 작업 큐가 꽉차게 되면 스레드를 생성시켜준다.
   1. 만약 생성할수 있는 최대 스레드 갯수(max)까지 생성되게되면 `connection-refused`오류를 반환하게된다.

Thread Pool은 최대한 core-size를 유지하려고 하기때문에 그에 맞는 `스레드 풀 전략`도 있고, `적정 스레드 갯수`도 검색하면 있다.

### ThreadPoolExecutor

java에서 Thread Pool을 사용하기 위한 구현체가 ThreadPoolExecutor이다.

ThreadPoolExecutor의 설정은 위의 application.yml에 작성한것처럼 tomcat의 thread를 설정할수 있다.

참고로 톰캣의 디폴트 옵션은 `스레드 최대 사이즈 : 200`, `core-size : 25`인데 

spring boot의 디폴트 값은 `스레드 최대 사이즈 : 200`, `core-size : 10`이라고 한다.



그럼 이 Thread Pool을 가지고 어떻게 spring boot에서 다수의 요청을 받는지 알아보자.

spring boot에 client의 요청이 들어오게 되면 spring boot의 내장 톰캣이 client의 요청을 받아 스레드를 할당시켜주어 서블릿의 service()를 통해 요청에 대한 응답 작업을 하게 해준다.

이 때 클라이언트의 요청을 받고 응답을 해주는 방식에는 2가지가 있는데, `BIO Connector`와 `NIO Connector`가 있다.



## Connector

Connector란 클라이언트와와 서버간 통신을 할때 소켓을 통해 연결을 하게 되는데, 서블릿 컨테이너에서는 소켓의 관리도 함께 해주는데, 서블릿 컨테이너에서의  accepter에서 while문으로 대기 하다가 port listen을 통해 Socket Connection을 얻게 되고 그 Socket Connection에서 client의 요청 데이터를 가져올수 있다.

즉, 클라이언트와 서블릿 컨테이너 간의 커뮤니케이션 구성 요소이다.

## BIO Connector

BIO Connector는 Java의 기본적인 I/O기술을 사용한다.

클라이언트의 요청을 받았을때 Thread Pool의 스레드를 각각 할당해준다.

즉, 하나의 connection 당 하나의 스레드가 할당되는 것이기 때문에, `사용자 수 = 스레드 수`가 된다.

이런 식으로 Thread를 사용하게 되면 Connection당 스레드가 할당되어 모든 스레드가 활발히 사용돼지 않고 Idel한 스레드가 발생하여 자원의 낭비가 발생하게 된다.

<img src="https://user-images.githubusercontent.com/57162257/152491200-769ce26f-bfe8-4284-af48-d1ee40a5851b.png" alt="image" style="zoom:50%;" />

이 문제를 해결하기 위해 NIO Connector가 등장하게 되었다.



## NIO Connector

NIO Connector는 `Poller`라는 별도의 스레드가 connection을 처리하는데, 새로운 스레드를 바로 할당하지 않고 Poller가 socket connection을 캐시로 들고 있다가 socket에서 **data에 대한 처리가 가능한 순간에만 스레드를 할당하는 방식**을 사용해서 스레드가 idle한 상태를 최소화 시켜준다.

NIO Connector에서는 acceptor에서 socket connection을 accept하면 소켓에서 `Socket Channel` 객체를 얻어서 톰캣의 `Nio Channel`에 등록한다. channel에 등록된 소켓 커넥션들 중 data처리가 가능한 소켓들은 Selector에 의해 Worker Thread Pool의 Worker Thread에 해당 소켓을 할당한다.

Poller에서는 Max Connection까지 연결을 수락하고, Selector를 통해 등록된 채널들을 관리하기 때문에 작업큐에 상관없이 `connection refused` 오류를 반환하지 않고 클라이언트 요청을 받아 들인다.

<img src="https://user-images.githubusercontent.com/57162257/152546227-db69f071-dc21-4c66-9e34-56a4fbf98be1.png" alt="image" style="zoom:50%;" />



## 참고

https://velog.io/@sihyung92/how-does-springboot-handle-multiple-requests

https://hadev.tistory.com/m/28