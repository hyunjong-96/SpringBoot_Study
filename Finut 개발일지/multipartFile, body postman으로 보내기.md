# MultipartFile과 Body 함께 요청하기

회원가입 등과 같이 이미지와 함께 요청을 해야하는 경우 전에는 postman에서 mutlpartFile과 body값을 함께 보내지 못해 항상 이미지만 보내는 api를 따로 분리하거나 테스트 배포시켜놓고 클라와 테스트해본후 정식배포를 했었었다..

하지만 검색을 하다보니 스프링부트에서는 함께 받는 방법이있어서 정리해본다.

보통은 body값을 받을때는 `@RequestBody` 어노테이션을 통해 받아 java로 매핑시켜준다. 하지만 body값만 받을수 있고 MultipartFile을 받으면 매핑 에러가 발생을 했었다.

그래서 다른 방법으로 @RequestPart 라는 어노테이션을 이용해 MultipartFile과 body를 함께 받아줄수있었다.

```java
//Controller
@PostMapping()
public ResponseEntity<Void> registration(@RequestPart(value = "image", required=false) MultipartFile multipartFile,@RequestPart(value="registrationInfoDto")RegistrationInfoDto registrationInfoDto){
  userService.registration(multipartFile, registrationInfoDto);
  return ResponseEntity.ok().build():
}
```

```java
//Dto
public class RegistrationReqDto {

	@NotBlank(message = "이름은 필수값입니다.")
	private String name;
	@NotBlank(message = "이메일은 필수값입니다.")
	private String email;
	@NotBlank(message = "비밀번호는 필수값입니다.")
	private String password;
	@Enum(enumClass = Sex.class, ignoreCase = true, message = "유효하지 않은 sex 타입입니다.")
	private String sex;
	@NotBlank(message = "생년월일은 필수값입니다.")
	@Length(min = 8, max = 8)
	private String birthDate;
	@NotNull(message = "전화번호를 입력해주세요.")
	private String phoneNumber;
  
  private String imgUrl;
}
```

이렇게 준비하고 포스트맨으로 post를 해보면

![image](https://user-images.githubusercontent.com/57162257/136484063-79193900-088a-4de7-9388-71e9d2a25d65.png)

이런 식으로 form-data형식으로 body값으로 보내려는 데이터들을 json형식으로(content type은 application/json 필수!), multipartFile은 file형식으로 value에 이미지를 넣어주면

![image](https://user-images.githubusercontent.com/57162257/136484228-3f8c3446-7b73-4099-b204-026baabd5565.png)

성공.