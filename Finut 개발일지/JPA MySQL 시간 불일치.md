# JPA와 MySQL 저장 시간 불일치

서비스 개발을 하면서 전혀 생각도 못하고 있었던 이슈이다.

로컬에서 서버를 실행시키면 당연히 로컬 환경의 시간은 당연히 KST였겠다. 그러니 로컬에서 시간을 구해서 저장하면 그 시간 그대로 저장이 된다.

하지만 인스턴스에서 시간을 저장하면 내가 설정한 시간의 -9:00 시간으로 저장이 되는 거였다.. 사실 현재 개발 서비스에서 시간이 제일 중요한 거였는데, 로컬에서만 테스트하고 제대로 확인을 안했었다..



## 이유

일단 9시간이라는 시간이 차이나는것은 UTC와 KST의 시간차이다. UTC는 그리니치 평균시를 기반으로 하는 표준 시간이고, KST는 한국 표준 시간이다. KST는 UTC보다 9시간이 느리다.

여기서 감이 왔다. 혹시나 해서 ubuntu서버의 시간을 확인해보니 UTC였다.

**즉, java에서 시간을 보낼때 UTC를 기준으로 JPA에 시간을 저장하지만 MySQL에서는 KST이기 때문에 MySQL은 쿼리로 들어온 UTC시간을 타임존(KST)에 맞는 시간대로 바꿔주게 된다.** 결국 UTC시간이 들어오면 그 시간에서 9시간을 뺀다.

예를 들어 23:00(UTC)를 타임존이 KST인 MySQL에 저장을 하게된다면 23시를 KST에 맞게 -9시간을 해주게 되어 14:00시로 변경해서 저장을 하게 되는것이다.



## 해결

간단하게 ubuntu의 시간을 KST로 변경해주면 된다.

**timedatectl**명령어를 사용해주었는데, 

```bash
$ timedatectl list-timezones
```

를 이용해서 사용가능한 timezone을 확인한다. 그중 Asiz/Seoul 타임존을 사용할것다.

```bash
$ sudo timedatectl set-timezone Asia/Seoul
```

명령어를 통해 시간을 변경해줄수 있고

```bash
$ date
```

명령어를 통해 시간을 확인할수 있다.



## 추가

참고로 나는 spring에서 jdbc datasource url에 servertimezone을 이미 설정을 해주었다.

```
spring.datasource.url=jdbc:mysql:[rds주소]?serverTimezone=Asia/Seoul&characterEncoding=UTF-8
```

properties에 serverTimezone=Asia/Seoul로 설정하는 방법이있다.



다른 방법으로는 MySQL서버의 타임존을 직접 변경하는방법도 있다.

난 RDS를 사용했기 때문에 직접 타임존을 수정하는 접근권한이 없어서 막하기 떄문에 RDS에서 직접 바꿔줘야한다.

https://ndb796.tistory.com/263

서버에서 설정을 해주거나 db에서 설정해주거나 둘중 하나만 하면 된다고 한다.

그 외의 방법은 https://yjh5369.tistory.com/entry/Spring-Boot%EC%97%90%EC%84%9C-MySQL-JDBC-Timezone-%EC%84%A4%EC%A0%95 여기를 참고하자.



해결



## 참고

https://blog.buffashe.com/2020/02/changing-ubuntu-timezone/

https://m.blog.naver.com/writer0713/221636678923

https://velog.io/@taelee/mysql%EC%97%90%EC%84%9C-9%EC%8B%9C%EA%B0%84-%EC%B0%A8%EC%9D%B4%EB%82%A0%EB%95%8CGCP

https://yjh5369.tistory.com/entry/Spring-Boot%EC%97%90%EC%84%9C-MySQL-JDBC-Timezone-%EC%84%A4%EC%A0%95