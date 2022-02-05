# Servlet 3.0 / 3.1

이전에 spring boot의 다중 사용자 처리를 통해 NIO Connector를 공부했었었다.

NIO Connector에서는 클라이언트와 서블릿컨테이너간의 커뮤니케이션을 non-blocking으로 처리함으로써 클라이언트의 요청마다 스레드가 할당되는 것이 아닌 nio channel에 등록되어 Selector를 통해 소켓 커넥션이 관리되었기 때문에 톰캣의 작업큐의 크기보다 많은 요청이 들어와도 요청 거절이 반환되지 않았던 것이다.

하지만 non-blockingd은 클라이언트와 서블릿 컨테이너만에서 해당이고 서블릿 컨테이너와 서블릿 간의 커뮤니케이션은 여전히 blocking이다.

<img src="https://user-images.githubusercontent.com/57162257/152633354-87891df1-68f4-4084-adea-b69f925266c2.png" alt="image" style="zoom:40%;" />

서블릿 컨테이너와 서블릿간의 blocking은 서블릿 3.0이전에서 나타나던 문제였고 이러한 문제를 해결하기 위해 서블릿 3.0과 3.1 버전에서 Asnyc Servlet과 Non-Blocking I/O를 지원하여 높은 동시성을 제공해준다.



## 서블릿 3.0

서블릿 3.0에서는 Async Servlet이 추가 되었다. 비동기 서블릿을 통해 서블릿 컨테이너에서 요청 데이터를 받은 서블릿은 서블릿안에서 request processing을 수행할 새로운 스레드를 만들고 해당 스레드는 스레드 풀에 반환 한다.

이때 request processing 수행을 위해 생성된 스레드는 AsyncConext 객체의 startAsync()메서드를 통해 비동기 처리를 할수 있게 된다.

### 비동기 서블릿 흐름

1. request processing을 수행할 스레드(T1)가 서블릿의 doGet()메서드를 실행.
2. T1은  비동기 처리를 위한 AsyncConext객체를 생성.
3. request processing을 대신 수행할 스레드(T2)생성.
4. T2는 request processing을 수행.
5. doGet()메서드는 종료되고 T1은 Thread Pool에 반환.
6. T2의 작업이 완료되고 이전에 생성했던 AsnycConext의 객체를 통해 클라이언트에게 응답.
7. T2는 AsnycConext의 complete()메서드를 통해 연결 종료 후 Thread Pool에 반환.

하지만 비동기 서블릿은 요청의 처리만 수행할뿐 서블릿 컨테이너와 서블릿간의 커뮤니케이션(read, write)에서의 I/O는 여전히 blocking이다.

서블릿 컨테이너와 서블릿간의 I/O에서 다수의 스레드가 block이 된다면 스레드 기아가 발생할수 밖에 없다.

<img src="https://user-images.githubusercontent.com/57162257/152635760-ea20d5b8-80b8-4ef5-9d07-a6b2731fc481.png" alt="image" style="zoom:40%;" />



## 서블릿 3.1

서블릿 3.1에서는 non-blocking I/O를 제공함으로써 서블릿컨테이너와 서블릿간의 I/O간에 무거운 데이터로 인해 block되는 문제를 해결해준다.

만약 서버에서 무거운 데이터를 클라이언트에게 반환해야할때, 비동기 서블릿 방식을 사용하게 되면 ServletOutStream의 응답 데이터가 느리면 서블릿 컨테이너의 스레드는 기다려야하고 반대로 서블릿 컨테이너의 요청 데이터가 느리면 서블릿 스레드에서 기다려야 하기때문에 block이 발생한다.

하지만 ServletInputStream과 ServletOutStream에 등록되어있는 ReadListener와 WriteListener인터페이스를 통해 block을 해결할수 있다.

예를 들어 ServletOutStream.isReady() 메소드를 통해 컨테이너가 데이터를 쓸 수 있을 때(nio channel buffer에 write할수 있을때) 이 메소드(isReady())를 처음 호출하고 만약 false를 반환했을때 데이터를 작성할수 있을때 WriteListener의 onWritePossible()메소드를 호출할것을 예약하고 다른 스레드에 의해 onWritePossible()메소드를 호출한다. 이렇게 되면 서블릿 컨테이너와 서블릿간의 I/O의 속도가 느려도 서블릿 각각의 스레드는 I/O를 위해 대기하지않고 ReadListener의 onDataAvaliable()과 WriteListener의 onWritePossible()을 통해 데이터 사용이 가능할때 다른 스레드에 의해 I/O가 발생하므로 서로간의 block이 생기지 않게되는 것이다. 

<img src="https://user-images.githubusercontent.com/57162257/152635717-1ed763ce-1e61-4c1c-af7a-d2ff202519be.png" alt="image" style="zoom:40%;" />

> ReadListener
>
> Invoked when data is available to read. The container will invoke this method the first time for a request as soon as there is data to read. Subsequent invocations will only occur if a call to ServletInputStream.isReady() has returned false and data has subsequently become available to read.
>
> 데이터를 읽을 수 있을 때 호출됩니다. 컨테이너는 읽을 데이터가 있는 즉시 요청에 대해 이 메서드를 처음으로 호출합니다. 이후 호출은 ServletInputStream.isReady()에 대한 호출이 false로 반환되고 이후에 데이터를 읽을 수 있게 된 경우에만 발생합니다.

> WriteListener
>
> Invoked when it it possible to write data without blocking. The container will invoke this method the first time for a request as soon as data can be written. Subsequent invocations will only occur if a call to ServletOutputStream.isReady() has returned false and it has since become possible to write data.
>
> 차단하지 않고 데이터를 쓸 수 있을 때 실행됩니다. 컨테이너는 데이터를 쓸 수 있는 즉시 요청에 대해 이 메서드를 처음으로 호출합니다. 이후 호출은 ServletOutputStream.isReady()에 대한 호출이 false로 반환되어 데이터 쓰기가 가능한 경우에만 발생합니다.

## 참고

https://hadev.tistory.com/m/29

https://javacan.tistory.com/entry/Servlet-3-Async

https://pythonq.com/so/java/240938