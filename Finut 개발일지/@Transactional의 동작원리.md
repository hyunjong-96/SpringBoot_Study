# @Transactional 동작 원리



## Transaction이란

데이터베이스에서 데이터를 다룰때 가장 중요한건 트랜잭션이다.

트랜잭션이란 데이터베이스와 상호작용을 하며 데이터 처리의 최소단위이다.

물론 spring boot 서버에서 데이터를 다룰때도 이 트랜잭션을 사용하는데, @Transactional 어노테이션을 사용해서 데이터의 흐름 처리를 해줄수 있게 된다.



## AOP

@Transactional는 AOP를 통해 트랜잭션 처리코드가 전 후로 수행된다.

AOP란? 관점 지향 프로그래밍으로써 핵심적 기능과 부가적 기능으로 나누어 부가적 기능을 모듈화해 주는 기술이다.

[https://github.com/hyunjong-96/SpringBoot_Study/blob/main/aop/AOP.md]

그럼 @Transactional은 어떻게 AOP로 동작이 되는것일까?

@Transactional을 선언한 타겟 메소드를 전 후로 EntityManager가 em.begin()과 em.commit()을 실행하도록 되어있다.

이처럼 AOP는 기본적으로 디자인 패턴 중 Proxy 패턴을 사용해서 구현한다.

Spring에서는 두 가지 프록시 구현체가 있다.

<img src="https://user-images.githubusercontent.com/57162257/151190174-cbd26faa-ed45-4df5-8f3c-1d20c81bc9c2.png" style="zoom:80%;">

- JDK Proxy(Dynamic Proxy)

  - AOP를 이용해 구현된 인터페이스를 프록시 객체로 구현해서 코드를 끼워넣는 방식
  - Java의 Reflection을 사용하며, Java에서 기본적으로 제공하는 기능
    - java reflection : 객체를 통해 클래스의 정보를 분석해 내는 프로그램 기법
  - 인터페이스가 구현이 되어있지 않다면 사용하지 못하는 단점
  - (Service layer에서 ~Impl클래스를 작성하는 방법도 JDK Proxy의 특성때문이라고 한다.)

- CGLib Proxy

  - 클래스를 상속을 통해 프록시 객체로 만들어 사용하는 방식
  - 타깃오브젝트가 인터페이스를 상속하고 있지 않다면 CGLib를 사용하여 인터페이스 대신 타깃오브젝트를 상속하는 프록시 객체를 만듦
  - Java의 Reflection을 사용하지 않고 바이트 코드 생성 프레임워크를 사용하여 런타임 시점에 프록시 객체 생성

  

## @Transactional의 동작

```java
public class void UserSerivce{
  ...
  
  public void saveUser(UserDto userDto){
    //save로직
  }
  
  public GetUserDto getUser(Long userId){
    //get로직
  }
  
  ...
}
```

```java
public void UserServiceTransactionProxy extends UserServuce{
  private final EntityManger em;
  
  public void saveUser(UserDto userDto){
    try{
      em.begin();
      super.saveUser(userDto);
      em.commit();
    }catch(Exception e){
      em.rollback();
    }
  }
  
  public GetUserDto getUser(Long userId){
    try{
      em.begin();
      super.getUser(userId);
      em.commit();
    }catch(Exception e){
      em.rollback();
    }
  }
}
```

대강 이런식으로 타겟 클래스(UserService)가 빈으로 등록될때 프록시객체(UserServiceTransactionProxy)가 함께 등록되어  UserService를 상속받아  Controller layer에서 오는 요청을 UserService의 프록시 객체가 대신 받아서 트랜잭션의 begin, commit 메소드를 타겟메소드(UserService의 메소드)를 호출하게 된다.



## @Transactional의 주의사항

### Private 접근제어자는 @Transactional이 적용되지 않는다.

<img src="https://user-images.githubusercontent.com/57162257/151182523-42759d3e-6f35-4a4b-9174-8d523a9090e6.png" style="zoom:50%;">

@Transactional은 아까 설명한대로 외부(프록시 객체)에서 접근해서 사용되므로 public으로 선언되어야한다.



### 같은 빈에서 여러 @Transactional호출

결과적으로는 같은 빈에서는 여러 @Transactional을 호출해도 타겟메소드 외의 메소드에 적용된 @Transactional은 소용없다.

두개의 메소드가 @Transactional을 선언했을 경우

<img src="/Users/flab1/Library/Application Support/typora-user-images/image-20220126235407371.png" alt="image-20220126235407371" style="zoom:50%;" />

![image-20220126235350158](/Users/flab1/Library/Application Support/typora-user-images/image-20220126235350158.png)

5번의 저장 로직을 수행했지만 아무것도 저장되지 않았다.

<img src="/Users/flab1/Library/Application Support/typora-user-images/image-20220126235456458.png" alt="image-20220126235456458" style="zoom:50%;" />

트랜잭션 또한 isOtherTransaction메소드의 것이 아닌 프록시 객체에서 호출된 타겟 메소드(repetitionUserSave)의 것임을 확인할수 있다.

처음 호출 메소드가 @Transactional을 선언하지 않았을경우

<img src="/Users/flab1/Library/Application Support/typora-user-images/image-20220126234910422.png" alt="image-20220126234910422" style="zoom:50%;" />

![image-20220126234846140](/Users/flab1/Library/Application Support/typora-user-images/image-20220126234846140.png)

위 와 같이 5개가 잘 저장된것을 확인할수있다.

<img src="/Users/flab1/Library/Application Support/typora-user-images/image-20220126234935819.png" alt="image-20220126234935819" style="zoom:50%;" />

하지만 처음에 Controller layer에서 호출한 타겟 메소드는 프록시 객체에 선언되어 있지 않아 트랜잭션이 적용되지 않은것을 볼수있고, 타겟 메소드에서 호출한 @Transactional이 선언된 메소드는 프록시객체에서 호출된 메소드가 아니기 때문에 트랜잭션이 적용되지 않은것 또한 볼수 있다.





## 참고

https://cobbybb.tistory.com/17

https://minkukjo.github.io/framework/2021/05/23/Spring/