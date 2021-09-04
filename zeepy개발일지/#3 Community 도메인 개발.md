# #3 Community 도메인 개발

## 상태코드

```text
200 OK - 요청 성공
201 Created - 요청에 따른 새로운 리소스 생성 성공
204 No Content - 요청은 성공했지만 딱히 보내줄 내용이 없음
400 Bad Request - 잘못된 요청
401 Unauthorized - 비인증 요청
403 Forbidden - 비승인 요청
404 Not Found - 존재하지 않는 리소스에 대한 요청
500 Internal Server Error - 서버 에러
503 Service Unavailable - 서비스가 이용 불가능함
```

---------------------------------

## @DataJpaTest

테스트케이스를 작성하면서 테스트에 `@SpringBootTest`어노테이션을 사용했는데 작곰이가 `@DataJpaTest`를 작성하라고 했다. `@DataJpaTest`가 무엇일까?

일단 그전에 @RunWith에 대해서 알아봐야한다

### @RunWith

> Junit테스트의 lifecycle및 테스트가 어떻게 실행시킬 것인지 정의

### @RunWith(SpringRunner.class)

> @Mock과 @Autowired등의 기능을 JUnit에서 상요할 수 있도록 해준다.
>
> 즉, 스프링 부트 테스트와 JUnit사이에 연결자 역할

### @SpringBootTest

> ApplicationContext를 모두 적재하기때문에 시간이 오래걸린다
>
> 유닛테스트에 사용하는건 아님

`JUnit4라면 @RunWith(SpringRunner.class)와 @DataJpaTest`

------------------------------------

## @WebMvcTest

webmvc를 테스트할수 있는 어노테이션.

웹 관련 설정만 Spring이 로딩해서 보통 ControllerTest에 사용.

http://wonwoo.ml/index.php/post/1926

------------------------------

## @MockBean

`@SpringBootTest` : `@SpringBootApplication`을 찾아서 테스트를 위한 빈들을 다 생성하고 `@MockBean`으로 정의된 빈을 찾아서 교체. 그리고 반드시 `@RunWith(SpringRunner.class)`와 함께 써야한다.

- `SpringBootTest.webEnvironment`
  - MOCK : 내장 톰캣 구동 안함
  - RANDOM_PORT : 실제 내장 톰캣 사용(MockBean대신 RestTemplate사용), 실제 가용한 포트로 내장톰캣을 띄우고 응답을 받아서 테스트 수행

`@MockBean` : @SpringBootTest를 사용한 테스트는 너무 크기가 크고 Controller테스트코드에서 Service단계까지 흘러간다. 그리고 ApplicationContext에 들어있는 빈을 Mock으로 만든 객체로 교체한다.
MockBean을 사용하면 구현체가 없어도 테스트가 가능하다(@MockBean으로 등록된 service는 껍데기 service이다.)

https://shinsunyoung.tistory.com/52



## @MockBean과 @Mock차이

- @Mock
  - `@RunWith()`을 사용해주면 `@Mock`을 사용해줄수 있는데 @Mock은 실제 의존성이 아닌 껍데기 의존성(SpringBoot를 사용하지 않아도 된다)
- @MockBean
  - @MockBean은 `org.springframework.boot.test-mock.mockito`패키지에 존재하는 springboot-test에서 제공하는 어노테이션이다. Mockito의 Mock객체들을 Spring의 ApplicationContext에 넣어주는데 동일한 타입의 Bean일 경우 MockBean으로 변경해준다고 한다.
- 사용방법
  - **springboot container**가 필요하고 **Bean이 container에 존재**해야한다면 @MockBean사용
  - 그렇지 않다면 @Mock

------------------------

## BDD

controller로직에선 serivce로직을 테스트하지말아야하고 service로직에서는 repository로직을 테스트하지 말아야한다.

그렇기 떄문에 controller로직에서는 껍데기service(mock빈 service), service로직에서는 껍데기repository(mock빈 repository)를 사용한다.

하지만 다음 로직에서 어떻게 행동할 것인지에 대해 가정을하면 test의 flow를 알수있게된다.

- `given(communityService.save(any(SaveDto.class))).willReturn(1L); ` : controller테스트
- `when(communityRepository.save(any(Community.class))).thenReturn` : service테스트 

직접적인 영향을 주지는 않지만 `@MockBean`으로 선언된 service의 함수의 결과값을 예상하여 `mockMvc.perform()`의 결과값에 반영이된다.

## controllerTest

```java
//given
given(communityService.save(any(SaveDto.class))).willReturn(1L);

//when
Long saveId = communityService.save(requestDto);

//then
assertThat~;
doPost(url, request);


```

- given
  BDD의 mockito.given메소드를 사용하여 mockito.given메소드를 사용하여 Service로직의 반환을 예상한다.

- when

  - assertThat을 사용하여 값을 비교할때는 @InjectMocks로 Mock설정된 service로 값을 반환받아온다
  - 이떄의 값은 given에서 예상반환된 값이 들어오게된다.

- then

  - assertThat을 사용해 service로직에 반환된 값들을 비교한다

  - 또는 mockMvc.perform을 통해 httpMethod로 controller를 테스트한다.

    ```java
    protected <T> ResultActions doPost(String path, T request) throws Exception {
            return mockMvc.perform(post(path)
                    .content(objectMapper.writeValueAsBytes(request))
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isCreated())
                    .andExpect(header().string(HttpHeaders.LOCATION, path + "/1"))
                    .andDo(MockMvcResultHandlers.print());
        }
    ```

    

## servierTest

```java
//given
long communityId = 1L;
        JoinCommunityRequestDto requestDto = new JoinCommunityRequestDto(null, 2L);

        Community community = createCommunity();
        User user = createJoinUser();
        Participation participation = createParticipation(community, user);

        when(communityRepository.findById(any(Long.class))).thenReturn(Optional.of(community));
        when(userRepository.findById(any(Long.class))).thenReturn(Optional.of(user));
        when(participationRepository.save(any(Participation.class))).thenReturn(participation);
        when(participationRepository.save(any(Participation.class))).thenReturn(participation);
		when(participationRepository.findAll()).thenReturn(Collections.singletonList(participation));
	

//when
Long participationId = communityService.joinCommunity(communityId, requestDto);

//then
Participation newParticipation = participationRepository.findById(participationId).orElseThrow(NotFoundParticipationException::new);

        assertThat(newParticipation.getCommunity().getId()).isEqualTo(community.getId());
        assertThat(newParticipation.getUser().getId()).isEqualTo(user.getId());
```

- given
  - communityService에서 사용될 repository를 통한 결과값들을 Mockito.when을 통해 예상해서 thenReturn으로 넣어준다.
- when
  - Mock으로 설정된 communitytService를 실행시킨다.
- then
  - participationRepository에서 가져온 값을 가지고 communityService로직의 결과물과 assertThat을 이용해 비교한다.

----------------------------------

## MultiValueMap<String,String> params

MultiValueMap<String, String> params = new LinkedMultiValueMap<>();

로 mockMvc로 getMethod를 테스트할때 params를 넣을때 사용

https://okky.kr/article/517350

------------------------------------

## 제네릭 메소드

```java
public class CoffeeMachine {

    public <T> Coffee makeCoffee(T capsule) {

        return new Coffee(capsule);
    }
}
```

```java
public class Box{
    CoffeMachine coffeMachine = new CoffeMachine();
    Colombian capsule = new Colombian();
    coffeMachine.<Colombian>makeCoffe(capsule);
    coffeMachine.makeCoffee(capsule);//타입 추정이 가능하므로 생략이 가능.
}
```

---------------------------

## Bean Validation

```java
implementation('org.springframework.boot:spring-boot-starter-validation')
```

의존성 추가를 먼저해준다(spring-boot-starter-web이 validation을 포함하고 있기떄문에 따로 추가해줄필요는 없다고한다.)

### 정의

기본적으로 Bean Validation은 클래스 필드에 특정 어노테이션을 달아 제약조건을 정의하는 방식으로 동작.

### 방법

- request body
- path에 포함된 variables
- query parameters

**Request Body**

 post혹은 put요청에서 request body에 validation어노테이션을 다는방법.(즉, dto에 작성해준다.)

```java
@Getter
@Setter
public class InputRequestDto{
    @Min(1)
    @Max(10)
    private int numberBetweenOneAndTen;//1~10사이의 값을 가져야한다.
    
    @NotEmpty
    private String notEmptyString;//빈 문자열이 아니여야한다.
    
    @Patter(regexp = "^[0-9]{6}")//pin코드 6자리
    private String pinCode;
}
```

```java
@RestController
public class ValidateRequestBodyController{
    @PostMapping("/validateBody")
    ResponseEntity<String> validateBody(@Valid @RequestBody InputRequestDto dto){
        return ResponseEntity.ok("valid");
    }
}
```

- `@RequestBody`어노테이션 앞에 `@Valid`어노테이션을 작성해줌으로써 다른 작업을 수행하기 전에 객체를 먼저 Validator에 전달해서 유효성검사를한다.
- 유효성 검사에 실패한 경우 `MethodArgumentNotValidException`예외 발생, Spring은 이 예외를 400Status으로 변환한다



https://jongmin92.github.io/2019/11/18/Spring/bean-validation-1/

-----------------------------------

## CustomException

### @Valid

- spring-boot-stater-web에 하위 의존 또는 spring-boot-starter-validation라이브러리

- Bean Validation(어노테이션) 

  - @NotBlank

    - null 허용x
    - 문자 한개 이상 있어야함

  - @Email

    - 이메일 형식

  - @Pattern

    - 정규표현식에 맞는 문자열이어야 함

    - - (?=.*[0-9])

      - - 숫자 적어도 하나

      - (?=.*[a-zA-Z])

      - - 영문 대,소문자중 적어도 하나

      - (?=.*\\W)

      - - 특수문자 적어도 하나

      - (?=\\S+$)

      - - 공백 제거

  - @NotEmpty

  - - null과 공백 문자열("") 을 허용하지 않음
    - **enumType.String 필드에 적용해야함**

### ExceptionHandler

**프로세스**

1. request body를 받는 Dto에서 `@Valid(Bean Validation)`를 받은 필드가 유효성검사에서 실패하면 `MethodArgumentNotValidException`발생
2. ControllerException클래스의 `@RestControllerAdvice`를 통해 Controller에서 발생하는 Exception을 캐치,
3. `@ExceptionHandler(MethodArgumentNotValidException.class)`가 선언된 커스텀익셉션할 메소드가 MethodArgumentNotValidException를 받음
4. Errors를 상속받은 BindingResult를 통해 Bean Validation을 통과하지 못한 필드와 내용들을 필터링해서 커스텀한다.
5. ResponseEntity에 묶어서 보낼 ErrorResponse에 `status, message` 그리고 Errors의 `FieldError`(`field, value(rejectedValue), reason(defaultMessage)`)들을 리스트로 묶어서 보내준다.

**코드**

```java
[InvalidBodyException.class] 
@Getter
public class InvalidBodyException extends CustomException{

    public InvalidBodyException(){
        super(ErrorCode.INVALID_BODY);
    }
}
```

```java
[CustomException.class] 
@Getter
public class CustomException extends RuntimeException {
    private ErrorCode errorCode;
public CustomException(ErrorCode errorCode){
    super(errorCode.getMessage());
    this.errorCode = errorCode;
}
```
```java
[CustomExceptionHandler.class]    
@ExceptionHandler(InvalidBodyException.class)
    public ResponseEntity<ErrorResponse> handleInvalidBodyException(InvalidBodyException e){
        logger.error("handleInvalidBodyException: "+e);

        final ErrorCode errorCode = e.getErrorCode();

        final ErrorResponse response
                =ErrorResponse.create().
                status(errorCode.getStatus()).
                message(e.getMessage()).
                errors(e.getErrors());

        return new ResponseEntity<>(response, HttpStatus.resolve(errorCode.getStatus()));
    }
```

```java
[ErrorResponse.class] 
@NoArgsConstructor
@Getter
public class ErrorResponse {
    private final LocalDateTime timeStamp = LocalDateTime.now();

    private int status;

    private String message;

    private List<CustomFieldError> customFieldErrors;

    public static ErrorResponse create(){
        return new ErrorResponse();
    }

    public ErrorResponse status(int status){
        this.status = status;
        return this;
    }

    public ErrorResponse message(String message){
        this.message = message;
        return this;
    }

    public ErrorResponse errors(Errors errors){
        setCustomFieldErrors(errors.getFieldErrors());
        return this;
    }

    //BindingResult.getFieldErrors()메소드를 통해 전달받은 fieldErrors
    public void setCustomFieldErrors(List<FieldError> fieldErrors){//필드에러를 Errors에서 반환해줌
        customFieldErrors = new ArrayList<>();

        fieldErrors.forEach(error-> customFieldErrors.add(new CustomFieldError(
                    error.getCodes()[0],
                    error.getRejectedValue(),
                error.getDefaultMessage()
        )));
    }

    //유효성 검증에 통과하지 못한 필드가 담길 클래스
    @Getter
    public static class CustomFieldError{
        private final String field;
        private final Object value;
        private final String reason;

        public CustomFieldError(String field,Object value,String reason){
            this.field = field;
            this.value = value;
            this.reason = reason;
        }
    }
}
```



**결과**
![image](https://user-images.githubusercontent.com/57162257/117912285-31259400-b31a-11eb-8614-db977c66edf0.png)

> `ExceptionHandler를 통한 customException구현`
>
> https://bamdule.tistory.com/92 (도움 많이됨)
>
> https://cheese10yun.github.io/spring-jpa-best-02/ (도움많이됨)
>
> https://jongmin92.github.io/2019/11/18/Spring/bean-validation-1/#Spring-Service%EC%9D%98-Validation
>
> https://velog.io/@hellozin/Valid-%EC%98%88%EC%99%B8%EB%A5%BC-%EC%A0%84%EC%97%AD-%EC%BB%A8%ED%8A%B8%EB%A1%A4%EB%9F%AC%EB%A1%9C-%EA%B0%84%EB%8B%A8%ED%95%98%EA%B2%8C-%EC%B2%98%EB%A6%AC%ED%95%98%EA%B8%B0
>
> https://medium.com/chequer/spring-methodargumentnotvalidexception-valid-%EC%98%88%EC%99%B8%EC%B2%98%EB%A6%AC-2f63e8087759
>
> `@Valid에 대한것`
>
> https://shuiky.tistory.com/entry/bean-validation%EC%97%90-%EB%8C%80%ED%95%B4%EC%84%9C
>
> https://victorydntmd.tistory.com/332
>
> `Errors객체`
>
> https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/validation/Errors.html
>
> `CustomException 테스트코드`
>
> https://lucaskim.tistory.com/40

--------------------------

# Optional

Optional은 Java 8에 추가된 `null`을 잘 다룰수 있도록 도와주는 class

## NullPointerException

참조를 할때 null인 객체를 참조하려고하면 NullPointerException이라는 예외가 발생하여 실행이 중단되는데 이를 막기위해 try,catch문을 이용해서 null처리를 하여 if문으로 null을 처리하여 가독성을 해칠수 있다.

## Method

예를 들어서 설명하겠다.

### Optional< T > findById

JPA에 존재하는 메소드로 id를 파라미터로 넣으면 Optional로 반환 타입을 한번 감싸서 값을 반환한다.

### orElse(T other)

```java
//BAD
Optional<Member> member = ...;
if (member.isPresent()) {
    return member.get();
} else {
    return null;
}

//GOOD
Optional<Member> member = ...;
return member.orElse(null);
```

- member를 받아왔고 memeber의 값이 null이라면 null을 반환
- 그렇지 않다면 null반환

### orElseThrow()

```java
//BAD
Optional<Member> member = ...;
if(member.isPresent()){
    return member.get();
}else throw new NoSucheElementException();

//GOOD
Optional<Member> member = ...;
return member.orElseThrow(()-> new NoSuchElementException());
```

- orElse와 비슷하지만 exception을 발생시킬떄 사용한다.(나는 customException을 부를때 사용)

### orElseGet(()->new...)

> orElse(...)에서 ...는 Optional에 값이 있든 없는 무조건 실행된다.
>
> ...가 새로운 객체를 생성하거나 연산을 수행하는 경우에는 orElse()대신 orElseGet()을 사용해야한다.

`member.orElse(new makeMember)`라고 한다면 Optional의 member안에 값이 있든 없든 makeMember객체가 생성됬다는 가정하에 실행되기 떄문에 makeMember는 무조건 생성되어 비효율적이다.
`즉, orElse(...)는 ...가 새 객체 생성이나 새로운 연산을 유발하지 않고 이미 생성되었거나 이미 계산된 값일 때만 사용한다.`

orElseGet(...)에서 ...은 Optional에 값이 없을 때만 실행되기떄문에 불필요한 오버헤드가 없다.

orElse와 orElseGet을 헷갈리지 말자!



## Optional사용에 주의할점

Optional은 비싸기 떄문에 사용에 주의해야하고 최대한 줄이는 것이 좋다고한다.

### 값을 얻을 목적이면 Optional대신 null비교

```java
//BAD
return Optional.ofNullable(status).orElse(READY);
//ofNullable은 null이 허용되는 Optional로 wrapp

//GOOD
return status != null ? status : READY;
```

### Optional대신 비어있는 컬렉션 반환

```java
//BAD
List<Member> team.getMembers();
return Optional.ofNullable(member);

//GOOD
List<Member> members = tea.getmembers();
return members != null ? members : Collections.emptyList();
```

- 컬렉션은 null이 아니라 비어있는 컬렉션을 반환하는 것이 좋을때가 많다.
- Optional을 감싸서 반환하지 말고 비어있는 컬렉션을 반환하자.
- 참고로 JPA에서 객체들의 list를 불러올때 값이 없다면 빈 배열을 반환해준다. 이때 List< Member >로 하고, Optional<List< Member >>방법은 추천하지 않는다.



출처 : http://homoefficio.github.io/2019/10/03/Java-Optional-%EB%B0%94%EB%A5%B4%EA%B2%8C-%EC%93%B0%EA%B8%B0/

-------------------------------------

## 오류

## 1

> org.springframework.http.converter.HttpMessageConversionException: Type definition error: [simple type, class com.kt.isearch.web.model.MainDomain]; nested exception is com.fasterxml.jackson.databind.exc.InvalidDefinitionException: Cannot construct instance of `com.kt.isearch.web.model.MainDomain` (no Creators, like default construct, exist): cannot deserialize from Object value (no delegate- or property-based Creator)
>
>  at [Source: (PushbackInputStream); line: 1, column: 2]

오류 코드중에 `no Creators, like default construct, exist`  이 파라미터 입력을 받지 않는 default생성자가 없어서 발생하는 오류이다.

default생성자를 만들기 위해선 `@NoArgsConstructor`을 넣어주면된다.

또한 default생성자는 Jackson라이브러리가 사용하기 떄문에 dto에는 deafult생성자를 만들어주는것이 좋다

## 2

> #### **Error creating bean with name 'reportController' defined in file [file 주소]**
>
> #### **Unsatisfied dependency expressed through constructor parameter** 0; nested exception is org.springframework.beans.factory.NoSuchBeanDefinitionException:
>
> #### **expected at least** 1 bean which qualifies as autowire candidate. Dependency annotations: {}



스프링 컨터이너가 Bean을 등록받지 못했다는 오류



## 3

> ![image](https://user-images.githubusercontent.com/57162257/126681749-8cc2dc1b-c819-4e26-b6e2-7c58fc78013f.png)



**org.springframework.web.util.NestedServletException: Request processing failed; nested exception is org.springframework.http.converter.HttpMessageConversionException: Type definition error: [simple type, class org.springframework.security.core.GrantedAuthority]; nested exception is com.fasterxml.jackson.databind.exc.InvalidDefinitionException: Cannot construct instance of `org.springframework.security.core.GrantedAuthority` (no Creators, like default constructor, exist): abstract types either need to be mapped to concrete types, have custom deserializer, or contain additional type information
 at [Source: (PushbackInputStream); line: 1, column: 520] (through reference chain: com.zeepy.server.community.dto.SaveCommunityRequestDto["user"]->com.zeepy.server.user.domain.User["authorities"]->java.util.Collections$SingletonList[1])**

이유인 즉슨, community.dto.SaveCommunityRequestDto["user"]에서 나타나는 문제로 reqDto에 user엔티티가 존재하게 되면 요청을 json을 java로 받는 jackson이라는 친구가 엔티티를 인식하지 못해 화내는 에러.

![image](https://user-images.githubusercontent.com/57162257/126682469-6a0862b6-8c15-492b-80c9-23dd28c010db.png)

community save 테스트 로직에서 SaveCommunityRequestDto에 user엔티티를 만들어서 req요청을 보내는것을 확인할수 있다. 위에 설명했던대로 json을 java로 받는 jackson이 req요청에 들어있는 user엔티티를 읽지 못해 발생하는 에러로! 반성하자.ㅎ

