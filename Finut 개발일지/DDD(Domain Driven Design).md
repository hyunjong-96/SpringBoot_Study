# DDD(Domain Driven Design)



## 1.Domain

도메인이란, `실제 세계에 있는 개념을 우리 시스템에 넣는 영역` 혹은 `어플리케이션 내의 로직들이 관여하는 정보와 활동의 영역`이라고 한다.

예를 들어 서비스에서 회원과 관련하여 회원의 이름, 나이 등과 같이 회원과 관련된 정보를 다룰때 회원의 정보가 있는 '회원'이라는 도메인이 있다.

다른 용어로는 `domain layer` 이라는 용어가 있다.

domain layer란 무엇인가? 그 전에 layer를 이루는 `layer architecture`부터 알아볼 필요가 있다.

## 2. 레이어 아키텍처(Layer Architecture)

아키텍처(architecture)는 시스템 목적을 위해 전체적인 시스템의 상호작용과 흐름의 디자인이다.

그렇다면 `Layer Architecture`는 유사한 관심사들을 레이어로 나누어 수직적으로 나열한것이다. 

### 장점

이렇게 레이어로 나누어 시스템을 구축한다면 한 층만 따로 다른 시스템으로 갈아끼워도 문제가 생기지 않고 다른 레이어에서 필요에 따라 가져와 사용할수 있다.
즉, **유동적이고 재활용성**있다.

### 구조

![image](https://user-images.githubusercontent.com/57162257/147226366-789be400-3237-4bf6-b7dc-60b2c580b918.png)

레이어 아키텍처의 구조는 위와 같다.

Presentation layer, Domain layer, Persistence Layer, Database layer

- **Persentation layer(view) - user interface** : 화면, 조작, 사용자에 의해 요청되는 계층
- **Application layer** : 특정한 행위의 어플리케이션 기능을 추상화하를 창출하는 공간.
- **Domain layer** : 핵심 비즈니스 로직이 담기는 곳, 어플리케이션의 가치를 결정하는 가장 중요한 layer
- **Infrastructure layer** : 각 layer들이 자신이 맡은 역할을 수행할수 있도록 기술적인 부분에서 지원해주는 layer

공부하면서 Application layer와 Domain layer간의 개념이 모호해서 많이 찾아보았었다.

https://riiidtechblog.medium.com/gradle%EA%B3%BC-%ED%95%A8%EA%BB%98%ED%95%98%EB%8A%94-backend-layered-architecture-97117b344ba8

님의 정리를 빌려오자면 

> application service에는 비즈니스 로직을 작성하지 않으며, domain layer에 작성되어 있는 비즈니스 로직을 호출하기만 한다.

라고 했다. 내가 이해한바로는 spring mvc의 controller로 이해했다.

## 3. Domain Layer

도메인 레이어는 위에서 말한바와 같이 어플리케이션을 구현하는데 있어 가장 중요한 layer이자 핵심 가치이다.

도메인 레이어의 유형에는 Transaction Script Pattern과 Domain Model Pattern이 있다.

### **Transaction Script Pattern**

나같은 아기 개발자들이 많이 사용하는 방법인데, 쉽게 설명하면 도메인 객체에서 데이터만 가지고있고 그 데이터를 처리하는 방법이다.

- ex) 주문을 취소했을때의 로직이다. 주문을 취소하면  order의 상태는 변경되어야하고 그 밖에 다른 로직이 수행되어야한다.

  ```java
  public Order cancelOrder(long orderId){
      Order order = orderRepository.findById(orderId);
      
      order.setCancel(true);
      somethingCancelMethod();
      somethingCancelMethod2();
      
      return order;
  }
  ```

  뭐.. 대충 이런식으로 data를 가진 객체를 getter와 setter만을 호출하기 떄문에 대부분의 로직은 Transaction Script안에 담기게 된다.

- Transaction Script의 문제점

  - 비대해지는 서비스 메소드
  - 이해하기 어려운 서비스 메소드
  - 테스트하기 어려운 메소드

### **Domain Model Pattern**

도메인 모델 패턴은 `행위가 관련된 데이터와 함께 객체를 이루는 형태.`

Transaction Script Pattern과 같은 예를 들자면

```java
public Order cancelOrder(long orderId, long userId){
    Order order = orderRepository.findById(orderId);
    
    order.cancel();
    
    return order;
}
```

행위와 관련된 데이터가 order에 이루어져있고 그 객체는 주문 취소라는 행위를 가지고 있어 그 객체에서 한번에 처리할수 있다. 









