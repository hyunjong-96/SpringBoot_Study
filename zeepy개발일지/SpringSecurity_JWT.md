# 기본적인 Security 동작 플로우

![image](https://user-images.githubusercontent.com/57162257/122236600-9d874a80-cef9-11eb-856b-706406e869b5.png)

1. 사용자가 로그인 정보와 함께 인증 요청(Http Request)
2. `AuthenticationFilter`에서 (1)의 요청을 가로채고 가로챈 정보를 통해 `UsernamePasswordAuthentication Token`으로 **인증 객체**(Authentication)를 생성
3. `AuthenticationFilter`에서 인증 객체를 `AuthenticationManager`에게 보낸다
   - `AuthenticationManager`에게 보내지지만 실제로는 `AuthenticationManager`의 구현체인 `ProviderManager`에게 보내져 `ProviderManager`가 인증 객체를 인증한다.
4. `ProviderManager`에서는 직접 인증을 해주는게 아니다. 다양한 인증을 해줄 멤버변수인 Provider들이 있는데, 그 중에 하나인 `AuthenticationProvider`객체가 인증 객체를 받아서 인증을한다.
5. `AuthenticationProvider`는 실제 데이터베이스를 통해 사용자 정보를 가져와 주는 `UserDetailService`에 사용자 정보를 넘겨받는다.
6. 넘겨 받은 사용자 정보를 통해 DB에서 찾은 사용자 정보인 `UserDetails`객체를 만든다.(저는 **UserDetails를 도메인영 객체에 상속**받아서 구현했습니다.)
7. `AuthenticationProvider`에게 `UserDetails`를 넘겨주고 사용자 정보를 비교한다.
8. 인증이 완료되면 인증이 된 사용자 정보를 인증 객체에 담아서 인증 객체를 반환한다.
9. `ProviderManager`에서 인증이 완료됬다고 알려주면 `AuthenticationManager`가 `isAuthneticated`값을 true로 변경해주면서 인증된 인증 객체를 반환해준다.
10. 인증객체를 `SecurityContext`에 저장한다.
    - SecurityContextHolder는 세션 영역에 있는 SecurityContext에 Authentication객체를 저장하는데, 세션영역에 Authentication객체를 저장한다는 것은 스프링 시큐리티가 세션-쿠키 기반의 인증 방식을 사용한다는 것을 의미한다(저는 JWT를 사용할꺼기 떄문에 저장하지 않았습니다.)

