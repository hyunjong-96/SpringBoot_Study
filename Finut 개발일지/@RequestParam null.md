# RequestParam null

작업을 하면서 문득 @RequestParam으로 받았을때 어떤경우 null값을 받고 처리할수 있을까? 라는 궁금증이 생겨서 테스트를 해봤다.

![image](https://user-images.githubusercontent.com/57162257/139386204-ec38eaf9-dd93-442c-b837-328580f6fb7f.png)

요청하는 값은 `isFavorite`, `exerciseParts`, `exerciseName` 을 받고 테스트해보는 것은 `string`타입의 exerciseName과 `enum`타입의 exerciseParts이다.

![image](https://user-images.githubusercontent.com/57162257/139386863-bf98896b-cb96-465c-b8bf-26ef7e79f9e6.png)

테스트 로그는 위와 같고, 케이스별로 한번 실행시켜보았다.



1. exerciseName : 빈값, exerciseParts : 포함x
   ![image](https://user-images.githubusercontent.com/57162257/139387166-f5b99eac-7cba-4ea4-93ce-b166eadfa80b.png)
   - key값에 아예 아무값도 넣지 않는다면 null값이 나온다.
   - key값에 포함은 시키지만 빈값을 넣으면 ""으로 길이가 0인 문자열이 보내짐.
2. exerciseName : 한칸 띄기, exerciseParts : 빈값
   ![image-20211029153749454](/Users/flab1/Library/Application Support/typora-user-images/image-20211029153749454.png)
   - enum값을 key값에 포함은 시키지만 빈값을 넣으면 null값으로 보내짐(exception 발생할줄 알았는데 null이 반한되서 좀 놀람)
   - 물론 enum에 등록되어지지 않는 값을 보낸다면 jackson에서 매핑 exception을 발생시킨다.
   - 띄어쓰기하면 역시 길이가 늘어난다.



enum타입의 값에 빈값을 보내면 null을 보내주길래 그걸로 분기처리를 하면되니까 controller에서 exerciseParts를 `required = false`로 할필요가 없어진줄 알았는데, `require = true`로 변경해봤더니 바로 exception이 발생했다. `false`로 해야 `null`로 받을수 있는것이었다.(아쉽)



## 정리

@RequestParam값에 포함을 시키지않고 요청하면 null

포함은 시키되 빈값으로 요청하면 `string`타입은 length = 0, `enum`타입은 null