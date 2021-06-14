# QueryDsl

# 1. gradle

```java
plugins {
    id 'org.springframework.boot' version '2.5.0'
    id 'io.spring.dependency-management' version '1.0.11.RELEASE'
    id "com.ewerk.gradle.plugins.querydsl" version "1.0.10"	//querydsl
    id 'java'
}

group = 'com.springboot_jpa'
version = '0.0.1-SNAPSHOT'
sourceCompatibility = '1.8'

configurations {
    compileOnly {
        extendsFrom annotationProcessor
    }
}

repositories {
    mavenCentral()
}

dependencies {
    ...

    implementation 'com.querydsl:querydsl-jpa'//querydsl
    implementation "com.querydsl:querydsl-apt"//querydsl

   	...
}

test {
    useJUnitPlatform()
}

//querydsl 추가 시작
def querydslDir = "$buildDir/generated/querydsl"	//1

querydsl {
    library = "com.querydsl:querydsl-apt:4.2.1"
    jpa = true
    querydslSourcesDir = querydslDir
}

sourceSets {	//2
    main.java.srcDir querydslDir
}

configurations {	//3
    querydsl.extendsFrom compileClasspath
}

compileQuerydsl {	//4
    options.annotationProcessorPath = configurations.querydsl
}

//querydsl 추가 끝

```

- plugins

  ```
   id "com.ewerk.gradle.plugins.querydsl" version "1.0.10"
  ```

  - querydsl에서 필요한 Q클래스를 생성해주는 plugin
  - Entity기반으로 Q클래스 생성

- dependencies

  ```
  implementation 'com.querydsl:querydsl-jpa'
  implementation "com.querydsl:querydsl-apt"
  ```

  - apt를 이용해서 Entity기반으로 querydsl plugin을 실행시키면 preifx "Q"가 붙는 큐클래스가 생성된다.

  - API : Annotation Processing Tool의 약자
    Annotation이 있는 코드기준으로 새로운 파일을 만들 수 있고 complie기능도 가능.

  - **진행순서**
    `Querydsl -> JPQL -> SQL`

    > JPQL이란?
    >
    > Java Persistence Query Language의 약자.
    >
    > JPA의 일부로 독립적인 객체지향 쿼리 언어.
    >
    > 관계형 데이터베이스의 엔티티에 대한 쿼리를 만드는데 사용된다.
    >
    > 데이터베이스에 다이렉트로 연결되지 않고 JPA엔티티에 대해서 작동.

- 1. Q클래스 생성 위치
  2. 에디터 설정
  3. 컴파일 클래스 패스에 넣음
  4. 어노테이션 프로세서와 맞물려서 빌드시 생성

- Q클래스는 반드시 ignore해줘야한다.
  ![image](https://user-images.githubusercontent.com/57162257/121834447-c9dc7480-cd09-11eb-8be8-f1ec692d6be0.png)
  사진에서는 src/main/generated에 Q클래스를 생성해서 ignore에 저렇게 설정해준것. 



# 2.  Q클래스 생성

gradle설정을 전부 해준다음 

![image](https://user-images.githubusercontent.com/57162257/121834799-a2d27280-cd0a-11eb-9d97-015388756e54.png)

![image](https://user-images.githubusercontent.com/57162257/121834662-3fe0db80-cd0a-11eb-9674-68d9722a5cf9.png)

`Tasks/other/compileJava`와 `Tasks/other/compileQuerydsl`을 눌러 gradle 빌드를 실행해주면 gradle에 설정해준 경로에 Q클래스가 생성된다



# 3.  Configuration설정

```java
@Configuration
public class QueryDslConfiguration {
	@PersistenceContext
	private EntityManager entityManager;

	@Bean
	public JPAQueryFactory jpaQueryFactory(){
		return new JPAQueryFactory(entityManager);
	}
}
```

- JPAQueryFactory를 빈등록후, 어느곳에서든지 JPAQueryFactory를 주입 받아 Querydsl을 사용할수 있게 된다.
- `@Configuration`
  1개 이상의 Bean을 Spring Bean에 등록하기 위해 사용하는 어노테이션
- `@Bean`
  - 개발자가 직접 제어가 불가능한 라이브러리를 활용할 때 사용
  - 초기에 설정을 하기 위해 활용하기 위해 사용
  - **@Bean을 통해 빈 등록이 가능은하지만 메소드 호출을 통해 객체를 생성할 싱글톤을 보장하지 못하므로 `@Configuration`을 함께 사용하는 것을 지향한다.**
- `@PersistenceContext`
  - **EntityManger**는 여러 쓰레드가 동시에 접근하면 동시성 문제로 인해 쓰레드간에는 무조건 공유하면 안된다.
  - 스프링은 일반적으로 싱글톤 기반으로 동작하기 떄문에 모든 쓰레드가 공유하게 된다. 즉 그냥 EntityManger를 사용하게 되면 동시성 문제가 발생할수 있다는 것이다.
  - EntityManager에 `@PersistenceContext`를 주입해주면 EntityManager를 **Proxy로 한번 감싸게 되고**, 이후 EntityManger를 호출 할때마다 Proxy를 통해 EntityManger를 생성하며 `Thread-Safety`를 보장한다.
  - EntityMangerFactory를 통해 바로 생성해준 EntityManger는 Proxy로 감싸져 있지 않다.

# 4. 사용법

## Spring Data Jpa Custom Repository이용

![image](https://user-images.githubusercontent.com/57162257/121841954-60b12d00-cd1a-11eb-944f-a7bbdcf907bc.png)

```java
//MemberRepositoryCustom
public interface MemberRepositoryCustom{
    List<Member> getMembersByQueryDsl();
    Member getMemberByQueryDsl(Long memberId);
}
```

```java
//MemberRepositoryImpl
import static com.springboot_jpa.jpa_study.domain.QMember.member;

public class MemberRepositoryImpl implements MemberRepositoryCustom{
    private final JPAQuaryFactory queryFactory;
    
    public List<Member> getMembersByQueryDsl(){
        return queryFactory
            .selectFrom(member)
            .fetch();
    }
    
    public Member getMemberByQueryDsl(Long memberId){
        return queryFactory
            .select(member.id, member.name)
            .from(member)
            .where(eqId(memberId))
            .fetchOne();
    }
    
    private BooleanExpression eqId(Long memberId){
        return memberId != null ? member.id.eq(memberId) : null;
    }
}
```

- 다이나믹 쿼리를 사용하기 위해서는 `BooleanExpression`타입을 사용해서 파라미터값으로 null값이 들어오게되면 조건절에 포함되지 않도록 이용할수 있다.

```java
//MemberRepository
@Repository
public interface MemberRepository extends JpaRepository<Member,Long>, MemberRepositoryCustom{
}
```

- MemberRepository를 통해 JPA와 QueryDsl를 함께 사용할수 있다.

# 5. Querydsl테스트

```java
@ExtendWith(SpringExtension.class)
@DataJpaTest
@Import(TestConfig.class)
public class RepositoryTest {
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private TeamRepository teamRepository;
    @Autowired
    private MemberRepositoryImpl memberRepositoryImpl;	//1

    @DisplayName("QueryDsl_Test")
    @Test
    public void QueryDslTest(){
        //given
        Member member = Member.builder().name("현종").build();

        memberRepository.save(member);

        //when
        List<Member> members = memberRepositoryImpl.getMembersByQueryDsl();

        //then
        assertThat(members.size()).isEqualTo(1);
        assertThat(members.get(0).getName()).isEqualTo("현종");
    }
}
```

1. Querydsl용 Repository는 QueryDslConfiguration클래스안에있는 `JPAQueryFactory`을 빈 등록을 한것을 통해 사용하는데, @SpringBootTest를 사용해서 테스트를 하면 모든 빈이 주입되기 떄문에 상관없지만, @DataJpATest와 같은 슬라이싱 테스트를 하고 싶을때는 queryFactory 및 상속용 클래스들이 빈으로 등록되지 않아 문제가 발생한다.

   이를 해결하는 방법으로

   ```java
   @TestConfiguration
   public class TestConfig {
   	@PersistenceContext
   	private EntityManager entityManager;
   
   	@Bean
   	public JPAQueryFactory jpaQueryFactory() {
   		return new JPAQueryFactory(entityManager);
   	}
   }
   ```

   `@TestConfiguration`을 통해 테스트용으로 빈 등록을 해주고 테스트 클래스에 `@Import(TestConfig.class)`를 import해줘서 @DataJpaTest를 하고자 할때 테스트용 빈을 등록할수 있다.

