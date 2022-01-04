# spring.main.allow-bean-definition-overriding=true



## 발단

회사의 배치서버에서 firebase notification에 관련해서 이슈를 잡기위해 writer부분을 수정하고 재배포를 하니 전에 발생하지않던 에러가 발생했다.

![image](https://user-images.githubusercontent.com/57162257/148013713-2d01eea1-dc87-423d-b3da-1fcd7918f65c.png)



## 이유

ReportNotificationJobConfiguration에서 JobParameter빈을 생성하는데 있어서 다른 Configuration에서 같은 JobParameter빈을 생성해서 중복되는 현상이였다.



## 해결

스프링부터 2.1부터는 overriding 옵션이 false로 되어있어 properties에서 true로 설정을 해주면 중복 빈을 overriding할수 있었다.

```
spring.main.allow-bean-definition-overriding:true
```



끝.



## 궁금점

근데 로컬 환경에서 돌렸을때는 빌드했을때 아무 문제없이 테스트가 되었어서 배포를 한거였는데.. ubuntu에서 빌드하려고 하니 오류가 발생해서 조금 당황스러웠다. 로컬환경에서도 똑같은 2.5.5버전이였는데 말이다..

하지만 이렇게 발생하는게 맞다고 생각하고, 빈 중복에있어서 오버라이딩 된다는 것도 알았다.



## 참고

https://n1tjrgns.tistory.com/172