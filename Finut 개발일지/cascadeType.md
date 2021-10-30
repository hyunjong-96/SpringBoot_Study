# CASCADE 와 orphan

## 1. 영속성 전이(CASCADE)란?

- cascade란 특정 엔티티를 영속성 상태로 만들때 연관되어진 엔티티도 함께 영속성 상태로 변경 하는 것을 말한다.

  ```
  Client client = new Client();
  client.setName("현종");
  em.persist(client); //영속성 상태변경
  
  Client client2 = new Client();
  client.setName("평화");
  em.persist(client2); //영속성 상태변경
  
  Trainer trainer = new Trainer();
  trainer.setName("한솔");
  trainer.setClient(client);
  trainer.setClient(client2);
  em.persist(trainer); //영속성 상태변경
  ```

  위의 코드는 3개의 엔티티를 영속 상태로 만들면서 자식 엔티티(Client)의 영속성도 함께 변경 시킬수있다.

  이처럼 JPA에서는 부모 엔티티(Trainer)를 영속 상태로 변경하면서 자식 엔티티의 영속 상태를 변경시킬수 있는데. 이를 **영속성 전이(CASCADE)**라고 한다.



## 2. CascadeTyp

![image](https://user-images.githubusercontent.com/57162257/132654830-9600b0cb-9440-4823-a7fd-364387469bbb.png)

```java
@Entity
public Client{
  @Id @GeneratedValue
  private Long id;
  
  private String name;
  
  @OneToMany(mappedBy = "client")
  private List<Matching> matchings = new ArrayList();
}

@Entity
public Trainer{
  @Id @GeneratedValue
  private Long id;
  
  @OneToMany(mappedBy = "trainer")
  private List<Matchings> clients = new ArrayList();
}

@Entity
public Matching{
  @Id @GeneratedValue
  private Long id;
  
  @ManyToOne @JoinColumn(name = "client_id")
  private Client client;
  
  @ManyToOne @JoinColumn(name = "trainer_id")
  private Trainer trainer;
}
```



### 2-1. 영속성 상태 저장(CascadeType.PERSIST)

영속성 상태 저장을 사용 하기 위해서는 CascadeType.PERSIST옵션을 통해 영속성 상태 변경을 할수 있다.

### 2-3. 영속성 상태 수정(CascadeType.MERGE)

영속성 상태에서 새로운 엔티티가 아닌 영속선 컨텍스트에 저장되어 있는 객체를 수정했을 때 persist가 아닌 merge가 발생하는데, 이 때의 상태를 변경해준다.

### 2-3. 영속성 상태 삭제(CascadeType.REMOVE)

영속성 상태에서 삭제하기 위해서는 자식 엔티티를 삭제 한 후 부모 엔티티를 삭제해야한다.

```
Trainer trainer = em.find(Trainer.class, "1");
Client client = em.find(Client.class, "1");

em.remove(client)
em.remove(trainer)
```

영속성 상태 삭제를 사용하기 위해서는 CascadeType.REMOVE옵션을 통해 영속성 상태 변경을 할수 있다.



## 3. OrphanRemoval

### 고아객체

- 고아객체란 서로 연관관계가 맺어진 상태에서 다른 한쪽에서 존재하지 않는 경우 연관관계를 맺고있는 엔티티를 찾지 못하는 상태, 즉 상대 객체를 찾지 못하는 객체를 고아객체라고한다.

### OrphanRemoval = true

- orphanRemoval은 고아객체를 엔티티를 삭제해줄때 사용한다.
- cascadeType.REMOVE와 큰 차이는 모르겠지만 더욱 강력한 친구인것같다.
- OneToMany 또는 OneToOne인 곳에서만 사용가능하다.

