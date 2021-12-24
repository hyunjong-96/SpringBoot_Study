# DDD(Domain Driven Design)



## 1.Domain

도메인이란, `실제 세계에 있는 개념을 우리 시스템에 넣는 영역` 혹은 `어플리케이션 내의 로직들이 관여하는 정보와 활동의 영역`이라고 한다.

예를 들어 서비스에서 회원과 관련하여 회원의 이름, 나이 등과 같이 회원과 관련된 정보를 다룰때 회원의 정보가 있는 '회원'이라는 도메인이 있다.

시스템에서 domain을 다루는 영역이 있는데 `domain layer` 이라는 용어가 있다.

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
- **Application layer** : 특정한 행위의 어플리케이션 기능을 추상화를 창출하는 공간(외부).
- **Domain layer** : 핵심 비즈니스 로직이 담기는 곳, 어플리케이션의 가치를 결정하는 가장 중요한 layer(내부)
- **Infrastructure layer** : 각 layer들이 자신이 맡은 역할을 수행할수 있도록 기술적인 부분에서 지원해주는 layer(외부)

공부하면서 Application layer와 Domain layer간의 개념이 모호해서 많이 찾아보았었다.

https://riiidtechblog.medium.com/gradle%EA%B3%BC-%ED%95%A8%EA%BB%98%ED%95%98%EB%8A%94-backend-layered-architecture-97117b344ba8

님의 정리를 빌려오자면 

> application service에는 비즈니스 로직을 작성하지 않으며, domain layer에 작성되어 있는 비즈니스 로직을 호출하기만 한다.

그리고 spring에 맞춰 잘 정리해준 https://www.baeldung.com/hexagonal-architecture-ddd-spring 에서는
(참조 소스는 hexagonal architecture 내용입니다.)

![image](https://user-images.githubusercontent.com/57162257/147326183-7c8b0f1e-2f52-4af1-8613-942a99301b55.png)

- Application layer는 도메인 로직의 실행을 조정하는 역할 즉, RESTful 컨트롤러 및 JSON 직렬화 라이브러리와 같은 항목이 포함되어야 한다. 애플리케이션에 항목을 노출하고 도메인 논리 실행을 조정하는 모든 것.
- Domain layer는 비즈니스 로직을 만지고 구현하는 코드를 유지. 즉, domain model을 정의하고 Application layer에서의 요청에 맞는 서비스를 정의하는 것.

자료들을 읽고 Domain layer는 `Entiy 클래스`, `JpaRepository의 추상화 Repository`, 그리고 `비즈니스 로직이 일부 포함된 Service`가 있고, `Application layer는 end point를 노출시키고 사용자에게 시스템의 논리 구현을 1대1로 연결 시켜주는 Controller`다 라고 이해했다.

(좀 더 많은 소스를 참고해보고 잘못알고 있었던 점은 다시 수정하자)



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

도메인 모델 패턴은 `행위가 관련된 데이터와 함께 객체를 이루는 형태.` 그리고 `Domain layer의 객체지향적 구현` 이다.

Transaction Script Pattern과 같은 예를 들자면

```java
public Order cancelOrder(long orderId, long userId){
    Order order = orderRepository.findById(orderId);
    
    order.cancel();
    
    return order;
}
```

행위와 관련된 데이터가 order에 이루어져있고 그 객체는 주문 취소라는 행위를 가지고 있어 그 객체에서 한번에 처리할수 있다. 



## 2. DDD란

 DDD는 Domain Driven Design의 약자로 `도메인 주도 설계`의 뜻으로 도메인 패턴을 중심에 놓고 설계하는 방식.

DDD의 목적은 `소프트웨어의 복잡성을 최소화 하는것.`

DDD를 사용함으로써 얻는 이점은 엔티티들이 개발 과정에서 추가, 삭제될 수 있다. 이러한 변화 속에서 도메인 중심적으로 개발을 한다면 각 각의 엔티티들의 관계는 크게 변하지 않는것이다.

예를 들어보자. 공연을 예매한다고 할때,

- 공연 스케줄은 하나 이상의 공연이 있어야한다.
- 공연에는 할인 정책이 있을수 있고 없을수 있다.
- 할인 정책이 있다면 할인 조건은 무조건 있어야 한다.
- 사용자가 예매를 했을때는 공연스케줄은 무조건 있어야 한다.

는 가정을 가지고 domain model을 아래와 같이 표현할수있다.

![image](https://user-images.githubusercontent.com/57162257/147331525-208c3354-ea73-4537-81ef-ad69bfbedca5.png)

공연을 예약할때 절차지향적으로 코드를 짠다면 아래 흐름과 같이 짤수있다.

```java
public class ReservationService{
	public Reservation reservationOfShow(long userId, long scheduleId){
  //1.스케줄을 찾는다.
  Schedule schedule = scheduleRepository.findById(scheduleId);
  //2.스케줄의 공연을 찾는다.
  Show show = showRepository.findById(schedule.getId());
  //3.회원이 공연을 예약할때 해당 공연에 할인정책이 적용되는지 확인한다.
  List<Rule> showRuleList = findRule(show.getId());
  //4.공연 예약 금액을 구한다.
  Money fee = show.getFee();
  if(rule != null){
    fee = calculatorFee(show);
  }
  //5.예약을 한다.
  Reservation reservation = new Reservation();
  reservation.setUserId(userId);
  reservation.setFee(fee);
  reservation.setShowScheduleId(schedule.getId());
	reservationRepository.save(reservation);
  
  return reservaion;
}
}
```

이런 코드는 비즈니스 로직이 Reservation에 집중되어있는 중앙 집중식 제어 스타일이다. 위에서 설명했던 TransactionScript라고 볼수있다.

하지만 DDD는 도메인 주도 설계이자 객체지향적인 설계는 각 행위에 대해 관련있는 객체에 책임을 위임시켜야 한다.

- **공연을 예약 하는 행위**는 예약에 대한 정보를 많이 가지고 있는 **Schedule에 책임을 위임**한다.
- **할인 조건을 판단하는 행위**는 할인 정보를 많이 가지고 있는 **Rule에 책임을 위임**한다.
- **할인 금액을 구분하는 행위**는 할인에 대한 정보를 많이 가지고 있는 **DiscountPolicy에 책임**을 위임한다.
- **공연의 가격을 측정하는 행위**는 공연의 가격을 가지고 있는 **Show에 책임을 위임**한다.

이렇게 각 행위의 객체에 책임을 위임시킴으로써 객체간의 협력관계를 통해 요구되는 행위를 해결해나가는 것이 객체지향적으로 설계한것이라 할수있다.

1. ```java
   public class Schedule{
     public Reservation reservation(User user, int amountUser){
       return new Reservation(user, this, amountUser);
     }
   }
   ```

2. ```java
   public class Reservation{
    public Reservation(User user, Schedule schedule, int amountUser){
      this.user = user;
      this.fee = schedule.calculatorFee();
      this.schedule = schedule;
      this.amountUser = amountUser;
    } 
   }
   ```

3. ```java
   public class Schedule{
     public Money calculatorFee(){
       return this.show.calculatorFee();
     }
   }
   ```

4. ```java
   public class Show{
     public Money calculatorFee(){
   		return this.discount(this.discountPolicy.caculateDiscount(this));
     }
   }
   ```

5. ```java
   public class DiscountPolicy{
   	public Money caculateDiscount(Show show){
   		for(Rule rule : rules){
         if(rule.isSatisfy(show)) return getSatisfyFee(show);
         return Money.ZERO;
       }
     }	
   }
   ```

6. ```java
   public Rule{
     public Boolean isSatisfy(Show show){
       //show가 rule에 만족하는지 안하는지에 대한 코드..
     }
   }
   ```

   

이런 식으로 예약과 할인의 행위에 따라 행위에 맞는 역할을 객체 위임하여 요구되는 행위들을 연관된 객체를 찾아서 해결하게 되는 것이다.





## 전략적 설계와 전술적 설계



### 전략적 설계

전략적 설계란 복잡한 하나의 도메인을 해결하고자 여러 하위 도메인으로 나누는 방법. 하위 도메인으로 나뉘어지면서 `Bounded Context(경계)`가 이루어지고 많이들 아는 MSA가 가능해지는 것이다.

![image](https://user-images.githubusercontent.com/57162257/147339725-da1b2e9a-f2fc-4c8b-9a98-046933deb452.png)

전략적 설계는 보는 바와 같이 Bounded Context에서도 교차되는 지점이 있는데, 이는 같은 식별자를 가져서 같은 대상을 지칭하고 있지만 다른 모델을 가질수 있는것이다.

예를 들어 음식을 배달 주문을 시킬때 배달 도메인과, 주문 도메인있고,  각 도메인은 전략적 설계로 하위 도메인으로 나뉘어져 배달, 주문이라는 Bounded Context로 나누어져 있다고 가정해보자 .
음식을 배달 주문 시킨 고객의 엔티티의 id는 배달 도메인에서도 주문 도메인에서도 분명 존재한다. 같은 고객 id를 가지고 있는 두 도메인이지만 배달 도메인의 고객 id 모델은 당연히 고객 id는 있고 고객의 `주소`, `전화번호` 등 배달에 필요한 정보를 사용할수 있고, 주문 도메인에도 고객 id는 분명 있다 하지만 `단골 여부`, `작성한 리뷰 갯수` 등을 사용할수 있다.

이들은 그저 고객의 id만들 공유하고 있을 뿐 주문이나 배달 model에 필드를 추가한다고 해서 크게 영향을 받지 않는다.



### 전술적 설계

전술적 설계는, 전략적 설계에 의해 만들어진 하나의 Bounded Context내에서 본격적으로 도메인 모델을 추출하는 방법이다.

![image](https://user-images.githubusercontent.com/57162257/147340885-cacce08e-06f7-4641-bf8b-58cafd3f8fd4.png)

전술적 설계에는 위와 같은 요소들이 사용된다고 한다.

#### Entity

Entity란 식별자를 가지는 객체이다.

도메인 레이어의 대부분 비즈니스 로직들이 Entity를 가진다.

흔히 Spring boot를 사용할때 **@Entity** 어노테이션을 사용하는 클래스라고 보면된다.

```java
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Show{
  @Id
  @GeneratedValue
	private Long id;
  
  @ManyToOne
  private List<Schedule> scheduleList;
  
  //...
}
```



#### Value Object

Value Object는 Entity를 조금더 표현적으로 나타나게 해준다.

Spring boot 에서는 **@Embeddable**어노테이션이라고 보면 된다.

```java
@Embeddable
public class Address{
  private String city;
  private String street;
  private String region;
}
```

```java
@Entity
public class User{
	@Id
  @GeneratedValue
  private Long id;
  
  private String name;
  
  @Embedded
  private Address address;
}
```

value object를 eneity의 attribute로 정의하면 entity의 id를 공유하며 저장된다.

value object에게도 일부의 책임을 위임할수 있다.



#### Aggregate

Aggregate는 도메인은 개발중에도 새로운 피쳐를 만들때도 복잡해지기 마련이다. 서비스가 복잡해질수록 관리를 유용하게 하기 위해 **도메인 객체의 집합체**이다.

aggregate를 사용함으로써 도메인 객체를 좀 더 상위 수준으로 추상화 할수 있다.

예를 들어 `주문`이라는 도메인은 `배달`, `주문자`, `결제` 등의 하위목록으로 나뉘어질수 있는데 이 하위 목록들을 `주문` 이라는 상위 개념(**Aggregate root**)으로 표현할수 있는것이다.

![image](https://user-images.githubusercontent.com/57162257/147342617-985b458f-3616-4af0-8c39-9f926f2967ea.png)

aggregate는 다수의 객체들이 포함되어있어 일관성을 유지해야하는데 하위 객체의 상태가 변하기 위해서 하위 객체에 직접 접근하여 변경을 해서 `일관성`을 깨트리는건 안된다.

오직 Aggregate root를 통해서만 하위 객체들의 상태를 변경시켜주어 일관성을 유지해야한다.



#### Repository

Repository는 도메인 모델의 영속성을 다룬다.

Repository는 Domain layer에서 interface형태로 정의되고 구현은 infra layer에서 이루어진다.

Spring boot에서는 `@Repository` 어노테이션을 사용하는 인터페이스이다.

```java
public interface OrderRepository extends JpaRepository<Order, Long>{
  //...
}
```



#### Domain Service

보통 비즈니스 로직은 Entity나 Value Object에 담기는 것을 권장한다. 하지만 어떤 행위를 다룰때 특정 객체가 다루기 애매한 경우가 있다. 이때 사용하는 것이 Domain Service.

Spring boot에서는 Domain Service를 의미하는 어노테이션은 따로 없고 `@Component어노테이션`을 보통 사용한다고 한다.
