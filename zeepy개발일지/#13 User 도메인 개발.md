# #13 User 도메인 개발

----------------------------

## Status 406, "Not Acceptable"

![image](https://user-images.githubusercontent.com/57162257/127344057-e9ae4424-807a-429c-b513-7fcbc0bcb50f.png)

![image](https://user-images.githubusercontent.com/57162257/127344315-0f27dbc6-d0b0-4b81-80af-b3f1a758344d.png)

사용자의 address를 뽑아오려고 하니까 406을 처음 받아봤다. 

검색결과 보통 jackson라이브러리가 추가되어있지 않아 발생한다고 한다. 하지만 jackson라이브러리는 이미 깔려있었고 확인해보니, 

반환 Dto인 `AddressResDto`에 `@Getter`가 없었다.

https://mommoo.tistory.com/83 의 말에 따르자면, **json을 java로, json을 java로 변환해주는 친구가 jackson이라는 친구인데, 이 jackson은 멤버변수의 유무로 json을 출력해주는 것이 아닌, 프로퍼티(클래스의 필드값)를 getter를 이용하여 가져온다고 한다.**

즉, <u>**반환해주는 dto에 @Getter가 선언이 되어있지 않았기 떄문에 오류가 발생했었다.**</u>

@Getter가 아닌 다른 방식으로 매핑하는 방법을 알고싶다면 위의 링크에서 공부해보자.

