# JPA DELETE method not working ?



## 문제

```java
@Entity
public class User{
  @Id
  private Long id;
  
  @OneToMany(mappedBy = "user", cascade={CascadeType.Persist})
  private List<Like> likes = new ArrayList(); 
}

@Entity
public class Post{
  @Id
  private Long id;
  
  @OneToMany(mappedBy = "post", cascade={CascadeType.Persist})
  private List<Like> likes = new ArrayList();
}

@Entity
public class Like{
  @Id
  private Long id;
  
  @ManyToOne
  @JoinColumne(name="user_id")
  private User user;
  
  @ManyToOne
  @JoinColumne(name="post_id")
  private Post post;
}
```

이렇게 User와 Post를 Like엔티티로 연관관계를 맺어 `좋아요 기능`을 관리하고 있다.

이떄 좋아요를 취소했을때 LikeRepository를 통해 user와 post를 가지고 있는 객체를 삭제하여 좋아요 취소 기능을 구현하려고 했다.

하지만 로그에서는 like엔티티에서 user와 post를 찾기만하고 그 다음 삭제하는 쿼리를 실행하지 않아서 삭제가 되지 않았다.



## 해결

참조한 레퍼런스에서는 `대부분의 경우 이러한 동작은 양방향 관계가 있고 부모와 자식이 모두 유지되는(현재 세션에 연결됨) 양쪽을 동기화하지 않을 때 발생합니다.` 라고 한다. **결국 user부분과 post부분에 like엔티티에 대해 영속관계를 걸어줬는데 동기화를 해주지 않아 삭제가 되지 않는다는 뜻이였다.**

그래서 User의 likes와 Post의 likes에 cascadeType.Persist를 삭제해주고 LikeRepository에서 해당 객체를 삭제해주니 삭제가 되었다.

아니면 영속성을 유지시켜주고 싶다면,  부모엔티티에서 부모엔티티에서 참조하고있는 자식 엔티티의 리스트에서 삭제하려는 객체를 삭제해주는 편의 메소드를 만들어서 관리해주는 방법도 가능하다.

## 참조

https://stackoverflow.com/questions/22688402/delete-not-working-with-jparepository

https://velog.io/@sonaky47/JPA-delete%EA%B0%80-%EC%95%88%EB%90%9C%EB%8B%A4