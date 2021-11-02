# Logger



## 1. Logback

- **log**

  - 로그란 애플리케이션에서 일어나는 일들을 기록으로 남겨서, 실시간 혹은 과거에 일어났던 사건을 역으로 트래킹 할 수 있는 자료.

- **Logback**

  - log4j, log4j2를 잇는 자바로깅 프레임워크
  - spring-boot-starter-web패키지 안에 Logback이 포함되어있음(Spring boot의 기본 로깅 모듈)
  - logback을 사용하는 이유는 다른 프레임워크에 비해 향상된 필터링 정책, 기능, 로그 레벨 변경 등에 대해 서버를 재시작할 필요 없이 자동 리로딩을 지원하기 때문이다.

- **Slf4j**

  - Simple Loggin Facade For java의 약자

  - 다양한 Loggin Framework에 대한 인터페이스

  - @Slf4j라는 어노테이션을 통해 log객체를 만들어 사용할수있음.

  - LogginFrameWork(구현체)가 변경되더라도 Slf4j덕분에 구현체에 종속되지않고 사용 가능

  - @Slf4j를 사용하면 springboot의 기본 로깅 모듈인 Logback을 사용하게됨.

    ```java
    @Slf4j
    @RequestMapping("/api/log")
    @RestController
    public class LogController{
      @GetMapping()
      public void logTest(){
    		log.error("에러");
        log.warn("위험");
        log.info("인포");
      }
    }
    ```

- **level**
  로그에는 총 5개의 레벨이 존재한다. 심각도 수준은 **Error > Warn > Info > Debug > Trace**
  - Error
    - 예상치 못해 심각한 문제가 발생하는 경우, 즉시 조취를 취해야 할 수준이다.
  - Warn
    - 유효성 확인, 예상 가능한 문제로 인한 예외 처리, 당장의 서비스 운영에는 문제가 없지만 주의해야 할 부분이다.
  - Info
    - 운영에 참고할만한 사향, 중요한 비즈니스 프로세스가 완료됨.
  - Debug
    - 개발 단계에서 사용되면, SQL로깅을 할 수 있다.
  - Trace
    - 모든 레벨에 대한 로깅이 추적되므로 개발 단계에서 사용한다.



## 2. logback-spring.xml

logback-spring.xml의 설정을 통해 콘솔창에 나오는 로그의 패턴을 커스텀하거나 로그파일생성, 슬랙등과 같은 다른 프로그램에 정보를 넘겨줄수 있다.

```properties
log.path = "./logs/info"
```



```xml
<configuration>
  
  <property name="LOGS_PATH" value="./logs"/> <!-- 6 -->
  <springProperty name="infoFilePath" value="log.path"/> <!-- 7 -->
  
	<appender name="STDOUT" class="ch.qos.logback.core.ConsoleAppender"> <!-- 1 -->
        <layout class="ch.qos.logback.classic.PatternLayout"> <!-- 2 -->
            <Pattern>[%d{yyyy-MM-dd HH:mm:ss}:%-3relative][%thread] %-5level %logger{36} - %msg%n</Pattern> <!-- 3 -->
        </layout>
  </appender>
  
  <root level="info">	<!-- 4 -->
    <appender-ref ref="STDOUT"/> <!-- 5 -->
  </root>
</configuration>
```

logback-spring.xml에서는 appendar와 logger로 크게 두개로 구분된다. apeender는 log의 형태를 설정, logger는 설정한 appender에 설정한 appender를 참조하여 package와 level을 설정.

1. log의 형태를 설정하는 **appender**

   - ch.qos.logback.core.ConsoleAppender : 콘솔에 로그를 찍음
   - ch.qos.logback.core.FileAppender : 파일에 로그를 찍음
   - ch.qos.logback.core.rolling.RollingFileAppender : 여러개의 파일을 순회하면서 로그를 찍음
   - ch.qos.logback.classic.net.SMTPAppender : 로그를 메일에 찍어 보냄
   - ch.qos.logback.classic.db.DBAppender : 데이터베이스에 로그를 찍음

2. log에 출력될 log의 패턴을 설정하는 **layout**

3. **pattern**

   - ```xml
     [%d{yyyy-MM-dd HH:mm:ss}:%-3relative][%thread] %-5level %logger{36} - %msg%n
     [2021-11-02 14:33:22:1520148][http-nio-8080-exec-1] INFO  c.s.s.controller.LogController - 로그 발생
     ```

     

   - %d

     - 로그 기록시간
     - ex) 2021-11-02 14:33:22:1520148

   - %thread

     - 현재 Thread이름
     - ex) http-nio-8080-exec-1

   - %-5level

     - 로그 레벨(5글자)
     - ex) INFO

   - %logger{36}

     - 축약된 logger name, 최대 36자리

     - ex) c.s.s.controller.LogController

     - logger name은 

       ```java
       private final Logger logger = LoggerFactory.getLogger(this.getClass().getSimpleName());
       ```

       를 log를 띄울 계층에 적용하면 해당 클래스 이름으로 나오게된다.

     - ```java
       import org.slf4j.Logger;
       import org.slf4j.LoggerFactory;
       import org.springframework.stereotype.Service;
       
       @Service
       public class LogService {
       
           private final Logger logger = LoggerFactory.getLogger(this.getClass().getSimpleName());
       
           public void log() {
               logger.info("Info");
               logger.warn("Warn");
               logger.error("Error");
           }
       }
       ```

       ```json
       [2021-08-07 18:19:09:17317][http-nio-8080-exec-1] INFO  LogService - Info
       [2021-08-07 18:19:09:17317][http-nio-8080-exec-1] WARN  LogService - Warn
       [2021-08-07 18:19:09:17317][http-nio-8080-exec-1] ERROR LogService - Error
       ```

       이를 통해 .xml 에서 Logger를 이용해 클래스마다 다르게 log level을 설정해서 로그를 뽑아올수 있다.

   - %msg

     - 로그메시지
     - ex) 로그 발생

   - %n

     - 줄바꿈

4. 최상단 디렉터리에 대한 설정을 하는 **root**

   - logger와 같음
   - 최상단 디렉터리의 로그 레벨을 설정할수 있다

5. root에서 사용할 로그를 선언해줄수있다.

6. xml에서 사용할 변수선언 **property**

7. 설정파일(.yml, .properties)에서 가져와 사용할 변수 선언 **springProperty**

```xml
<appender name="INFO_LOG" class="ch.qos.logback.core.rolling.RollingFileAppender">	<!-- 1 -->
    <file>${infoFilePath}/application.log</file>	<!-- 2 -->
    <filter class="ch.qos.logback.classic.filter.LevelFilter">	<!-- 3 -->
        <level>info</level>
        <onMatch>ACCEPT</onMatch>
        <onMismatch>DENY</onMismatch>
    </filter>
    <encoder>	<!-- 4 -->
        <pattern>%d{yyyyMMdd HH:mm:ss.SSS} [%thread] %-5level [%logger{0}:%line] - %msg %n</pattern>
    </encoder>
    <rollingPolicy class="ch.qos.logback.core.rolling.TimeBasedRollingPolicy">	<!-- 5 -->
        <fileNamePattern>${infoFilePath}/application.log.%d{yyyy-MM-dd}.gz</fileNamePattern>
        <maxHistory>30</maxHistory>
        <totalSizeCap>5GB</totalSizeCap>
    </rollingPolicy>
</appender>
```

1. 여러개의 파일을 순회하면서 로그를 찍는 appender클래스인 RollingFileAppender로 **로그파일을 생성**할때 사용하는 클래스이다.

2. 기록할 파일명과 경로를 설정하는 **file**

3. 로그파일을 생성할때 로그레벨에 따라 이벤트 생성,무시 여부를 할수 있는 **filter**

   - LevelFilter
     - **level**로 설정된 로그 레벨의 로그만 로그 이벤트가 활성화된다.
   - ThresholdFilter
     - **level**로 설정된 로그 레벨의 이상 로그만 로그 이벤트가 활성화된다.

4. layout과 유사한 기능을하는 **encoder**

   - encoder가 시간과 내용을 자유롭게 제어할수 있지만, layout은 이벤트 등의 제어에 한계가 있다.

5. 로그파일 생성 필드 **rollingPolicy**

   - 날짜가 변경되었을때 파일을 따로 저장하도록 설정.
   - 확장자를 gz로 설정하면 압축 파일로 저장.

   - ch.qos.logback.core.rolling.TimeBasedRollingPolicy
     - 일자별 적용
   - ch.qos.logback.core.rolling.SizeAndTimeBasedFNATP 
     - 일자별 + 크기별 적용



## 결과

![image](https://user-images.githubusercontent.com/57162257/139794148-c35f362b-d5a4-4c33-8963-4fd1ab1ddf6e.png)

![image](https://user-images.githubusercontent.com/57162257/139794217-89b754e7-28dd-4402-a7a0-fb7248b5f531.png)



## 참고

logger filter 

- http://logback.qos.ch/manual/filters.html

Logger 적용 xml 

- https://frozenpond.tistory.com/86?category=1175501
- https://wbluke.tistory.com/51
- https://tecoble.techcourse.co.kr/post/2021-08-07-logback-tutorial/
- https://goddaehee.tistory.com/206
- https://dejavuhyo.github.io/posts/spring-boot-logback/

