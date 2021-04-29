# 1.의존성 주입

스프링에는 의존성 주입(Depedency Injection), 관점 지향 프로그래밍(AOP, Aspect Oriented Programing), PSA(Portable Service Abstraction)

- DI : 애플리케이션 간의 의존성을 통해 결합도를 낮춰줌
- AOP : 애플리케이션 전체에 걸쳐 사용되는 기능을 재사용하도록 도와줌
- PSA : 환경의 변화와 관계없이 일관된 방식의 기술로의 접근 환경을 제공하려는 추상화 구조



## (1) 의존성 주입(DI)란?

스프링의 **IoC(Inversion of Control) : 제어의 역전** 이라고 한다.

```java
//내부에서 주입
public class Car{
    private Tire tire;
    
    public Car(){
        this.tire = new KoreaTire();
    }
    
    public driveCar(){
        this.tire.start(); //바퀴가 굴러가는 기능
    }
}
```

위의 코드에서 Car라는 클래스에서 KoreaTire라는 객체를 생성자에서 만들어 Car클래스의 tire에게 대입하고있다.  **tire의 start메소드가 실행되기 위해서는 Car클래스는 Tire의 클래스가 필요하다.**

즉, 위의 코드는 ```Car가 Tire에 의존성을 가진다.``` 라고 할 수 있다.

하지만 이 코드에서는 Tire를 변경해주고 싶다면 Car클래스에서 항상 코드를 수정해줘야하기 때문에 **재활용성이 떨어지게되고 결합도가 높아진다**.

만약 Tire를 Car클래스 내부가 아닌 외부에서 주입을 해주게 된다면 Tire에 대해 수정을 해줘야할때 Car클래스를 직접 수정하지 않아도 된다.

```java
//외부에서 주입
public class Car{
    private Tire tire;
    
    public Car(Tire tire){	//tire를 외부에서 주입해준다.
        this.tire = tire;
    }
    
    public driveCar(){
        this.tire.start();
    }
}
```

 맨 처음의 코드처럼 클래스 내부에서의 주입이 아닌 위의 코드처럼 외부에서 주입을 해주는 것을 ```의존성 주입```이라고 한다.

**의존성 주입으로 인해 결합도가 낮아져 느슨한 결합이 됨.**

```java
public class Client{
    public static void main(String[] args){
        Tire tire = new KoreaTire();
        Car car = new Car(tire);	//외부에서 KoreaTire를 Car클래스에 주입
    }
}
```

## 

## (2) 생성자 의존성 주입 vs Setter의존성 주입

- 생성자 의존성 주입

  ```java
  public class Car{
      private Tire tire;
      
      public Car(Tire tire){
          this.tire = tire;
      }
  }
  ```

  **생성자를 통해 의존성을 주입**하게 된다면 단 한번의 의존성 주입 이후에는 의존관계를 변경할 일이 없다.(**의존관계는 애플리케이션 종료 전까지 불변**)

- Setter의존성 주입

  ```java
  publuic class Car{
      private Tire tire;
      
      public setTire(Tire tire){
          this.tire = tire;
      }
  }
  ```

  **setter를 통해 의존성을 주입**하게 되면 누군가에 의해 변경이 다분하고 추적또한 힘들어진다.

즉, **생성자 의존성 주입을 지향**한다.



## (3) 의존성 주입을 해야하는 이유

- 재사용성을 높이기 위해
- 결합도를 낮추기 위해(변경에 민감하지 않고 유연성과 확장성을 향상시킬수 있다)
- 테스트 하기에 용이함(의존성 주입을 유연하게 할수 있어 테스트할때 편리함, 특히 생성자 의존성 주입을 했을때)



## (4) 필드에 final

필드의 단 한번의 초기화와 개발자의 실수를 줄이기 위해~

![image](https://user-images.githubusercontent.com/57162257/116514223-b3b75800-a905-11eb-8d83-7786f185bec9.png)

final이 선언된 필드에서 초기화를 해주지 않는다면 컴파일 에러 발생.

![image](https://user-images.githubusercontent.com/57162257/116514500-127cd180-a906-11eb-9ad4-a3c6f5826443.png)

편안~

# 2.스프링에서 의존성 주입

## (1) Spring IoC 컨테이너란?

- Bean설정 소스로부터 Bean정의를 읽어오고 Bean을 구성하고 제공하는 역할
- Bean들의 의존관계 설정(객체 생성을 책임지고, 의존성 관리)
- Ioc컨테이너의 핵심 인터페이스는 BeanFactory, ApplicationContext

![image](https://user-images.githubusercontent.com/57162257/116517056-5ae9be80-a909-11eb-8972-b7e53c137508.png)

즉, **Bean으로 등록된 객체들을 IoC컨테이너가 의존성을 만들어서 외부에서 주입해주는 것.**

![image](https://user-images.githubusercontent.com/57162257/116516075-17db1b80-a908-11eb-9e9a-dc84d2173610.png)

- BeanFactory
  - 스프링 빈 컨테이너에 접근하기 위한 최상위 인터페이스
  - Bean 객체를 생성하고 관리하는 인터페이스
  - FactoryMethod 패턴을 구현한것
  - 구동될때 Bean객체를 생성하는 것이 아니라 클라이언트의 요청이 있을때 getBean()객체를 생성
- ApplicationContext
  - Bean을 Listable하게 보관하는 인터페이스
  - ResourceLoader, ApplicationEventPublisher, MessageSource, Bean Lifecycle을 제공
  - 구동되는 시점에 등록된 Bean 객체들을 스캔하여 객체화



## (2) 빈(Bean)이란?

```스프링 IoC 컨테이너가 관리하는 객체```



### Bean 등록의 장점

- 전에는 의존성을 직접 해줘야 했지만 IoC컨테이너에 등록된 Bean은 스프링이 의존성 관리 및 주입을 해주기 떄문에 의존성 관리가 수월해진다
- IoC컨테이너에 등록된 Bean들은 싱글톤의 형태로 관리된다.

### Bean 등록

```java
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ApplicationConfig {

    @Bean
    public BookRepository bookRepository() {
        return new BookRepository();
    }

    @Bean
    public BookService bookService() {
        BookService bookService = new BookService();
        bookService.setBookRepository(bookRepository());
        return bookService;
    }
}
```

```@Configuration```어노테이션을 통해 해당 파일이 Bean 설정 파일이라는 것을 알려주고 ```@Bean``` 어노테이션을 이용해서 Bean으로 등록.



## (3) Spring Boot에서 Bean을 등록하는 법

![image](https://user-images.githubusercontent.com/57162257/116520208-5f17db00-a90d-11eb-9b4c-1abe73b95b66.png)

```Component-scan```방식으로 위의 어노테이션들을 스캔해서 Bean으로 등록

Component-scan은 최상위 위치에 존재하는 ```@SpringBootApplication```어노테이션안에 존재하기 떄문에 SpringBoot에서 IoC컨테이너에 Bean을 쉽게 등록해서 의존성 주입을 할수 있다.

- Component-scan
  해당 패키지와 같거나 하위 패키지에 존재하는 어노테이션들만 스캔해서 Bean으로 등록할 수 있다.
  즉, 이니셜라이즈로 프로젝트를 생성했을때 [프로젝트명Application]에 ```@SpringBootApplication```어노테이션이 존재하니까 현재 위치의 디렉터리 혹은 하위 디렉터리에 존재하는 어노테이션들을 등록.

# 3.@Autowired



## (1)Autowired란 무엇인가

의존성을 주입하는 대표적인 방법

- 생성자 주입
- setter 주입
- 필드 주입

을 할때 ```@Autowired```어노테이션을 이용하면 의존성을 주입한다는 뜻

**[생성자 주입]**
![image](https://user-images.githubusercontent.com/57162257/116548056-a793c080-a92e-11eb-8fc7-7e9cfae9dfde.png)

![image](https://user-images.githubusercontent.com/57162257/116548555-5df7a580-a92f-11eb-8cc2-03a95cb73289.png)

1. TestController와 TestService는 애플리케이션이 실행되면 **```@ComponentScan```에 의해서 ```@RestController```, ```@Service``` 어노테이션이 붙어있는 클래스이므로 모두 IoC컨테이너에 Bean으로 등록**된다.

2. **```@Autowired```어노테이션을 통해 생성자 주입**을 하고 있기때문에 IoC컨테이너가 ```@Service```어노테이션에 의해 등록된 TestService객체를 만들어서 **TestController객체가 생성될때 의존성을 주입**해준다.

위의 과정으로 Spring이 의존성 주입을 해준다.

만약 IoC컨터이너에 등록되지 않은 Bean으로 의존성 주입을 하게 된다면?

![image](https://user-images.githubusercontent.com/57162257/116550053-36a1d800-a931-11eb-968f-58b7f8497313.png)

![image](https://user-images.githubusercontent.com/57162257/116550097-47524e00-a931-11eb-8a20-dc25fd387fcf.png)

그러면 Bean으로 등록되지 않았음을 프레임워크에서 인지하고 있고 컴파일 에러 발생.

![image](https://user-images.githubusercontent.com/57162257/116550317-95ffe800-a931-11eb-9f28-8bb4d04f55e3.png)

실행 시켜보면 TestController에서 찾을수 없는 TestService bean을 요구하고 있고 TestService를 bean타입으로 정의하라고 나와있다.

**[setter 주입]**
![image](https://user-images.githubusercontent.com/57162257/116548224-e295f400-a92e-11eb-995a-b37781470db4.png)

**[필드 주입]**
![image](https://user-images.githubusercontent.com/57162257/116548271-f17ca680-a92e-11eb-978d-ac9895869d98.png)

필드 주입은 테스트할때 의존성 주입의 어려움을 줄수 있는 등의 불편 사항이 있기때문에 Spring에서도 권장하지 않는 방법이다.

```java
public class Car{
    @Autowired
    private Tire tire;
}
```

```java
class CarTest{
    @Test
    void Test(){
        Tire koreaTire = new KoreaTire();
        Car car = new Car();	//내가 KoreaTire를 의존성 주입하고 싶은데 넣을수가 없어서 테스트를 할수 없다.
        System.out.println(car);
    }
}
```

# 4.Bean LifeCycle

스프링 빈은 ```객체 생성 -> 의존관계 주입``` 이라는 라이프사이클을 가진다.

즉, **스프링 빈은 의존관계 주입이 다 끝난 다음에야 필요한 데이터를 사용할 수 있는 준비가 완료된다.**

## (1) Spring 의존관계 주입 과정

![image-20210429213209487](C:\Users\leehyunjong\AppData\Roaming\Typora\typora-user-images\image-20210429213209487.png)

위 그림은 Spring Boot에서 ```Component-Scan```을 이용해 Bean 등록을 시작하는 과정의 그림

1. ```@Configuration```, ```@RestController```, ```@Service``` 등 Bean으로 들록할 수 있는 어노테이션들과 설정파일을 읽어 IoC컨테이너 안에 Bean으로 등록
   ![image](https://user-images.githubusercontent.com/57162257/116551188-9fd61b00-a932-11eb-93a3-5afe704c8596.png)
   그리고 의존 관계를 주입하기 전의 준비 단계가 있다.
   이 단계에서 객체의 생성이 일어나는데

   - 생성자 주입 : **객체의 생성, 의존관계 주입이 동시**에 일어남
   - setter 주입, 필드 주입 : **객체의 생성 -> 의존관계 주입**으로 라이프 사이클이 나뉘어짐.

   ``생성자 주입``이 객체의 생성과 의존관계 주입이 동시에 일어나는 이유는

   ```java
   public class Car{
       private Tire tire;
       
       public Car(Tire tire){
           this.thire=thire;
       }
   }
   ```

   위 의 코드에서 볼때 Car의 객체가 생성될때 Tire클래스와의 의존관계가 존재하지 않는다면 Car객체를 생성할수 없게 된다.
   그러므로 **생성자 주입에서는 ``객체 생성과 의존관계 주입``이 하나의 단계에서 일어난다.**

2. 코드에 작성된 의존관계를 보고 IoC컨테이너에서 의존성 주입을 해준다.
   이런 과정은 Bean생명주기와 관련이 있고 간단한 흐름!!![image](https://user-images.githubusercontent.com/57162257/116551664-1a069f80-a933-11eb-8fea-2f901cf4d928.png)



## (2) Bean LifeCycle

1. 스프링 컨테이너 생성
2. 스프링 빈 생성
3. 의존관계 주입
4. 초기화 콜백
5. 사용
6. 소멸 전 콜백
7. 스프링 종료

- 초기화 콜백 : 빈이 생성되고 빈의 의존관계 주입이 완료 된 후 호출
- 소멸전 콜백 : 빈이 소멸되기 직전에 호출

**``객체의 생성과 초기화를 분리하자``**
생성자는 필수 정보를 받고, 메모리를 할당해서 객체를 생성하는 책임을 가진다.
초기화는 생성된 값들을 활용해서 외부 커넥션을 연결하는 등 무거운 동작을 수행한다.

``스프링의 빈 생명주기 콜백 지원``

- 인터페이스(InitializingBean, DisposableBean)
- 설정 정보에 초기화 메소드, 종료 메소드 지정
- @PostConstruct, @PreDestroy 어노테이션 지원

등의 방법이 있는데 그중 가장 권장하는 방법인 어노테이션 방법을 살펴보면

![image](https://user-images.githubusercontent.com/57162257/116556080-0c9fe400-a938-11eb-8dcb-3ac7eb8b9b42.png)

실행시켰을때 ``@PostConstruct``어노테이션의 초기화 콜백이 실행되는게 확인된다.





# 5.빈 스코프

Spring Bean이 컨테이너가 생성되고 종료될때까지의 과정을 ``Bean LifeCycle``이라고 하는데 Bean LifeCycle이 한번만 도는 이유는 Spring Bean은 기본적으로 **싱글톤 스코프** 로 생성되기 떄문이다. 

## (1) 싱글 톤 스코프

![image](https://user-images.githubusercontent.com/57162257/116565799-242f9a80-a941-11eb-9922-fc8374b7cae3.png)

싱글톤 스코프 빈을 조회하면 스프링 컨테이너는 항상 같은 인스턴스의 스프링 빈을 반환한다.

## (2) 프로토타입 스코프

![image](https://user-images.githubusercontent.com/57162257/116565849-2f82c600-a941-11eb-99c7-02f37721e633.png)

프로토 타입 스코프는 새로운 빈이 계속 생성된다.
**스프링 컨테이너는 프로토타입 빈을 생성, 의존관계 주입, 초기화 까지만 처리**

클라이언트에 빈을 반환하고, 이후 스프링 컨테이너는 생성된 프로토타입 빈을 관리하지 않는다. 프로토타입 빈을 관리할 책임은 프로토타입 빈을 받은 클라이언트에 있고 ``@PreDestory``같은 종료 메소드가 호출되지 않는다

