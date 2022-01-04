# [QueryDsl]  join



## 1. join

일반적인 inner join

```java
List<Client> clientList = queryFactory
  .selectFrom(client)
  .join(client.trainer, client)
  .where(client.id.eq(client.trainer.id))
  .fetch();
```

연관관계가 되어있는 엔티티간에는 위의 코드로 할수 있다.

```java
public class Exercise{
  @Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String name;

	//...
}

public class FavoriteExercise{

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@OneToOne
	@JoinColumn(name = "exercise_id")
	private Exercise exercise;
  
  //...
}
```

exercise와 favoriteExercise는 일대일 매칭이지만 단방향이라 exercise에서는 favoriteExercise를 확인할수없다.

```java
List<Exercise> exerciseList = queryFactory
  .selectFrom(exercise)
  .join(favoriteExercise).on(exercise.eq(favoriteExercise.exercise))
  .fetch();
```

이렇게 exercise에서 참조할수없는 favoriteExercise와도 inner join이 가능하다.



## 2. theta Join

예전 querydsl에서는 연관관계가 없다면 join을 할수 없었는데 요즘 버전에서는 연관관계 없이 조인이 되는 기능을 지원한다. 

```java
List<Member> memberList = queryFactory
  .select(member)
  .from(member, team)
  .where(member.name.eq(team.name))
  .fetch();
```

회원이름과 팀이름이 같은 경우 join을 하는 방법인데. from 절에서 엔티티를 선택하는 경우를 세타 조인이라고한다 (카티시안 곱 + 선택 연산)

세타 조인에서는 외부 조인을 불가능하다.



## 3. fetch join

페치 조인은 JPQL에서 성능 최적화를 위해 제공하는 기능, SQL의 조인 종류와는 다르다.

```java
@Getter
@Entity
public class Member{
  @Id
  private Long id;
  private String username;
  private Integer age;
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColum(name = "team_id")
  private Team team;
}

@Getter
@Entity
public class Team{
  @Id
  private Long id;
  private String team;
  @OneToMany(mappedBy="team")
  priavte<Member> members = new ArrayList();
}
```

Member와 Team은 다대일 양방향 관계이다.

```java
//Team1 : 나무늘보1, 나무늘보2
//Team2 : 나무늘보3

List<Member> memberList = memberRepository.findAll();
for(Member m : memberList){
  System.out.println("member="+m.getUsername()+" , "+m.getTeam().getName())
}
```

<img src="https://user-images.githubusercontent.com/57162257/148019483-9e344aab-04d3-40cc-9d18-a2cae564f409.png" width="50%">

1. select를 통해서 member를 가져왔다
2. member에서 참조 필드(team)을 불러왔기떄문에 프록시로 인해 영속성 컨텍스트에 의해서 lazy loading이 일어나 select문을 다시 호출한다.
3. 나무늘보, 나무늘보2는 같은 팀1이기 때문에 나무늘보가 불러온 team이 영속성 컨텍스트에 남아 있기떄문에 select가 두번 호출되지 않는다. 하지만 나무늘보3은 다른 팀인 팀2이기 때문에 select를 한번 더 호출하게 된다.

이러한 문제를 `N+1`문제 라고 한다.

여기서 1은 member를 불러오기위한 첫 번째 쿼리, 이고 나머지 쿼리는 N이 된다.

예를 들어 100개의 서로 다른 팀을 가진 나무늘보가 있을때 member를 찾는 쿼리 한번 + 다른 팀을 찾는 쿼리 100개가 발생하게 되는것이다.

해당 문제를 해결하기 위해 필요한 것이 `fetch join`이다.

**OneToMany의 fetch join**

```java
@Query("SELECT m FROM Member m join fetch m.team")
public List<Member> findAllFetchJoinTeam();

List<Member> memberList = memberRepository.findAllFetchJoinTeam();
for(Member m : memberList){
  System.out.println("member="+m.getUsername()+" , "+m.getTeam().getName())
}
```

<img src="https://user-images.githubusercontent.com/57162257/148020845-abf6d7bf-da8d-43b2-a733-a3cc5d85d643.png" width="50%">

Fetch join을 했더니 쿼리에서 team이 member와 조인이 되서 반환되는것을 확인할수 있다.



**ManyToOne의 fetch join**

```java
@Query("SELECT t FROM Team t join fetch t.member")
private List<Team> findAllFetchJoinMember();

List<Team> teamList = teamRepository.findAllFetchJoinMember();
for(Team t : teamList){
  System.out.println("member="+t.getName()+" , "+t.getMembers().size());
}
```

<img src="https://user-images.githubusercontent.com/57162257/148021477-986fd97f-d4e6-4200-bd7a-00a246e3cde7.png" width="50%">

team에서의 fetch join도 마찬가지로 team에 member가 조인된 상태로 반환되게 된다.

그런데 log를 잘확인해보면 `중복된 데이터(team = 팀1 | members=2)`가 출력된 것을 확인할수 있다.



<img src="https://user-images.githubusercontent.com/57162257/148021735-daf77143-2adc-4a01-b797-6ca53ff12fcc.png" width="50%">

위의 그림에서 보면 팀A의 pk를 가지고 있는 회원이 총 2명이기 때문에 2개의 team이 반환된것이다.

위의 ManyToOne fetch join코드에서도 마찬가지로 팀1에는 나무늘보, 나무늘보2가 있기때문에 팀1의 나무늘보, 팀1의 나무늘보2, 팀2의 나무늘보3로 인해 3개의 결과가 반환되었고 중복으로 나오게되는것이다.

중복되는 결과에 대한 쿼리는 DISTINCT를 추가해주면 되는데 **SQL에서의 DISTINCT는 각 행의 값이 완전히 같지 않으면 중복을 제거할수 없기 때문에 중복제거에 실패**를 한다.

하지만 JPQL의 DISTINCT는

<img src="https://user-images.githubusercontent.com/57162257/148023041-95b25ff9-e02d-420c-b0ba-a64a2b130fea.png" width="50%">

**같은 식별자를 가진 엔티티를 제거**해 주기 때문에 SQL DISTINCT와는 다른 결과를 반환한다.

```java
@Query("SELECT distinct t FROM Team t join fetch t.members")
private List<Team> findAllDistinctFetchJoinMember();

for(Team t : teamList){
  System.out.println("team = "+t.getName()+" | members="+t.getMembers().size());
  for(Member m : t.getMembers()){
    System.out.println(" - members="+m);
  }
}
```

<img src="https://user-images.githubusercontent.com/57162257/148024848-486669c0-fcfd-4d4f-976c-04ba92bcdfd8.png" width="70%">

ManyToOne에서 distinct를 곁들인 fetch join의 결과는 아까의 중복(식별자가 같은 엔티티)은 제거가 되었고 참조필드를 통해 무리없이 가져올수 있다.



## 참조

https://devkingdom.tistory.com/245

https://sloth.tistory.com/13