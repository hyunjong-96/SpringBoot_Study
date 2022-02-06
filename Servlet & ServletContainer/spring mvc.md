# Spring MVC



## spring mvc란?

spring framework로 개발을 할때 기본적으로 mvc 패턴을 따른다.

MVC패턴이란, Model, View, Controller이 3가지로 나누어 역할을 분담하고 있는 디자인 패턴이다.

역할을 나누어서 처리하기 때문에 서로의 결합도가 낮아져 유지보수가 쉽고 좋은 코드가 된다.

#### 처리 흐름

1. 사용자의 요청을 Controller가 받는다.
2. Controller는 비즈니르 로직을 처리후 Model에 담는다.
3. Model에 저장된 결과를 View에서 시각적으로 처리하여 사용자에게 전달한다.



## spring mvc 구조

<img src="https://user-images.githubusercontent.com/57162257/152669414-6fc43c1f-1525-4fd5-aee5-e7839eae92f9.png" alt="image" style="zoom:50%;" />

1. 핸들러 조회
   - 핸들러 매핑을 통해 클라이언트의 요청 URL에 매핑된 핸들러(Controller)를 찾는다.
2. 핸들러 어댑터 조회
   - 핸들러를 처리할수 있는 핸들러 어댑터를 조회한다.
3. handle
   - 핸들러 어댑터를 통해 실제 핸들러를 호출해서 실행시킨다.
4. ModelAndView 반환
   - 핸들러가 반환하는 데이터를 ModelAndView로 변환해서 반환한다.
5. ViewResolver 호출
   - ViewResolver를 이용해 응답으로 사용할 View를 반환한다.
6. Render 호출
   - ViewResolver를 통해 찾은 View를 클라이언트에게 응답한다.



#### DispatcherServlet

DispatcherServlet은 spring mvc에서의 핵심이라고 할수 있다.

DispatcherServlet의 상속관계는 아래와 같다.

<img src="https://user-images.githubusercontent.com/57162257/152670876-926447c2-05d9-4de2-9506-8602e2e24166.png" alt="image" style="zoom:40%;" />

서블릿 컨테이너에 의해 서블릿이 호출되게 되면 FrameworkServlet의 service()메소드를 실행한다.(FrameworkServlet.service()메소드는 HttpServlet의 오버라이드)

FrameworkServlet.service()를 호출하게 되면 processRequest()메소드를 호출하고 doService()메소드를 호출하게 되는데, doService()메소드의 구현은 DispatcherServlet에서 구현하고 있다.

DispatcherServlet.doService()메소드를 호출하게 되면 Dispatcher.doDispatch()라는 메소드를 호출하게 되는데, 이 doDispatch()메소드에 spring mvc의 핵심 동작이 구현되어있다.

> DispatcherServlet.doDispatch()
>
> process the actual dispatching to the handler.
> The handler will be obtained by applying the servlet's HandlerMappings in order. The HandlerAdapter will be obtained by querying the servlet's installed HandlerAdapters to find the first that supports the handler class.
> All HTTP methods are handled by this method. It's up to HandlerAdapters or handlers themselves to decide which methods are acceptable.
>
> 핸들러에 대한 실제 발송을 처리합니다.
> 처리기는 서블릿의 처리기맵핑을 순서대로 적용하여 가져옵니다. HandlerAdapter는 서블릿의 설치된 HandlerAdapters를 쿼리하여 핸들러 클래스를 지원하는 첫 번째를 찾아 가져옵니다.
> 모든 HTTP 메서드는 이 메서드에 의해 처리됩니다. 어떤 메서드가 허용되는지는 핸들러어댑터 또는 핸들러 자체에 달려 있습니다.

doDispatch()메소드를 확인해보면,

- 핸들러 매핑
  <img src="https://user-images.githubusercontent.com/57162257/152671063-86a0a3f4-b42b-4fb4-b477-ff74373e40a7.png" alt="image" style="zoom:50%;" />

- 핸들러 어댑터 조회

  <img src = "https://user-images.githubusercontent.com/57162257/152671073-175b7000-3b25-470a-9c82-c7572bf2ed59.png" alt="image" style="zoom:70%;">

- 핸들러 어댑터 실행 -> 핸들러 어댑터를 통해 핸들러 실행 -> ModelAndView 반환

  <img src="https://user-images.githubusercontent.com/57162257/152671087-c6ab9071-7187-450f-8f0a-252d94085e69.png" alt="image" style="zoom:50%;" />

- ViewResolver를 통해 view 반환

  <img src="https://user-images.githubusercontent.com/57162257/152671155-9e3296e0-1bb5-4e1b-adb6-205b70533e31.png" style = "zoom:50%">

- View 랜터링

  <img src="https://user-images.githubusercontent.com/57162257/152671166-d9023b4f-5fdf-4bff-8704-79b942102104.png" style = "zoom:50%">

이처럼 위에 설명했던 spring mvc구조가 doDispatch()메소드에 모두 구현이 되어있는 것을 확인할수 있다.



## HandlerMapping / HandlerAdapter

Spring mvc에서는 어떻게 요청 URL에 맞는 핸들러(Controller)를 찾아가는지에 대한 동작을 알아보자

- Handler Mapping
  - 요청 URL에 해당하는 핸들러를 조회
- Handler Adapter
  - Handler Mapping에 의해 찾은 핸들러를 실제로 처리



### Handler Mapping / Handler Adapter 동작 이해

우선 Handler Mapping과 Handler Adapter의 우선순위를 알아보면

- Handler Mapping
  0. RequestMappingHandlerMapping	// @RequestMapping의 핸들러 매핑
  1. BeanNameUrlHandlerMapping 	// 스프링 빈(Bean)의 이름으로 핸들러를 찾음
- Handler Adapter
  0. RequestMappingHandlerAdapter	// @RequestMapping의 핸들러 어댑터
  1. HttpRequestHandlerAdapter	// HttpRequestHandler 처리	
  2. SimpleControllerHandlerAdapter	// Controller 인터페이스 처리



보통은 @RequestMapping어노테이션을 @Controller나 @RestController으로 빈 등록을 할때 사용한다

#### 동작 순서

1. 클라이언트의 요청이 들어온다.
2. 핸들러 매핑 찾기
   - HandlerMapping을 순회하며 요청 url에 맞는 핸들러 RequestMappingHandlerMapping 반환
3. 핸들러 반환
   - RequestMappingHandlerMapping이 스프링 빈 중 @RequestMapping 또는 @Controller가 클래스 레벨에 있는 것을 찾아 매핑 정보로 인식한다.
4. 핸들러 어댑터 찾기
   - Handler Adapter의 supports()를 순서대로 호출하며 처리할 수 있는 핸들러 어댑터를 찾는다.
   - RequestMappingHandlerAdapter 반환
5. 핸들러 어댑터 실행
   - RequestMappingHandlerAdapter로 요청 URL로 찾은 핸들러를 호출
   - ModelAndView 반환
6. view resolver
   - 논리 뷰 주소를 실제 물리 주소로 매핑
7. view
   - ModelAndView에 있는 데이터를 통해 view 랜더링



## 참조

https://velog.io/@ehdrms2034/%EC%8A%A4%ED%94%84%EB%A7%81-MVC-Spring-MVC-%EA%B5%AC%EC%A1%B0

https://velog.io/@neity16/6-%EC%8A%A4%ED%94%84%EB%A7%81-MVC-6-%EC%8A%A4%ED%94%84%EB%A7%81-MVC-%EA%B5%AC%EC%A1%B0-DispatcherServlet-HandlerMapping-HandlerAdapter-viewResolver

https://aridom.tistory.com/61