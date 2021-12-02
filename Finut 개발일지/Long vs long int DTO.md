# Long vs long in DTO

dto를 사용하면서 문뜩 구린 느낌이났다. 나는 처음에 spring boot를 배우고 dto라는 개념을 배우면서부터 Long과 같은 클래스 타입으로 키값을 선언해주었었다. 그렇다 무지성이였다. 근데 데이터 형식은 long으로 선언해주어도 무리없이 사용은 되었다. 하지만 전에 작곰이가 왜 클래스로 dto에 선언을 했는지 물어봤었는데 문자열 타입은 string이 안되서 클래스로 했다고 얘기했었다. 아마 지금 생각난 long과 Long때문에 물어봤던 질문이였었던거 같은데,, 이제서야 궁금증이 생겼다.

## 

## 자료형

기초부터 다시 돌아와서 데이터 타입에는 크게 두가지 타입이 있다.

기본형(Primitive Type)과 참조형(Reference Type)으로 나눌수 있다.

- **`기본형(Primitive Type)`**
  - 논리형(boolean), 문자형(char), 정수형(byte, short, int, long), 실수형(float, double)
  - 계산을 위한 실제 값들을 나타낸다.
  - **메모리영역의 스택영역에 실제 값들이 저장**
- **`참조형(Reference Type)`**
  - Class, interface
  - 객체의 주소를 저장한다.
  - Java.lang.Object를 상속받을 경우 참조형이 된다.
  - **실제 인스턴스는 힙 영역에 생성되어있고, 그 영역의 주소를 스택영역에서 저장하고 있다**



## Wrapper class

기본 타입의 데이터를 객체로 표현하기 위해서 사용하는 클래스들을 `래퍼 클래스(wrapper class)`라고 한다.

기본 타입 값은 외부에서 변경할수 없다. 왜냐하면 래퍼 클래스는 final로 선언된 클래스이기 떄문이다.

만약 값을 변경하고 싶다면 새로운 포장 객체를 만들어야한다.

![image](https://user-images.githubusercontent.com/57162257/144371367-4ffc90fd-7c30-40ca-b126-99c98f70c00b.png)

Long 클래스의 내부인데, public final class로 선언되어있는것을 확인할수있다.



### Wrapper class의 구조도

![image](https://user-images.githubusercontent.com/57162257/144371537-6ad8fc6f-ea1b-43c6-9a86-13e329d7fa2b.png)



### 기본형과 래퍼클래스의 값 비교

- **기본형**
  - 기본형과 기본형을 비교할때는 `==`
  - 기본형과 래퍼클래스를 비교할때는 `==`, `equals()`
- 래퍼클래스
  - 래퍼클래스와 래퍼클래스를 비교할때는 `equals()`



자료형과 래퍼클래스에 대해서 되새김질을 했고, 이제 DTO에서는 기본타입을 사용해야할지, 클래스를 사용해야할지를 알아보자.

결과적으로는 래퍼클래스를 사용하는걸 선언한다고한다.

Hibernate JPA 공식문서 링크 : (https://docs.jboss.org/hibernate/orm/5.3/userguide/html_single/Hibernate_User_Guide.html#entity-pojo-identifier)

그 이유는 기본형인 long을 사용하면 기본값이 0이기 떄문에 실제 값이 0인건지 없는건지 구분하기가 어렵다고한다.

하지만 **래퍼 클래스를 사용한다면 null을 사용할수 있기때문에, 실제 값 자체가 없다는 것을 표현할수 있기때문에 래퍼클래스를 사용하는것은 권장한다고한다.**



해결.



# 참고

https://akdl911215.tistory.com/299

https://catsbi.oopy.io/6541026f-1e19-4117-8fef-aea145e4fc1b

https://coding-factory.tistory.com/547
