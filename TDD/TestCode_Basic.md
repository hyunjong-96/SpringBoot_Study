# Spring Boot Test



## 왜 우리는 테스트 코드를 작성해야 하는 것인가?

1. 확인
   - 단위 테스트 시 해당 비즈니스 로직이 잘 수행되고 있다는 것을 확인할수 있다.
2. 시간 절약
   - 데이터를 저장하는 비즈니스 로직을 구현중일때 코드를 실행시켰을때 원하는 값대로 저장이 되지 않았다면 다시 데이터를 삭제하고 다시 저장해야하는 반복.
   - 서버를 빌드하는데 까지 걸리는 시간을 절약할수 있다.
3. 리팩토링
   - 코드의 수정사항이 생겨 수정 후 다시 테스트를 하려면 위의 과정을 반복해야한다. 하지만 테스트 코드를 작성하면 버튼 하나만 누르면 테스트 완료
4. 문서의 역할
   - 코드를 처음 보는 개발자들이 해당 비즈니스 로직에 대한 이해를 테스트 코드를 보고 어떤 값을 넣어야하고 어떤 값이 반환되는 것인지 파악할수 있다.
   - 또한 예외에 대한 테스트 코드도 작성할 수 있어 어떤 예외가 발생하는지 알 수 있다.

이 밖의 다양한 장점들이 많다.

테스트 코드에는 단위 테스트, 통합 테스트가 있다.

먼저 통합 테스트는 말 그대로 Spring Boot의 모든 자원을 사용해서 전체적인 비즈니스 로직을 테스트 하는 것을 말한다. 그렇기 때문에 서버를 직접 실행한 것과 마찬가지로 무겁고 오래걸린다.

이에 반해 단위 테스트는 각 계층(Controller, Service, Repository)을 분리하여 해당 계층에서만 필요한 것들로만 가볍고 빠르게 코드를 확인하는 것을 말한다.

먼저 단위 테스트와 관련된 것들을 알아보자.

단위 테스트를 할 때는 가볍고 빨라야 한다고 했다. 그렇기 때문에 최대한 테스트에 꼭 필요한 것들만 남기고 나머지는 사용하지 않아야하는데 즉, **'테스트 하는 코드가 사용되지 않는 코드와 확실하게 격리되어야 한다'**는 것이다. 이를 해결하는 방법이 `테스트 더블(Test Double)`이라고 한다.

테스트 더블에는 Mockito, Stub, Spy 등이 있다.

## Mockito

`@Mock`을 사용하게 되면 테스트 코드(단위 테스트)에서 행위 조작이 가능한 껍데기 mock객체를 생성할수있다.

mock객체를 생성해서 의존성을 해결할 수 있다.

예를 들어 Controller의 테스트를 할 때 Service빈을 의존성 주입 받는다. 그렇기 때문에 Controller의 테스트 코드를 작성할 때도 Spring Context에 의존성 주입 받을 Service를 빈으로 등록 시키고 Controller에 주입을 시켜줘야한다. 하지만 단위 테스트는 위에서 말 한 것처럼 가볍고 빨라야 하기 때문에 mock으로 빈으로 등록할 Service를 껍데기 객체로 만들어줘 의존성 주입을 한 척 만 하게 해주는 것이다.

`@Spy` 는 Stub 해주지 않은 메소드들은 원본 메소드 그대로 사용하는 어노테이션

`@InjectMock` 은 @Mock이나 @Spy를 통해 생성된 가짜 객체를 자동으로 주입시켜주는 어노테이션

아래 코드를 보게 되면

```java
@ExtendWith(MockitoExtension.class)	//1
public class UserServiceTest{
  @Mock	//2
  UserRepository userRepository;
  @InjectMock	//3
  UserService userService;
  
  @Display("회원 불러오기")
  @Test
  public getUserTest(){
    //given
    User saveUser = User.builder()
      .id(1L)
      .name("테스터")
      .age(27)
      .build();
    UserDto userDto = UserDto.builder()
      .name(saveUser.getName())
      .age(saveUser.getAge())
     	.build();
    given(userRepository.findById(saveUser.getId())).willReturn(saveUser);	//4
    
    //when
		final UserDto result = userService.getUserInfo(1L);	//5
    
    //then
    assertThat(result.getName()).isEqualTo(userDto.getName());	//6
    assertThat(result.getAge()).isEqualTo(userDto.getAge());
  }
}
```

1. @ExtendWith(MockitoExtension.class)
   - Mockito도 테스팅 프레임 워크 이기 때문에 JUnit과 결합이 되기 위해서는 별도의 작업이 필요하다.
   - 기존의 JUnit4에서는 @RunWith(MocktoJUnitRunner.class)를 붙여줘야 연동이 가능했다.
   - JUnit5에서는 @ExtendWith(MockitoExtension.class)를 사용해준다.
2. @Mock
   - @Mock 어노테이션은 위에서 설명 했듯이 Mock객체를 만들어 주는 어노테이션이다.
   - @Mock과 비슷한 역할을 하는 @MockBean도 있다.
3. @InjectMock
   - @Mock으로 생성해준 가짜 객체를 의존성 주입해줄때 사용하는 어노테이션
4. given()
   - @Mock을 통해 만들어진 가짜 객체는 기존 메소드를 사용할수 없으므로 의존성 주입된 객체 내에서 기존 메소드의 결괏값을 준비시켜야한다.
   - given, when 이 있으며 각 argument에는 주입된 가짜 객체가 실행할 메소드를 작성한다
   - 그 다음 메소드 체이닝(willReturn() 등)을 통해 예상되는 결괏값을 argument에 넣어주면된다.
5. userService.getUserInfo()
   - mock과 가짜 결괏값을 설정해 주었고 그 것들의 결과로 나온 값을 반환받는다(결과물)
6. assertThat().isEqualTo()
   - JUnit5에서는 assertEqual()와 같은 비교 메소드를 지원해준다.
   - 하지만 조금더 직관적(?)인 메소드를 제공해주는 AssertJ의 메소드를 사용해 좀 더 풍부한 문법과 메서드 체이닝을 사용할수 있다.
   - AssertJ의 assertThat()은 argument에 비교할 값을 넣고 그 뒤에 연결되는 isEqualTo()를 통해 비교 대상을 넣어 true인지 여부를 확인한다.



### @Mock 과 @MockBean



## JUnit5

### JUnit5란?

자바에서 사용하는 테스팅 기반 프레임워크

Java8 이상 부터 사용이 가능하며 JUnit, Jupiter, Junit Vintage 결합한 형태

- JUnit : 테스트를 실행해주는 런처와 TestEngine API를 제공
- Jupiter : TestEngine API 구현체로 JUnit5에서 제공
- Vintage : TestEngine API 구현체로 JUnit3, 4에서 제공

<img width="678" alt="image" src="https://user-images.githubusercontent.com/57162257/160287294-64768c42-12a4-4028-af97-805474a42179.png" style="zoom:50%;" >



| 어노테이션      | 설명                     | Bean          |
| --------------- | ------------------------ | ------------- |
| @SpringBootTest | 통합 전체 테스트         | Bean 전체     |
| @WebMvcTest     | 단위 테스트,  MVC 테스트 | MVC 관련 Bean |
| @DataJpaTest    | 단위 테스트, JPA 테스트  | JPA 관련 Bean |