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