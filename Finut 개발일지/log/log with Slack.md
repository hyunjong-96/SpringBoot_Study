# log with Slack

실시간으로 서버의 상황을 봐야할때 내가 알고있는 nohup에 들어가서 계속 에러 로그를 확인할수는 없다.

그렇기 떄문에 중요한 로그인 error만 따로 실시간으로 볼수없을까하다가 Slack에서 로그를 볼수있는 것을 알고 따라해보기로했다.

## 1. Slack채널에 앱 설치하기

![image](https://user-images.githubusercontent.com/57162257/139818362-65c31bac-9e9d-4081-ab52-ce51d066340f.png)

- 로그를 받을 채널을 만든다

![image](https://user-images.githubusercontent.com/57162257/139818680-081034fc-7a2f-4a35-8b61-1db0d0713616.png)

- 채널에 `incoming WebHooks` 을 다운받는다.

![image](https://user-images.githubusercontent.com/57162257/139818927-d2b8765e-1769-450e-9698-6590159431a6.png)

- 밑으로 내려오다보면 웹후크 URL이 있는데 이걸 복사해놓는다.



## 2. logback-spring-xml

### gradle

```
implementation 'com.github.maricn:logback-slack-appender:1.4.0'
```

### logback-spring.xml

```xml
<?xml version="1.0" encoding="UTF-8" ?>
<configuration>
  <springPropery name = "ERROR_SLACK_WEBHOOK_URI" source="loggin.slack.error.webhook-uri"/>
  <springPropery name = "logPath" source="log.path"/>
  
  <appender name="ERROR_SLACK" class="com.github.maricn.logback.SlackAppender">
        <webhookUri>${ERROR_SLACK_WEBHOOK_URI}</webhookUri>
        <layout class="ch.qos.logback.classic.PatternLayout">
            <pattern>[%d{yyyy-MM-dd HH:mm:ss}:%-3relative][%thread] %-5level %logger{36} - %msg%n</pattern>
        </layout>
        <username>[user]</username>
        <colorCoding>true</colorCoding>
    </appender>
  
  <appender name="ASYNC_ERROR_SLACK" class="ch.qos.logback.classic.AsyncAppender">
        <appender-ref ref="ERROR_SLACK"/>
        <filter class="ch.qos.logback.classic.filter.ThresholdFilter">
            <level>ERROR</level>
        </filter>
    </appender>
  
  <root level="INFO">
        <appender-ref ref="ASYNC_ERROR_SLACK"/>
    </root>
  
</configuration>
```

이게.. 뭐였더라.. 하면 log정리한걸 다시보자



## 결과

![image](https://user-images.githubusercontent.com/57162257/139820698-5b0235c9-ebaf-46e4-bc6b-32e8649c0be2.png)

성공