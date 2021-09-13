# @Transactional



##  트랜잭션 이란?

- 데이터베이스의 상태를 변경하는 작업 또는 한번에 연산되어야하는 연산들을 의미
- begin, commit  을 의미
- 예외 발생시 rollback처리를 자동으로 해준다.

## 트랜잭션의 4가지 성질

- **원자성**
  - 한 트랜잭션에서 실행한 작업들은 하나로 처리한다.(전부 실패 | 전부 성공)
- **일관성**
  - 트랜잭션은 일관성 있는 데이터베이스 상태를 유지한다.
- **격리성**
  - 동시에 실행되는 트랜잭션들이 서로 영향을 받지 않도록 한다.
- **영속성**
  - 트랜잭션을 성공적으로 마치면 결과가 항상 저장되어야한다.

## 트랜잭션 처리방법

- @Transactional 어노테이션을 메소드, 클래스 등에 선언하여 사용한다
- 이 방법을 **선언적 트랜잭션**이라 한다.
- 적용된 범위에서 트랜잭션 기능이 포함된 프록시 객체가 생성되어 자동으로 commit 혹은 rollback을 진행시켜준다.

### "Make this method "public" or remove the "@Transactional" annotation"

- 이런 exception이 발생하는 경우, @Transactional의 **AOP**를 고려하지 않아 발생한 에러라고한다.

- 이 어노테이션은 컨트롤러에서 서비스를 호출하거나 서비스에서 레포지토리를 사용한때 전처리 혹은 후처리를 하는 것이다.

- 그렇기 떄문에 컴포넌트 내부에서 처리하는 경우에는 이런 어노테이션은 작동하지 않아서 발생하는 것이라고한다.

  

## @Transactional 옵션

1.  **rollbackFor**
   - 특정 예외시 rollback
   - 보통  `@Transactional(rollback = Exception.class)` 을 사용한다고 한다.
   - @Transactional어노테이션은 unchecked Exception, Error만을 rollback하고 있는데 exception같은 경우, 예상한 예외처리를 뜻하느 것이기떄문에  Exception.class를 옵션으로 넣어줘야한다고한다.
     (Uncheckede도 함께 예외처리가 되는지 테스트해볼필요가 있다.)
2. **readOnly**
   - 읽기 전용
   - `@Transactional(readOnly = true)` 을 사용시 insert, update, delet 수행시 예외가 발생한다.
   - Default 값은 false이다.

------------

출저

https://eblo.tistory.com/16

https://stackoverflow.com/questions/4396284/does-spring-transactional-attribute-work-on-a-private-method