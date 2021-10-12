# #3 Spring Batch의 메타 테이블

![image](https://user-images.githubusercontent.com/57162257/136761698-0b2584d2-dd16-4a2e-8767-b4cd25606cbe.png)



## 3-1. BATCH_JOB_INSTANCE

![image](https://user-images.githubusercontent.com/57162257/136895377-6d0525cf-676f-404a-8175-51883eff3822.png)

- JOB_NAME
  - 실행시킨 Batch Job의 이름

BATCH_JOB_INSTANCE 테이블은 **Job Parameter에 따라 생성되는 테이블.**

**Job Parameter란, Spring Batch가 실행될때 외부에서 받을 수 있는 파라미터.**

BATCH_JOB_INSTANCE 테이블에는 같은 Batch Job이라도 **Job Parameter가 다르면 BATCH_JOB_INSTANCE에 기록**되지만, **Job Parameter가 같다면 기록되지 않습니다.**



JobConfiguration에서 jobParameter를 받을수 있도록 코드를 수정한후

![image](https://user-images.githubusercontent.com/57162257/136897786-02e0d511-97ee-4b66-a697-fc5b2108a5e8.png)

![image](https://user-images.githubusercontent.com/57162257/136897975-ab912412-05bd-45ca-8428-05b080c17790.png)

requestDate를 jobParameter로 보내주면 Spring Batch에 요청한 parameter가 맞게 들어온것을 확인할수있고

![image](https://user-images.githubusercontent.com/57162257/136897853-247c1d3f-796b-4a54-b33a-0b5401d3e4fc.png)

![image](https://user-images.githubusercontent.com/57162257/136898165-e0902356-e47a-4b60-9ab1-35553b601d0a.png)

BATCH_JOB_INSTANCE에도 기록이 잘되어있는것을 확인할수 있다.

만약 BATCH_JOB_INSTANCE에 존재하는 Batch Job에 동일한 parameter를 실행시킨다면 

```apl
A job instance already exists and is complete for parameters={requestDate=20211012}.  If you want to run this job again, change the parameters.
```

라고 `JobInstanceAlreadyCompleteException`이라는 Exception과 함께 메시지를 발생시킨다.



## 3-2. BATCH_JOB_EXECUTION



![image](https://user-images.githubusercontent.com/57162257/136899058-b9d2dd43-9fe4-498a-80ad-e79dc808c000.png)

![image](https://user-images.githubusercontent.com/57162257/136899188-57c0daa5-3295-4a63-985d-a50580ba87f1.png)

현재의 BATCH_JOB_EXECUTION이다.

현재까지 jobParameter가 `null`인 Spring Batch를 4번 실행시켰고, `requestDate = 20211012`, `requestData = 20211011`인 jobParameter를 각 한번씩 실행시킨 결과이다.

JOB_EXECUTION_ID가 1은 성공적으로 수행되었지만, 2~4까지는 `EXIT_CODE가 COMPLETED`인 것을 확인할수 있고 EXIT_MESSAGE에 `A job instance already exists and is complete for parameters={requestData=null}`.이라고 적혀있다. 이는 내가 1번 job은 성공시켰지만 2~4번 job은 이미 실패한 job 내역을 가지고 있다.

> *EXIT_CODE와 STATUS의 차이는 뒤에서 공부할거다.
>
> 짧게 설명하면 STATUS는 JOB Flow 자체를 모두 성공했는가에 대한 상태값이고, EXIST_CODE는 job flow 도중 발생한 이벤트에 대한 상태값이다.
> `(job flow도중 예외처리가 발생한 경우 STATUS : FAILED, EXIT_CODE : FALED)`
> `(job flow도중 사용했던 jobParamter를 다시 이용한 job실행시 STATUS : COMPLETE, EXIT_CODE : NOOP)`

![image](https://user-images.githubusercontent.com/57162257/136899307-f0638cd2-875b-4209-a70f-b0b5931cef38.png)

하지만 BATCH_JOB_INSTANCE를 확인해보면 3개의 job instance가 있는 것을 확인할수 있다.

즉, **BATCH_JOB_INSTANCE는 성공한 인스턴스만, BATCH_JOB_EXECUTION은 JOB_INSTANCE가 성공/실패했던 모든 내역을 가지고 있다**.(**부모-자식 관계**)

그리고 동일한 Job Parameter로 성공한 기록이 있을때만 재수행이 안되고 성공한 기록이 없다면, 성공한 기록이 있을때 까지는 지속적으로 기록을 해준다.



## 3-3. BATCH_JOB_EXECUTION_PARAMS

![image](https://user-images.githubusercontent.com/57162257/136901055-21667613-e833-42e9-a5d8-7972e4c5002a.png)

Batch에서 사용한 jobParameter를 확인할수 있다.



## 참고

https://jojoldu.tistory.com/326?category=902551