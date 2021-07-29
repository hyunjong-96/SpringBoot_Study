# #13 User 도메인 개발

----------------------------

## Status 406, "Not Acceptable"

![image](https://user-images.githubusercontent.com/57162257/127344057-e9ae4424-807a-429c-b513-7fcbc0bcb50f.png)

![image](https://user-images.githubusercontent.com/57162257/127344315-0f27dbc6-d0b0-4b81-80af-b3f1a758344d.png)

사용자의 address를 뽑아오려고 하니까 406을 처음 받아봤다. 

검색결과 보통 jackson라이브러리가 추가되어있지 않아 발생한다고 한다. 하지만 jackson라이브러리는 이미 깔려있었고 확인해보니, 

반환 Dto인 `AddressResDto`에 `@Getter`가 없었다.

https://mommoo.tistory.com/83 의 말에 따르자면, **json을 java로, json을 java로 변환해주는 친구가 jackson이라는 친구인데, 이 jackson은 멤버변수의 유무로 json을 출력해주는 것이 아닌, 프로퍼티(클래스의 필드값)를 getter를 이용하여 가져온다고 한다.**

즉, <u>**반환해주는 dto에 @Getter가 선언이 되어있지 않았기 떄문에 오류가 발생했었다.**</u>

@Getter가 아닌 다른 방식으로 매핑하는 방법을 알고싶다면 위의 링크에서 공부해보자.



-------------------------------------



## @ElementCollection

- **@ElementCollection이란?**
  - 지정된 속성이 컬렉션을 저장할 것이라고 설명한다.
  - 컬렉션의 저장될 정보는 별도의 테이블에 존재한다.
- **@ElementCollection을 사용하는 경우**
  - 한 테이블에서 연관된 다른 테이블의 정보를 다룬다.(**One To Many**)
    - @Embeddable객체와 관계를 정의하여 사용할 수 있다.
  - **@Entity**를 받는 속성을 정의할수 없다.
    - @Entity를 받으려면 **@OneToMany**를 사용해야한다.
    - 즉, 간단한 Collection의 타입만 사용할 수 있다는것.
  - 이 annotation이 설정된 속성은 부모 클래스와 별도로 저장하거나, 테이블에서 가져올수 없다.
    - 부모 클래스에서 @ElementCollection이 선언된 필드값을 통해 가져올수있다.(**FetchType.LAZY**)
  - 관계 테이블의 데이터는 무조건 부모와 함꼐 저장되고 삭제되고 관리된다.
    - cascade옵션이 없다.(비슷은 하다)
- JPA가 지원하는 콜렉션 Type
  - List : 인덱스 기반의 순서가 있는 값 목록
  - Set : 중복을 허용하지 않는 집합
  - Map : (키,값) 쌍을 갖는 맵
  - Collection : 중복을 허용하는 집합



- User.class

  ```java
  @Entity
  public class User{
      @Id
      @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_sequence_gen")
      @SequenceGenerator(name = "user_sequence_gen", sequenceName = "user_sequence")
      private Long id;
      
      private String name;
      
      @ElementCollection
      @CollectionTable(name = "user_address", joinColumns = @JoinColumn(name = "user_id"))
      @Column(name = "address")
      @OrderColumn(name = "address_id")
      private List<Address> addresses;
      
      ...
          
  }
  ```

  - **@ElemetCollection**
    - 관계하는 테이블의 Entity클래스를 생성하지 않고 사용할때 쓴다.(보통 collection)
    - @Embeddable타입에 대한 콜렉션 매핑을 한다(Address.class)
  - **@CollectionTable**
    - collection을 저장할 때 사용할 테이블 지정
      - name : collection테이블 이름 지정
      - joinColumns : collection테이블에서 엔티티 테이블을 참조할 때 사용할 칼럼 이름을 지정
        - 따로 지정하지 않으면 "엔티티이름_엔티티의 pk"
  - **@OrderColumn**
    - collection테이블에서 리스트의 인덱스 값을 지정할 칼럼 이름을 지정
      - 지정하지 않는다면 "속성이름_index"

- Address.class

  ```java
  @NoArgsConstructor
  @AllARgsConstructor
  @Embeddable
  public class Address{
      private String cityDistinct;
      private String primaryAddress;
      private String detailAddress;
      
      ...
          
  }
  ```





![image](https://user-images.githubusercontent.com/57162257/127537250-22b7ec7b-69fa-4ad0-962d-3a0fd6c5905f.png)

![image](https://user-images.githubusercontent.com/57162257/127537433-5fbd9ccb-8b04-4275-a6ce-26f37459226f.png)



**저장**

- User에서 List< Address > addresses 이다.
- Address 생성자로 Address를 생성해준다.
- 저장할 Address리스트들을 setAddresses()를 통해 저장해준다.



https://gunju-ko.github.io/jpa/2019/06/15/JPA-%ED%94%84%EB%A1%9C%EA%B7%B8%EB%9E%98%EB%B0%8D-%EC%9E%85%EB%AC%B8-chapter09.-%EA%B0%92-%EC%BB%AC%EB%A0%89%EC%85%98-%EB%A7%A4%ED%95%91.html

https://kogle.tistory.com/138?category=872749
