# Calendar



이번에 구현해볼 기능은 특정 날짜를 받으면 그 날짜의 월요일부터 일요일까지를 반환하는 것이다.

참고로 한 주의 시작 요일을 월요일로 두었다.

-----------------

```java
public LocalDate getDayOfWeeksMon(LocalDate localDate) {
		Date monDate = setMonday(localDate);	//1
		return dateToLocalDate(monDate);
	}

	public LocalDate getDayOfWeeksSun(LocalDate localDate) {
		Date sunDate = setSunDay(localDate);	//1
		return dateToLocalDate(sunDate);
	}
```

각각은 특정 날짜의 월요일과 일요일을 구하는 메소드이다.

1. `localDate`값을 받으면 월요일을 `Date`타입으로 반환해주는 메소드.

---------------

```java
public Date setMonday(LocalDate localDate) {
		Calendar calendar = setCalendarByLocalDate(localDate);	//1
		calendar = validSunDay(calendar);	//2
		calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);	//3
		return calendar.getTime();
	}

	public Date setSunDay(LocalDate localDate) {
		Calendar calendar = setCalendarByLocalDate(localDate);	//1
		calendar = validSunDay(calendar);	//2
		calendar.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);	//3
		calendar.add(Calendar.DATE, 7);	//4
		return calendar.getTime();
	}
```

1. ```java
   private Calendar setCalendarByLocalDate(LocalDate localDate) {
      Calendar calendar = Calendar.getInstance();
   
      calendar.set(localDate.getYear(), localDate.getMonthValue() - 1, localDate.getDayOfMonth(), 0, 0, 0);
      return calendar;
   }
   ```

   매개변수로 받은 localDate를 Calendar클래스로 변경시켜주는 메소드.
   `calendar.set()` 을 통해 변경시켜준다.
   참고로 localDate의 월은 1월이 1, 2월이 2..이렇게 표현한다. 하지만 Calendar의 월은 1월이 0, 2월이 1이기때문에 `localDate.getMonthValue()`에서 -1을 해줘서 세팅을 해준다.

   

2. ```java
   public Calendar validSunDay(Calendar calendar) {
      if (calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY)
         calendar.add(Calendar.DATE, -6);
      return calendar;
   }
   ```

   애를 가장 많이 먹었던 부분이였는데 생각해보면 너무 간단했다.
   Calendar에서는 한 주의 첫번째 요일이 일요일이다. 하지만 내가 원하는건 첫번쨰 요일이 월요일이 나오게 해야하는데, 자꾸 날짜가 이상하게 나와서 디버깅하다가 이 사실을 알게되었다..
   그래서 **localDate를 Calendar로 변경시켜줄때 만약 요청한 날짜가 일요일이라면 Calendar에서는 내가 받고자하는 주의 다음주를 가리키게 된다**. 그렇기 떄문에 Calendar가 일요일을 가리키고 있다면 그 전 주에 해당해야하므로 일을 `-6`해서 주를 그 전 주로 이동시켜주는것이다.

   예를 들어

   (내가 원하는 주는 파란색, 캘린더가 가리키는 주는 빨간색)
   ![image](https://user-images.githubusercontent.com/57162257/139403831-6042eed4-123b-4d52-8d3b-2f2d0f1a5d3a.png)

    10월 24일 5번째 주를 캘린더가 가리키고 있다면 나는 4번째 주가 필요한 것이다. 그렇기에 캘린더를 그 전 주인 4번째 주로 바꿔주기 위해 임의로 10월 18일로 캘린더를 가리키게 하는 것이다.

   ![image](https://user-images.githubusercontent.com/57162257/139403032-15674dc7-ff27-46d1-aa28-2fbbc137720c.png)

   

3. 2에서 받은 캘린더는 localDate가 보내준 주차를 보고있다. 일요일이였다면 그 주의 월요일을 보고있을것이고 그 외에는 localDate의 요일을 보고 있을것이다.
   그런다음 `calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)`로 월요일을, 		`calendar.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY)`로 일요일로 변경시켜준다.

   예를 들어 2번 메소드를 통해 10월 18일을 가리키고 있는 Calendar를 받아왔다면 해당 주의 월요일로 바꿔주면 10월 18일, 일요일로 바꿔주면 10월 17일이 된다.

   ![image](https://user-images.githubusercontent.com/57162257/139403311-c34dd753-f965-4a88-8719-01e566917cb2.png)

   

4. 말했다 싶이 Calendar에서 해당 주의 일요일은 첫번째 요일이다. 그러므로 3을 통해서 일요일로 옮겼지만 이것은 내가 찾고자하는 그 전 주의 일요일일뿐, 그러니까 여기서 +7을 해주면 Calendar에서는 그 다음 주, 나에게는 이번 주가 되는것이다
   예를 들어 3번 메소드를 통해 일요일이 10월 17일로 되어있기때문에 내가 얻고자하는 날은 10월 24일 일요일이기때문에 한 주를 이동시켜주면 되는것이다.
   ![image](https://user-images.githubusercontent.com/57162257/139403587-d4c3d39e-c6e1-4768-9cf2-07e23d09cc73.png)

---------------



```java
public LocalDate dateToLocalDate(Date date) {
		return date.toInstant()
			.atZone(ZoneId.systemDefault())
			.toLocalDate();
	}
```

Calendar를 LocalDate로 다시 사용하기 위해 위 메소드를 통해 변경시켜준다.