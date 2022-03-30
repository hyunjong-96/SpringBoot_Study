# @RequestBody vs @ModelAttribute



## @RequestBody

> Annotation indicating a method parameter should be bound to the body of the web request. The body of the request is passed through an HttpMessageConverter to resolve the method argument depending on the content type of the request. Optionally, automatic validation can be applied by annotating the argument with @Valid.
>
> 메서드 매개 변수를 나타내는 주석은 웹 요청 본문에 바인딩해야 합니다. 요청 본문은 HttpMessageConverter를 통해 전달되며 요청 콘텐츠유형에 따라 method 인수를 해결합니다. 필요에 따라 @Valid 인수에 주석을 달아 자동 검증을 적용할 수 있습니다.
>
> POST HTTP1.1 / requestbody
> Body:
> {"password" : "123123", "email" : "test@test.com"}

- @RequestBody는 요청 본문의 Json, XML, Text등의 데이터가 HttpMessageConverter를 통해 파싱되어 Java객체로 변환 된다.
- @RequestBody를 사용할 객체는 필드를 바인딩 할 생성자나 setter 메서드가 필요없다
- 하지만 직렬화를 위해 기본 생성자는 필수, 데이터 바인딩을 위한 필드명을 알기 위해 getter 메서드 필수



@RequestBody 어노테이션은 Http요청 본문(Json, XML)을 HttpMessageConverter를 통해 Java Object로 변환하는 역할을 한다

```java
[TestController.class]
@RequestMapping("/api/test")
@RestController
public class TestController {
	@PostMapping
	public ResponseEntity<RequestBodyDto> requestBodyTest(
		@RequestBody RequestBodyDto requestBodyDto
	){
		return ResponseEntity.ok().body(requestBodyDto);
	}
}
```

```java
[RequestBodyDto.class]
public class RequestBodyDto {
	private String name;
	private long age;
	private String password;
	private String email;

	public RequestBodyDto(){}

	public RequestBodyDto(
		String name,
		long age,
		String password,
		String email
	){
		this.name = name;
		this.age = age;
		this.password = password;
		this.email = email;
	}
}
```

```java
[TestControllerTest.class]
@WebMvcTest(TestController.class)
class TestControllerTest {
	@Autowired
	private MockMvc mvc;
	@Autowired
	private ObjectMapper objectMapper;

	@Test
	void requestBodyTest() throws Exception {
		String name = "테스터";
		long age = 27;
		String password = "123123";
		String email = "test@test.com";
		RequestBodyDto requestBodyDto
			= new RequestBodyDto(name, age, password, email);

		String content = objectMapper.writeValueAsString(requestBodyDto);
		mvc
			.perform(post("/api/test")
				.content(content)
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("name").value(name))
			.andExpect(jsonPath("age").value(age))
			.andExpect(jsonPath("email").value(email));
	}
}
```

RequestBody의 바인딩을 테스트 하기 위핸 TestController의 requestBody(), RequestBodyDto, TestControllerTest 이다.

TestController의 requestBody()에 요청을 보내게 되면 아래와 같은 에러가 발생한다.

```
No serializer found for class com.example.unittest.test.RequestBodyDto and no properties discovered to create BeanSerializer (to avoid exception, disable SerializationFeature.FAIL_ON_EMPTY_BEANS)
com.fasterxml.jackson.databind.exc.InvalidDefinitionException: No serializer found for class com.example.unittest.test.RequestBodyDto and no properties discovered to create BeanSerializer (to avoid exception, disable SerializationFeature.FAIL_ON_EMPTY_BEANS)
```

대강 해석해보면 RequestBodyDto와 BeanSerializer를 만들기 위한 속성을 찾을수 없다는 뜻이다.

위에서 설명했듯이 역직렬화를 하기 위해서는 Http요청 본문의 데이터를 바인딩 하기 위한 객체에 getter 메서드를 가져야 한다.

```java
[RequestBodyDto.class]
@Getter
public class RequestBodyDto {
	private String name;
	private long age;
	private String password;
	private String email;

	public RequestBodyDto(){}

	public RequestBodyDto(
		String name,
		long age,
		String password,
		String email
	){
		this.name = name;
		this.age = age;
		this.password = password;
		this.email = email;
	}
  
  //혹은
  public String getName(){
    return this.name;
  }
  public long getAge(){
    return this.age;
  }
  public String getPassword(){
    return this.password;
  }
  public String getEmail(){
    return this.email;
  }
}
```

이처럼 수정해주면 된다.

<img width="602" alt="image" src="https://user-images.githubusercontent.com/57162257/160879615-158a4c32-4333-478e-8475-1b62e2af1b74.png">
(요청 시 Body에 요청한 데이터가 들어가있는것을 확인할 수 있다.)

https://stackoverflow.com/questions/59578802/jackson-no-serializer-found-for-class-and-no-properties-discovered-to-cre

위의 설명에 따르면 Jackson은 public인 필드나 public getter 메서드가 있는 필드에서만 동작한다고 한다.

위의 RequestBodyDto는 private인 필드이고 getter 메서드가 없기 때문에 jackson이 동작을 하지 않게 되는것이다.

갑자기 Jackson이 튀어나왔다. 여기서 짤막하게 Jackson에 대해서 집고 넘어가자면

#### Jackson

- Json데이터 구조를 처리해 주는 라이브러리
- Jackson은 기본적으로 프로퍼티로 동작한다.
  - 자바에서 프로퍼티란 객체와 관련하여 이름 붙여진 속성
- 프로퍼티로 동작하기 때문에 객체의 getter를 기준으로 바인딩 해준다.

Jackson이 Json데이터 구조를 처리해 주는 역할을 하는 것을 알았다.

그렇다면 @RequestBody와 Jackson은 어떻게 작용해서 역직렬화 할수 있는 것일까?

@RequestBody를 통해  요청 바디를 바인딩할때 HttpMessageConvert를 사용하는데,  그 중 HttpJackson2HttpMessageConverter를 이용하여 내부적으로 ObjectMapper를 통해 JSON값으로 역직렬화하여 Controller에서 우리는 선언해준 객체 그대로를 사용할수 있게 되는 것이다.

(이 HttpJackson2HttpMessageConverter를 이름에서 아시다 시피 Jackson이 제공해 준다.)

(물론 contentType이 application/json 인 경우에 말이다!)



---



## @ModelAttribute

> Annotation that binds a method parameter or method return value to a named model attribute, exposed to a web view. Supported for controller classes with @RequestMapping methods.
>
> 메서드 매개 변수 또는 메서드 반환 값을 명명된 모델 속성에 바인딩하는 주석으로, 웹 뷰에 표시됩니다. @RequestMapping 메서드를 사용하는 컨트롤러 클래스에서 지원됩니다.
>
> POST HTTP1.1 / modelattribute
> Request params: id = 21 name = tester

- @ModelAttribute를 사용하면 Http 파라미터 데이터를 Java 객체에 매핑한다.
- @RequestBody와 다르게 객체의 필드에 접근해 바인딩 하기 위해서 setter 메서드가 필요하다.
- Query String 및 Form 형식이 아닌 데이터는 처리할 수 없다.



@ModelAttribute 어노테이션은 Http 파라미터들을 특정 Java Object에 바인딩하는 역할을 한다.

Http 요청 URL중 `test/modelattribute?id=21&name=tester` 과 같은 QueryString이나 요청 본문의 Form 형태의 데이터를 처리한다.

<img width="693" alt="image" src="https://user-images.githubusercontent.com/57162257/160867536-8f638f7b-dcbe-4b93-8343-dc41b6360a00.png" style="zoom:70%;" >
( 위의 포스트맨에서 form 형태의 데이터를 확인할수 있다. )

```java
[TestController.class]
@RequestMapping("/api/test")
@RestController
public class TestController {
	@GetMapping("/modelattribute")
	public ResponseEntity<ModelAttributeDto> modelAttributeTest(
		@ModelAttribute ModelAttributeDto modelAttributeDto
	){
		return ResponseEntity.ok().body(modelAttributeDto);
	}
}
```

```java
[ModelAttributeDto.class]
@Getter
public class ModelAttributeDto {
	private String name;
	private long age;
	private String password;
	private String email;

	public ModelAttributeDto(
		String name,
		long age,
		String password,
		String email) {
		this.name = name;
		this.age = age;
		this.password = password;
		this.email = email;
	}
}
```

```java
[TestControllerTest.class]
@WebMvcTest(TestController.class)
class TestControllerTest {
	@Autowired
	private MockMvc mvc;
	@Autowired
	private ObjectMapper objectMapper;

	@Test
	void modelAttributeTest() throws Exception {
		String name = "테스터";
		String age = "27";
		String password = "123123";
		String email = "test@test.com";
		mvc
			.perform(get("/api/test/modelattribute")
				.param("name",name)
				.param("age", age)
				.param("password", password)
				.param("email", email))
			.andExpect(status().isOk())
			.andExpect(jsonPath("name").value(name))
			.andExpect(jsonPath("age").value(age))
			.andExpect(jsonPath("password").value(password))
			.andExpect(jsonPath("email").value(email))
			.andDo(print());
	}
}
```

위의 테스트코드를 실행시켜보면 
<img width="584" alt="image" src="https://user-images.githubusercontent.com/57162257/160875567-aaa28648-701e-4693-ba75-cb6b0d7c664c.png">

Http 파라미터 값으로 각 데이터가 잘 요청되는 것을 확인할수 있고
<img width="601" alt="image" src="https://user-images.githubusercontent.com/57162257/160875795-af10171d-6796-4a3c-8472-08513e4d7f92.png">
바인딩이 잘되어 들어오는 것을 확인할 수 있다.

만약 @RequestBody에서 테스트한것과 같이 getter 메서드 없이 보낸다면?

<img width="528" alt="image" src="https://user-images.githubusercontent.com/57162257/160877271-ea3a8918-5145-4680-ad1f-dd3d8325ca41.png">
 해당 미디어타입에 접근 할수 없다고 406 에러가 발생한다.
(다른 미디어 타입을 넣어봐도 406이 계속 발생한다.)

GET메서드 방식은 key : value 형식으로 보내져서 content type이 필요없지만 @ModelAttribute를 통해 객체와 바인딩 하기 위해서는 setter나 바인딩하는 값들을 주입해주는 생성자가 필요한데 객체의 속성값을 비교하기 위해 getter메서드가 필요한것 같다.

그렇기 때문에 `@ModelAttrubute`로 Http 파라미터를 객체에 바인딩 하기 위해서는 `getter메서드`와 `setter메서도 또는 모든 필드에 대한 생성자`가 꼭 필요하다.

만약 POST메서드를 통해 파라미터 값이 아닌 body에 요청값을 넣어 @ModelAttribute로 객체를 바인딩 시켜주려고 한다면 @ModelAttribute는 요청값에 대한 바인딩을 수행할 수 없기 때문에 `415 Unsupported Media Type에러`가 발생하게 된다.

아까 설명 했듯이 @ModelAttribute는 QueryString이나 Form형식의 데이터만 바인딩 할수 있다고 했다.

그렇기 떄문에 POST로 요청하기 위해서는 

```java
...
mvc
			.perform(post("/api/test/modelattribute")
				.contentType(MediaType.MULTIPART_FORM_DATA)
				.flashAttr("modelAttributeDto", modelAttributeDto)
				// .param("name",name)
				// .param("age", age)
				// .param("password", password)
				// .param("email", email)
			)
			.andExpect(status().isOk())
			.andExpect(jsonPath("name").value(name))
			.andExpect(jsonPath("age").value(age))
			.andExpect(jsonPath("password").value(password))
			.andExpect(jsonPath("email").value(email))
			.andDo(print());
...
```



참고로 테스트에서 요청 파라미터를 하나 하나 작성하는데 번거롭기 때문에 flashAttr메서드를 통해 객체화 한 후 요청하면 더 편리하게 사용할수 있다.

```java
...
mvc
			.perform(get("/api/test/modelattribute")
				.flashAttr("modelAttributeDto", modelAttributeDto)
				// .param("name",name)
				// .param("age", age)
				// .param("password", password)
				// .param("email", email)
			)
			.andExpect(status().isOk())
			.andExpect(jsonPath("name").value(name))
			.andExpect(jsonPath("age").value(age))
			.andExpect(jsonPath("password").value(password))
			.andExpect(jsonPath("email").value(email))
			.andDo(print());
...
```





## 참조

https://tecoble.techcourse.co.kr/post/2021-05-11-requestbody-modelattribute/

https://www.inflearn.com/questions/15242

https://jojoldu.tistory.com/407

https://mommoo.tistory.com/83

https://atoz-develop.tistory.com/entry/JAVA%EC%9D%98-%EA%B0%9D%EC%B2%B4-%EC%A7%81%EB%A0%AC%ED%99%94Serialization%EC%99%80-JSON-%EC%A7%81%EB%A0%AC%ED%99%94

https://pygmalion0220.tistory.com/entry/HTTP-Content-Type