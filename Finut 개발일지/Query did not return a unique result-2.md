# Query did not return a unique result:2



Finut코드에서 스케줄을 삭제하려고할때 갑자기 `Query did not return a unique result:2`라는 에러가 떴다. 분명 테스트를 했었는데 이런 에러는 안떴어서 당황했었다.



## 발생

이 Exception은 데이터베이스에서 하나 이상의 값에 엑세스하고 있기 때문이다.

예를 들어 `JpaRepository`에서 `Optional< Schedule >findByUserIdAndType` 와 같이 pk을 통해 찾지않고 다른 조건을 통해 select문을 요청했을때 UserId와 Type이 조건에 맞는 값이 두개 이상이라면 에러가 발생한다는 것이다.

나는 저 위와같은 쿼리메소드를 작성했고 쿼리문으로 직접 찾아보니 3개의 값이 반환되었다.



## 해결

사실 validation용으로 만든것이기 때문에 하나 이상이 있다면 exception을 띄워주었다. 

그렇기에 List< Schedule >로 해서 size를 비교할까 하다가 검색해보니 findTopBy 쿼리 메소드가 있어서 `findTopByUserIdAndType`을 사용해서 해결했다.

앞으로 JpaRepository를 통해 엔티티를 찾을때는 pk를 통해 찾는 걸 먼저 고려해보고 코드를 짜야할것같다.



해결!

https://alalstjr.github.io/java/2019/06/27/JPA-Repository-%EC%BF%BC%EB%A6%AC-limit-%EC%84%A4%EC%A0%95/