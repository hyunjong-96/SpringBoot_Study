# #3 Community 도메인 개발

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

------------------------

## BDD

controller로직에선 serivce로직을 테스트하지말아야하고 service로직에서는 repository로직을 테스트하지 말아야한다.

그렇기 떄문에 controller로직에서는 껍데기service(mock빈 service), service로직에서는 껍데기repository(mock빈 repository)를 사용한다.

하지만 다음 로직에서 어떻게 행동할 것인지에 대해 가정을하면 test의 flow를 알수있게된다.

- `given(communityService.save(any(SaveDto.class))).willReturn(1L); ` : controller테스트
- `when(communityRepository.save(any(Community.class))).thenReturn` : service테스트 

직접적인 영향을 주지는 않지만 `@MockBean`으로 선언된 service의 함수의 결과값을 예상하여 `mockMvc.perform()`의 결과값에 반영이된다.

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

1. request body를 받는 Dto에서 `@Valid(Bean Validation)`를 받은 필드가 유효성검사에서 실패하면 `MethodArgymentNotValidException`발생
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

-------------------------------------

## 오류

> org.springframework.http.converter.HttpMessageConversionException: Type definition error: [simple type, class com.kt.isearch.web.model.MainDomain]; nested exception is com.fasterxml.jackson.databind.exc.InvalidDefinitionException: Cannot construct instance of `com.kt.isearch.web.model.MainDomain` (no Creators, like default construct, exist): cannot deserialize from Object value (no delegate- or property-based Creator)
>
>  at [Source: (PushbackInputStream); line: 1, column: 2]

오류 코드중에 `no Creators, like default construct, exist`  이 파라미터 입력을 받지 않는 default생성자가 없어서 발생하는 오류이다.

default생성자를 만들기 위해선 `@NoArgsConstructor`을 넣어주면된다.

또한 default생성자는 Jackson라이브러리가 사용하기 떄문에 dto에는 deafult생성자를 만들어주는것이 좋다



> #### **Error creating bean with name 'reportController' defined in file [file 주소]**
>
> #### **Unsatisfied dependency expressed through constructor parameter** 0; nested exception is org.springframework.beans.factory.NoSuchBeanDefinitionException:
>
> #### **expected at least** 1 bean which qualifies as autowire candidate. Dependency annotations: {}



스프링 컨터이너가 Bean을 등록받지 못했다는 오류