# #4 Participation 도메인 개발

# 다대일 연관관계

```java
public class Community{
    @Id
    private Long id;
    
    @OneToMany(mappedBy = "community")
    private List<Participation> participationsList = new ArrayList<>();
}
```

```java
public class Participation{
	@Id
    private Long id;
    
    @ManyToOne
    @JoinColumn(name="community_id")
    private Community community;
    
    public void setCommunity(Community community){
        if(this.community != null){
            this.community.getParticipationsList().remove(this);
        }
        this.community = community;
        community.getParticipationsList().add(this);
    }
}
```

### 연관관계를 맺을 때의 주인

- 연관관계의 주인이란 Participation테이블에 있는 community_id외래 키를 관리자를 선택하는 것.
- 보통 주인은 다 대 일에서 **다**
- 다 쪽이 주인인 이유는 다인 테이블에 외래키가 존재하는데 일인 테이블이 주인이되면 물리적으로 전혀다른 테이블의 외래 키를 관리해야하기 때문이다.
-  때문에 주인이 아닌 Community엔티티가 주인이 아니라고 알려줘야 하기 떄문에 `mappedBy`를 사용해주고 mappedBy의 값으로 사용된 community는 연관관계 주인인 **Participation엔티티의 community필드**를 말한다.

### 양방향 연관관계 주의점

```java
public void test(){
    Community community = Community.builder().id(1L).build();

	Participation participation = Participation.builder().community(community).build();
    
    community.getParticipationsList().size() // 0
}

```

- community와 participation을 모두 만들어줬지만 community의 participationsList의 길이는 0이다.

- 이렇게 되면 양방향 연관관계가 아닌 그냥 단방향 연관관계가 되는것이다. 그렇기 때문에 community객체의 participationList에도 participation객체를 등록해줘야한다.

- 연관관계의 주인인 Participation엔티티에서 setCommunity메소드를 만들어줘 양방향인 community와 participation을 set해준다.

  ```java
  public void setCommunity(Community community){
      if(this.community != null){
          this.community.getParticipationsList().remove(this);
      }//1
      this.community = community;//2
      community.getParticipationsList().add(this);//3
  }
  ```

  1. 현재의 participation객체에 이미 등록된 community가 있다면 새로 들어온 community를 등록시켜줘야함으로 등록되어있는 community의 participationsList에 현재 participation객체를 제거해준다.

  2. 현재 participation객체에 새로 들어온 community객체를 등록

  3. 새로 등록된 community객체의 participationsList에 현재 participation객체를 등록해준다.

  이렇게 이미 맺어져 있는 연관관계의 유무와 처리, 그리고 양방향 등록까지 해주는 set메소드를 `연관관계 편의 메서드` 라고 한다.

https://velog.io/@conatuseus/%EC%97%B0%EA%B4%80%EA%B4%80%EA%B3%84-%EB%A7%A4%ED%95%91-%EA%B8%B0%EC%B4%88-2-%EC%96%91%EB%B0%A9%ED%96%A5-%EC%97%B0%EA%B4%80%EA%B4%80%EA%B3%84%EC%99%80-%EC%97%B0%EA%B4%80%EA%B4%80%EA%B3%84%EC%9D%98-%EC%A3%BC%EC%9D%B8

-------------------------------------------------

# 양방향 JPA Entity

위의 예제에서 participation과 Community는 다대일 양방향 연관관계이다(게다가 예제에는 없지만 Participation은 User까지 양방향이다.`즉, community와 user는 다대다 관계이다`). 이 관계를 JPA를 통해 데이터를 가져오려고하면 재귀로 인해 무한루프가 걸리게 되고 JSON으로 시리얼라이즈하는 순간 스택오버플로우가 발생한다. 

처음에는 이렇게 무한 루프가 걸리게되면 DB에 무리가 가지 않을까 생각했지만 sql문을 확인해보니 join을 이용해서 한눈에 봤을떄는 트래픽이 심해보이는 느낌은 받지 못했다. 게다가 김영한님께서는 많은양의 돈이 왔다갔다하는 로직이나 무거운 로직에도 JPA를 그대로 사용해도 문제가 없었다고 하셨다.

그렇담 이 무한루프가 걸린 Entity를 JSON으로 보내기 위해선 어떻게해야할까? 

바로 DTO를 사용하는 방법이다. 

```java
@Getter
@NoArgsConstructor
public class ParticipationResDto {
    private Long id;
    private CommunityCategory communityCategory;
    private String title;
    private String content;

    @Builder
    public ParticipationResDto(Participation participation) {
        this.id = participation.getCommunity().getId();
        this.communityCategory = participation.getCommunity().getCommunityCategory();
        this.title = participation.getCommunity().getTitle();
        this.content = participation.getCommunity().getContent();
    }
}
```

- findAllByUserId()를 이용해서 엔티티를 불러왔고 이 엔티티를 ParticipationResDto에 넣어줘서 필요한 필드값만 저장한다
- 깔끔

https://ict-nroo.tistory.com/122

------------------------------------------

# PathVariable Vs QueryParam

## @PathVariable

`localhost:8080/api/test/{id}`

보통 pathvarialbe은 값을 호출할때 주로 많이 사용

## @QueryParam

`localhost:8080/api/test?id=1&page=2`

보통 queryparam은 페이지 및 검색 정보를 함꼐 전달하는 방식을 사용할 때 많이 사용.

https://www.baeldung.com/spring-requestparam-vs-pathvariable

https://elfinlas.github.io/2018/02/18/spring-parameter/

## MockMvc의 param

controller테스트를 할때 mockMvc의 params을 사용할때가 있는데 PathVariable로 받는 값은 pathUrl에 직접넣어주고 RequestParam에서 받는 값을 받을때 `MultiValueMapping<String,String> params = new LinkedMultiValueMap<>()`로 wrapper해준 다음 사용해주면 된다.

---------------------------------------------------------

# Delete

객체에서 양방향인것처럼 보이게 하기 위해서는 set메소드를 만들어서 양방향인것처럼 만들어줘야한다.

반면 관계형 데이터베이스는 외래 키 하나로 문제를 단순하게 해결할수있다.

김영한님께서 말씀하시길, `객체에서 양방향 연관관계를 사용하려면 로직을 견고하게 작성해야한다.`라고 하셨다.

특시 객체의 양방향 관계가 제거되지 않아도 데이터베이스 외래 키를 변경하는 데는 문제가 없다. 왜냐면 연관관계의 주인이 아닌 엔티티에서 주인을 참조하는 필드(거의 List< T >일것이다.)는 아니라서 데이터베이스에 전혀 영향을 미치지 않는다.

결국 양방향 연관관계를 사용할때 삭제를 해줘야한다면 양측 객체의 필드에 있는 요소들을 삭제해주는것이 맞다. 하지만 그저 삭제만의 기능을 사용하고 삭제 외에는 다른 기능을 사용하지 않는다면 굳이 편의메소드를 만들어줄필요는 없다고 생각된다.

## controllertest

보통 delete기능을 구현할때는 service layer에서는 void타입을 반환해준다. 그렇기에 mockito의 기능을 평범하게 사용하는데 제약이 따른다.(특정 타입을 반환해주라고 에러발생함) 그렇기 때문에 mockito에 아무것도 반환되지않는다고 알려줘야한다.

```java
doNothing().when(communityService).cancelJoinCommunity(id,reqdto);
```

위의 코드처럼 doNothing()이라는 메소드를 통해 아무것도 반환되지 않는다고 알려준다.

`MockBean인 commnunityService는 모르는걸 시키면 아무것도 하지않는다. 그렇기 떄문에 doNothing()를 잘 알아주자`



# RunWith vs ExtendWith

CommunityServiceTest를 하면서 Mock과 TDD를 이용해서 참여하기 service로직을 테스트 해보려고 했는데 service로직에서 findById()에서 exception이 발생했다. 분명 TDD의 when()을 이용해서 가정해서 값을 올바르게 넣어줬는데도 null로 인해 exception이 발생했다. 

구글링을 통해서 serviceTest를 돌리는데 `RunWith(MockitoJUnitRunner.class)`를 이용해서 돌리는 코드가 있길래 돌려보니 정상적으로 돌아갔다... 

이유는 Junit이 4로 설정되어있었기 때문.. 해결..

서비스테스트 코드

## RunWith(MockitoJUnitRunner.class)

- Mockito의 Mock객체를 사용하기 위한 어노테이션
- JUnit4에서 사용

## ExtendWith(MockitoExtension.class)

- 똑같이 Mockito의 Mock객체를 사용하기 위한 어노테이션
- JUnit5에서 사용

RunWith과 ExtendWith의 차이 : https://www.baeldung.com/junit-5-runwith

## @Mock

- 의존성을 주입하는 방법
- mock객체를 생성
- mock객체란 의존성을 주입해주지만 말 그대로 '**가짜**'객체를 생성하는 것이기 떄문에 Mock을 주입한 객체는 '**가정**'만 가능하며 실제 로직을 흐르게 할수 없다.

## @InjectMocks

- 생성한 Mock객체를 주입하여 사용할 수 있도록 만든 객체

- 쉽게 말하면 **@InjectMocks에서 사용하는 객체를 @Mock으로 만들어 가져다 붙이는 것.**

- 예를 들면 

  ```java
  @RunWith(MiockitoRunner.class)
  public class CommunityServiceTest{
      @InjectMocks
      private CommunityService communityservice;
      @Mock
      private CommunityRepository communityReposiotry;
      
      ...
  }
  ```

  - CommunityRepository는 Mocking의 대상
  - 이러한 Mock객체를 주입당하는 Service가 CommunityService이다.



테스트코드에서 의존성 주입방법 : https://sanghye.tistory.com/24

TDD : https://sanghye.tistory.com/11 (진짜 정리잘된 블로그!!)