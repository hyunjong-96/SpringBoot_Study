# #8 Comment 도메인 개발

## 1. Comment도메인 설계

```java
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    private String comment;

    @ManyToOne
    @JoinColumn(name = "community_id")
    private Community community;

    @ManyToOne
    @JoinColumn(name = "super_comment_id")
    private Comment superComment;

    @OneToMany(mappedBy = "superComment")
    private List<Comment> subComments = new ArrayList<>();

    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;

	...

    public void setSuperComment(Comment superComment) {
        this.superComment = superComment;
        if (superComment != null) {
            superComment.getSubComments().add(this);
        }
    }

    public void setCommunity(Community community) {
        this.community = community;
        if (this.superComment == null) {
            community.getComments().add(this);
        }
    }
}
```

- 필드명
  - id
  - comment
  - community : comment가 달린 community
  - superComment : 부모 댓글의 여부(댓글이면 null, 대댓글이면 comment유)
  - subComment : 자식 댓글의 여부
  - user : 작성자
- **community**
  하나의 커뮤니티에 여러개의 comment가 등록될수 있음으로 `@ManyToOne`
- **superComment**
  부모 comment는 `@ManyToOne`으로 한다는데 <u>정확히 이해하지 못함..</u>
- **subComment**
  댓글에 여러개의 subComment(대댓글)이 등록될수 있음으로 `@OneToMany`, 그리고 부모 댓글(superComment)와 참조되므로 `mappedBy="superComment"`

-----------------------------------------------------------

## 2. 객체참조와 데이터베이스 연관관계

comment와 community연관관계를 맺어줄때 community의 comments에 대댓글일때는 추가를 해주면 안된다. 이것은 객체 지향으로 편의 메소드를 짠다면 

```java
public void setCommunity(Community community) {
        this.community = community;
        if (this.superComment == null) {
            community.getComments().add(this);
        }
    }
```

이렇게 짜줄수 있다. 그런데 findById()로 해당 community를 불러오면 community의 comments에는 대댓글도 추가되어있는것을 확인할수 있다.

처음에는 로직을 잘못 짠줄 알았는데 그게 아니였다.

```java
[Community]
@OneToMany(mappedBy = "community")
    private List<Comment> comments = new ArrayList<>();

[Comment]
@ManyToOne
    @JoinColumn(name = "community_id")
    private Community community;
```

이 둘은 양방향 연관관계로 맺어져있다. 그렇기 때문에 대댓글이라 comments에 추가가 되지 않더라도 이미 주인인 comment에선 community를 참조하고 있게 된다. 
이렇게 됬을 경우 Object의 관점으로 봤을때는 comments에 해당 comment는 추가되어있지 않다. 하지만 데이터베이스 관점으로 봤을때는 이미 comment를 save시킴으로써 영속성이 끝남과 동시에 데이터베이스에서는 community의 comments엔 대댓글인 comment가 연관관계로 맺어져 참조하고 있는것이다.

즉, 내가 놓친 부분은 편의 메소드로 서로 참조하고 있는 관계에 해당 객체를 추가하지 않는것은(community의 comments에 대댓글comment를 추가하지 않는것) 객체 지향을 유지하면서 코드를 작성하기 위함이다.(community객체도 영속성이 끝나 데이터베이스에 저장되기 전에 추가된 필드값들을 알고있게 하는것이다.)

정리 : **데이터베이스에서 community와 comment와의 연관관계때문에 영속성이 끝났을때 community의 comments에 자동으로 참조가 된다.**

-----------------------------------------------------------

## 3. stream()

### filter

위의 내용에서 findById()를 통해서 저장된 community를 가져올때는 이미 대댓글comment도 comments에 추가되어서 같이 딸려오게 된다. 이를 막기 위해선 comments가 List<>임을 이용한다.

stream()을 이용해서 `filter(v-> v.getSuperComment() == null)`인 comment만 필터링 해서 dto에 담는것이다.

참고로 superComment가 null인것이 댓글 객체가 존재하는것이 대댓글이다



### sorted

List타입을 정렬할때 사용해줌



### map

List타입안에 있는 객체 하나하나를 다른 객체로 묶어주거나 set해줄때 사용.

------------------------------------

## 4. DTO는 api당 몇개 만들어야할까

리드 개발자와 얘기를 하면서 나한테 궁금한점이 있다고 했다.

내가 로직을 구현할때 Controller -> Service로직에서 DTO하나,

Service -> Repository로직에서 DTO하나 이렇게 총 두개의 DTO를 사용하는 이유를 물어봤다. 

일반적으로는 하나의 DTO(Controller에서 Service로직에서 사용하는 DTO)를 쓴다. 
하지만 이론적으로 `Service 계층과 Repository계층은 동일한 계층이 아니기 때문에 전달해야하는 데이터가 있기에 이 두 계층사이에 DTO를 만드는것은 DTO의 개념에 부합한다.` 
그리고 나는 `Controller에서 넘어오는 데이터와 Service에서 넘겨주는 데이터가 다루는게 많고 Repository에 전달해줄때의 데이터값도 다르기 떄문에 따로 만들어서 관리하고 다룰 필요가 있다고 생각한다.`

리드 개발자 말로는 이것도 개발 팀마다의 성격이라고 해서 나중에 팀의 성격에 맞게 개발하면 될것같다.

-----------------------------------

## 5. @PrePersist & @PreUpdate

`@PrePersist` : 해당 엔티티를 저장하기 이전에 호출

- 해당 엔티티를 처음 save하면 @PrePersist가 발생
- 트랜잭션 안에서 같은 엔티티를 save해도 영속성 컨텍스트가 유지되고 있기 때문에 여러변 save해도 @PrePersist가 발생되지 않는다.

`@PreUpdate` : 해당 엔티티를 업데이트하기 이전에 호출 

- 보통 엔티티를 업데이트 할떄는 해당 엔티티의 필드값을 변경해서 수정하는 **더티체킹**을 사용하는데 `EntityManager`에는 update라는 메소드가 없다. 결국엔 더티체킹을 해야하는 건데, 더티체킹을 한다고 해서 @PreUpdate가 발생하지는 않았다. 언제 <u>@PreUpdate가 발생하는지 찾이 못함..</u> 

### @PreUpdate는 왜 동작하지 않는가?

```java
@Transactional
public void Test(){
    User writer = User.builder().id(1L).name("작성자").build();
        User user1 = User.builder().id(2L).name("참여자1").build();
        User user2 = User.builder().id(3L).name("참여자2").build();
        userRepository.save(writer);
        userRepository.save(user1);
        userRepository.save(user2);

        Community community = jointpurchaseEntity(writer);
        Community community2 = freesharingEntity(writer);
        Community saveCommunity = communityRepository.save(community); //1
        Community saveCommunity2 = communityRepository.save(community2); //2

        saveCommunity.setCurrentNumberOfPeople();
        communityRepository.saveAndFlush(saveCommunity); //3
        saveCommunity.setCurrentNumberOfPeople();
        communityRepository.saveAndFlush(saveCommunity); //4

        assertThat(saveCommunity.getCurrentNumberOfPeople()).isEqualTo(1);
        assertThat(saveCommunity.getCurrentNumberOfPeople()).isEqualTo(2);
}
```

```java
@Entity
public class Community{
    @PrePersist
    public void persisTest(){
       System.out.print("PrePersist!!!!");
        System.out.print(id);
    }

    @PreUpdate
    public void preUpdateTest(){
        System.out.print("PreUpdate!!!");
        System.out.print(id);
    }
}
```

1. `insert`를 통해 community를 저장
   ![image](https://user-images.githubusercontent.com/57162257/120146357-e9f73880-c21f-11eb-9665-490da91a2ff2.png)
2. `insert`를 통해 community2를 저장
   ![image](https://user-images.githubusercontent.com/57162257/120146394-fa0f1800-c21f-11eb-9a13-79452e0ac868.png)
3. currentNumberOfPeople이 변경된 community가 `update`쿼리를 바로 데이터베이스에 전달
   ![image-20210531145448142](C:\Users\leehyunjong\AppData\Roaming\Typora\typora-user-images\image-20210531145448142.png)
4. 에러가 발생해서 실행되지 않음

`flush` : 영속성 컨텍스트의 변경 내용을 DB에 반영하는 것(Transaction commit이 일어날때 flush가 발생해서, 쓰기 지연 저장소에 쌓아놨던 SQL문들이 DB에 날아간다)

`flush동작과정`

- 변경감지(dirty checking)
- 수정된 entity를 쓰기 지연 SQL저장소에 등록
- 쓰기지연 SQL저장소의 Query를 DB에 전송
- flush가 동작할수 있는 이유는 데이터베이스 트랜잭션(작업 단위)라는 개념떄문이다,  트랜잭션이 시작 되고 해당 트랜잭션이 commit되는 시점 직전에만 동기화 해주면 되기때문에 플러시 메커니즘의 동작이 가능한것이다.

`saveAndFlush()` : db에 바로 업데이트를 하는것이 아닌 쓰기 지연 SQL저장소로 update Query를 flush하는것 

그외

- @PostLoad : 해당 엔티티를 새로 불러오거나 refresh한 경우
- @PreRemove : 해당 엔티티를 삭제하기 이전

각 영속성 관련 어노테이션 실행 위치 : https://pumpkineaterdotorg.files.wordpress.com/2013/08/lifeent30-e1375858118520.gif

save 와 saveflush차이 : https://happyer16.tistory.com/entry/Spring-jpa-save-saveAndFlush-%EC%A0%9C%EB%8C%80%EB%A1%9C-%EC%95%8C%EA%B3%A0-%EC%93%B0%EA%B8%B0

--------------------------

# org.hibernate.LazyInitializationException

위에 설명했던 데이터베이스의 연관관계 때문에 Service계층하고 Repository계층하고의 통합테스트를 해보다가 위의 에러를 발견했다.

김영한님께서 

> org.hibernate.LazyInitializationException 이 오류는 엔티티 조회까지는 성공했는데, 연관관계의 엔티티를 호출해서 사용할 때 영속성 컨텍스트가 종료되어 버려서, 지연 로딩을 할 수 없어서 발생하는 오류 입니다. JPA에서 지연로딩을 하려면 항상 영속성 컨텍스트가 있어야 하거든요.
>
> 보통은 트랜잭션 밖에서 엔티티를 조회할때 이런 문제가 발생한다.

라고 했다. 그래서 `@Transactional`을 추가해줘서 해결



# Caused by: org.h2.jdbc.JdbcSQLSyntaxErrorException: ... expected "identifier"; SQL statement:

> ```
> Caused by: org.h2.jdbc.JdbcSQLSyntaxErrorException: Syntax error in SQL statement "ALTER TABLE ORDER_DETAIL ADD CONSTRAINT FKPLAM7WXC4TJBGEX0XYK8F0QXO FOREIGN KEY (ORDER_ID) REFERENCES ORDER[*] (ORDER_ID) "; expected "identifier"; SQL statement:
> ```

스프링부트를 실행하니까 `expected "identifier"`어쩌구 저쩌구 하는 에러가 발생했는데

**이유** 

엔티티의 테이블 명 때문이였다. `Like`라는 이름을 썼는데 Like는 SQL의 키워드 중 하나기 때문에 테이블의 이름으로 사용할수 없다는 것이였다.

**해결**

다른 테이블 명을 사용해주면 된다. :)