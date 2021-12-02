# FCM PushMessage

rest api로 어떤 요청이 들어왔을때 관련된 pushMessage를 보내는걸 구현해보려고한다.

트레이너가 요청을 보내면 회원에게 트레이너가 요청을 보냈다고 push알림이가고, 회원이 요청을 보내면 트레이너에게 회원이 요청을 보냈다고 push알림을 보내는 식이다.

푸시 메시지를 보낼때 firbase에서 비공개 키까지 받았다고 가정하고 정리하겠다.



## 1. 의존성 추가

```
dependencies {
	//firebase sdk
	implementation 'com.google.firebase:firebase-admin:8.1.0'
}
```



## 2. 비공개 키 저장 & 환경설정

![image](https://user-images.githubusercontent.com/57162257/144377149-e2425082-e4c0-43b4-9dcf-1d0f45f4444e.png)

Firebase에서 발급받은 비공개 키를 프로젝트에 저장한다.

![image-20211202163129720](/Users/flab1/Library/Application Support/typora-user-images/image-20211202163129720.png)

properties에 환경설정



## 3. Initialize

```java
@Component
public class FCMInitializer {

	private static final Logger logger = LoggerFactory.getLogger(FCMInitializer.class);
	@Value("${firebase-config-path}")
	private String FIREBASE_CONFIG_PATH;
	@Value("${firebase-create-scoped}")
	private String FIREBASE_CREATE_SCOPE;

	@PostConstruct
	public void initialize() {
		try {
      //1
			GoogleCredentials googleCredentials = GoogleCredentials.fromStream(
					new ClassPathResource(FIREBASE_CONFIG_PATH)
						.getInputStream())
				.createScoped(Collections.singletonList(FIREBASE_CREATE_SCOPE));

      //2
			FirebaseOptions options = FirebaseOptions.builder()
				.setCredentials(googleCredentials)
				.build();

			FirebaseApp.initializeApp(options);
			logger.info("Firebase application has been initialized");
		} catch (IOException e) {
			logger.error(e.getMessage());
		}
	}
}
```

1. `GoogleCredentials`
   - `GoogleCredentials`는 GoogleApi를 사용하기 위해서 oauth2를 이용해 인증한 대상을 나타내는 객체
   - `fromStream()`메소드를 통해 비밀키를 `inputStream()`을 통해 넣어주면 발급받을수 있다.
   - 이때 인증하는 서버에서 필요로 하는 권한을 지정해주어야 하는데, 반환된 GoogleCredentials 인스턴스의 createScoped()를 통해 설정 가능함 (https://developers.google.com/identity/protocols/oauth2/scopes#fcm)
2. `FirebaseOptions`
   - Firebase의 옵션을 등록하는 객체로써 인증받은 `GoogleCredentials`를 넣어주고 FirebaseApp의 `initializeApp()`을 통해 `FirebaseOption`을 등록해야만 FCM이 App Server에서 PushMessage를 보낼때 반응을 한다.



## 4. FCM 보내기

```java
@RequiredArgsConstructor
@Service
public class FCMService {

	private static final Logger logger = LoggerFactory.getLogger(FCMService.class);

	public void send(final NotificationRequest notificationRequest) throws InterruptedException, ExecutionException {
    //1
		Message message = Message.builder()
			.setToken(notificationRequest.getToken())
			.setNotification(
				Notification.builder()//2
					.setTitle(notificationRequest.getTitle())
					.setBody(notificationRequest.getBody())
					.build()
			)
			.build();

    //3
		String response = FirebaseMessaging.getInstance().sendAsync(message).get();
		logger.info("Send firebaseMessage : " + response);
	}
}
```

1. `Message`는 Firebase 클라우드 메시징 서비스에서 보낼 메시지 객체.
   ![image](https://user-images.githubusercontent.com/57162257/144382010-51281469-dc17-43a1-a772-a83e5ccd9004.png)

2. `Message`안에는 많은 필드값들이 있는데, 나는 title과 body만 필요해서 `token`과 `notification`을 사용했다.
   ![image](https://user-images.githubusercontent.com/57162257/144382634-0f8d6492-fffc-423d-9c82-b95d7dc4bd17.png)
   위는 Message안의 Notification인데 Message의 `setNotification()`에 builder를 통해 설정해준다.
3. Message Builder를 통해 만들어진 정상적인 Message를 보내면 `projects/finut-server/messages/0:1568180254610207%cc9b4facf9fd7ecd` 등과 같은 message의 값을 리턴받는다.



## 5. NotificationService

```java
@Getter
@NoArgsConstructor
public class ReservationScheduleNotificationRequest {

	private User sender;	//api 요청자
	private User receiver;	//수신자
	private Schedule reservationSchedule;	//api 요청자가 요청한 스케줄 정보

	public ReservationScheduleNotificationRequest(User sender, User receiver, Schedule reservationSchedule) {
		this.sender = sender;
		this.receiver = receiver;
		this.reservationSchedule = reservationSchedule;
	}
}
```



```java
@Getter
@NoArgsConstructor
public class NotificationRequest {

	private String title;
	private String token;
	private String body;

	@Builder
	public NotificationRequest(String title, String token, String body) {
		this.title = title;
		this.token = token;
		this.body = body;
	}
}
```





```java
@Transactional
@RequiredArgsConstructor
@Service
public class NotificationService {

	private final FCMService fcmService;
	private final NotificationUtil notificationUtil;

	public void sendReservationScheduleNotification(
		ReservationScheduleNotificationRequest reservationScheduleNotificationRequest) {
		User sender = reservationScheduleNotificationRequest.getSender();
		User receiver = reservationScheduleNotificationRequest.getReceiver();
		Schedule reservationSchedule = reservationScheduleNotificationRequest.getReservationSchedule();

    //1
		if (notificationUtil.isHaveFCMToken(reservationScheduleNotificationRequest.getReceiver())) {
      //2
			NotificationRequest createReservationScheduleNotification =
				notificationUtil.createReservationScheduleNotification(sender, receiver, reservationSchedule);

			this.sendNotification(createReservationScheduleNotification);
		}
	}
  
  //3
  private void sendNotification(NotificationRequest notificationRequest) {
		try {
			fcmService.send(notificationRequest);
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
	}
  
}
```

1. push알림을 받을 사용자가 fcm토큰을 가지고 있는지 확인, 소유하지 않고 있다면 별도로 push알림을 보내지 않는다.
2. `Sender, receiver, schedule`을 통해 `Message`에 선언해줄 `title, body, token`을 담은 `NotificationRequest`를 받아온다.
3. [4]에서 선언한 `FcmService`의 `send`에 `NotificationRequest`를 보내준다.



## 6. NotificationUtil

```java
@Component
public class NotificationUtil {

	public Boolean isHaveFCMToken(User receiver) {
		return receiver.getFcmToken() != null;
	}

	public NotificationRequest createReservationScheduleNotification(
		User sender, User receiver, Schedule reservationSchedule) {

		String token = ~;
		String body = ~;
		String title = ~;

		return NotificationRequest.builder()
			.title(title)
			.token(token)
			.body(body)
			.build();
	}
}
```

1. Sender, receiver, schedule을 받아서 적절하게 receiver에게 push알림을 보내준다.



![image](https://user-images.githubusercontent.com/57162257/144386510-828e49d0-efca-41e3-a135-0cb4d047c404.png)

잘 나온다.

해결.



## 참조

https://galid1.tistory.com/740

https://galid1.tistory.com/740

https://velog.io/@skygl/FCM-Spring-Boot%EB%A5%BC-%EC%82%AC%EC%9A%A9%ED%95%98%EC%97%AC-%EC%9B%B9-%ED%91%B8%EC%8B%9C-%EA%B8%B0%EB%8A%A5-%EA%B5%AC%ED%98%84%ED%95%98%EA%B8%B0

https://firebase.google.com/docs/reference/fcm/rest/v1/projects.messages#Notification