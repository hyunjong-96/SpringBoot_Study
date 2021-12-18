# Stream



## 1. 스트림이란

스트림은 자바 8에서 추가된 기능으로 함수형 인터페이스인 람다(lambda)를 활용할수 있는 기술이다.

배열이나 컬렉션에 있는 요소들을 반복문을 통해 순회하며 코드를 작성했다면 stream을 통해 좀더 간결하게 코드를 작성할수 있다.

또한, stream을 사용한다면 데이터를 병렬로 처리할수있다. 기존 반복문을 사용한다면 `synchronized`와 같은 병렬성을 위한 동기화 코드를 관리해야한다. `synchronized`는 나중에 따로 정리하도록 하자.

stream은 크게 3가지 단계가 있다.

1. 컬렉션이나 배열로 부터 **스트림을 생성**하는 작업(Stream Source)
2. 스트림을 필터링하거나 요소를 알맞게 변환하는 **중간 연산**(Intermediate Operations)
3. 최종적인 결과를 도출하는 **단말 연산**(Terminal Operations)



### 컬렉션으로 생성

```java
//list와 list2는 동일하다.
List<String> list = Arrays.asList("lee","hyun","jong");
List<String> list2 = new ArrayList<>(Arrays.asList("lee","hyun","jong"));

Stream<String> stream = list.stream();
Stream<String> stream2 = list.stream();
```

### 배열로 생성

```java
String[] arr = new String[]{"lee","hyun","jong"};
Stream<String> stream = Arrays.stream(arr);

//0번째 인덱스만 선택(closed range)
Stream<String> specificStream = Arrays.stream(arr, 0, 1);
specificStream.forEach(System.out::println); //"lee"만 출력
```

### 병렬 스트림 생성

`stream`대신에 `parallelStream`메서드를 호출하면 **병렬 스트림**을 생성할 수 있다.

각각의 스레드에서 작업을 처리할수 있도록 스트림 요소를 여러 **청크(chunk)**로 분할가능.

*청크 : 작업 단위

```java
List<String> list = new ArrayList<>(Arrays.asList("lee","hyun","jong"));
Stream<String> stream = list.parallelStream();
```





## 2. Map

map메서드를 사용하면 단일 스트림 안의 요소를 원하는 특정 형태로 변환할수 있다.

```java
public Person{
  private String name;
  private int age;
  //...
}

public class MapTest(){
  public static void main(String[] args){
    List<Person> personList = Arrays.asList(new Person("hyun",26), new Person("jong",27));
    
    List<String> convertPersonToName = personList.stream()
      .map(Person::getName)
      .collect(Collectors.toList());
    
		convertPersonToName.forEach(System.out::println);	//"hyun","jong" 출력
  }
}
```

 stream과 동시에 람다식과 메서드 참조를 사용해서 좀더 간결하게 코드를 작성할 수 있다.



## 3. FlatMap

flatMap 메서드는 스트림의 향태가 배열과 같을 때, 모든 원소를 단일 원소 스트림으로 반환할 수 있다.

```java
//Stream<String[]>
//Stream<Stream<String>
//String[][]
[
  [1, 2],
  [3, 4],
  [5, 6]
]

==>
//Stream<String>
//String[]
[1, 2, 3, 4, 5, 6]
```

FlatMap은 2개 수준의 stream을 하나의 stream수준으로 변환하거나 2d 배열을 1d배열로 변환하는데 사용된다.

이렇게 단일 스트림으로 변경해주는 이유는 `List<List< String >>`이나 `Stream<Stream< String >>` 또는 둘 이상의 배열이나 리스트를 포함하고 있는 스트림을 처리하기는 어렵다.. 그래서 `List< String >`이나 `Stream< String >`같이 하나의 레벨로 평면화해주는것이다.



```java
public class FlatMapTest(){
  public static void main(String[] args){
    String[][] array = new String[][]{{"a","b"},{"c","d"},{"e","f"}};
    
    //배열을 스트림으로 변경
		Stream<String[]> convertArrayToStream = Arrays.stream(array);
    
    List<String> convertStreamToList = convertArrayToStream	//Stream<String[]>
      .filter(element -> !element.equal("a"))	//Stream<String[]>
      .collect(Collectors.toList());	//convertToList
    
    System.out.println(convertStreamToList.size()); //0
  }
}
```

`convertStreamToList`의 길이가 0으로 반환되었다. 그 이유는 filter메소드의 element는 "a","b"와 같은 값들이 아닌 `[a, b], [c, d]`인 배열인 친구들이다. 그러니 "a"와 같지않아 항상 false를 가리키고 있는것이고 모든 element들이 필터링된것이다.

값이 나오게 하려면 어떻게 짜야할까?

```java
public class FlatMapTest(){
  public static void main(String[] args){
    String[][] array = new String[][]{{"a","b"},{"c","d"},{"e","f"}};
    
    //배열을 스트림으로 변경
		Stream<String[]> convertArrayToStream = Arrays.stream(array);
    
    List<String> convertStreamToList = convertArrayToStream	//Stream<String[]>
      .filter(elementArray -> {	//Stream<String[]>
        for(String element : elementArray){	//Stream<String>
          return !element.equals("a");
        }
      })
      .collect(Collectors.toList());	//convertToList
    
    System.out.println(convertStreamToList.size()); //2
    
    convertStreamToList.forEach(x->System.out.println(Arrays.toString(x)));
  }
}

==>
  
[c, d]
[e, f]
```

`stream`의 `filter`메소드를 리펙토링해서 `elemetArray(String[])`을 for문으로 돌려 각 `element(String)`들을 비교해 "a"인지 아닌지 비교를 한 후 "a"가 포함되어있는 `elementArray( [a,b] )` 요소를 제외한 `[c, d], [e, f]` 요소들만 필터링을 거쳐 반환되게 된다.



이제 FlatMap을 사용해서 위와같은 코드를 작성해보자.

```java
public class FlatMapTest(){
  public static void main(String[] args){
    String[][] array = new String[][]{{"a","b"},{"c","d"},{"e","f"}};
    
    //배열을 스트림으로 변경
		Stream<String[]> convertArrayToStream = Arrays.stream(array);
    
    List<String> convertStreamToList = convertArrayToStream //Stream<String[]>
      .flatMap(Stream::of)	//Stream<String>
      .filter(element -> !element.equals("a"))	//filter
      .collect(Collectors.toList());	//convertToList
    
    convertStreamToList.forEach(x->System.out.println(Arrays.toString(x)));
  }
}

==>
  
b
c
d
e
f
```

첫번째 코드에서 하려고했던 filter메소드를 flatMap를 하나 거치니까 바로 필터링이 된다.

하지만 주의해야할 점은 FlatMap을 통해 단일 원소로 변경했기때문에 배열이 아닌 String으로 풀려서 반환되게 된다는점 유의하면서 적절하게 사용하면 될것같다.

```java
public User{
  private String name;
  private List<Book> bookList;
  //...
}

public Book{
  private String title;
}

public class FlatMap(){
  public static void Main(String[] args){
    User user1 = new User("hyun", 26);
    User user2 = new User("jong", 27);
    
    user1.set(new Book("java를 java라"));
    user1.set(new Book("나 java바라"));
    user1.set(new Book("애플 python"));
    
    user2.set(new Book("java를 java라"));
    user2.set(new Book("java는 햄, javascript는 햄스터"));
    user2.set(new Book("python python"));
    
    List<User> userList = new ArrayList<>();
    userList.add(user1);
    userList.add(user2);
    
    List<String> convertToTitle =  userList.stream()	//Stream<User>
      .map(User::getBook)	//Stream<List<Book>>
      .flatMap(Collection::stream)	//Stream<Book>
      .filter(book -> !book.getTitle().equals("python"))	//filter
      .map(Book::getTitle)	//Stream<String>
      .collect(Collectors.toList());	//convertToList
    
    convertToTitle.forEach(System.out::println);
  }
}

==>
  
java를 java라
나 java바라
java를 java라
java는 햄, javascript는 햄스터
```

User의 Book리스트 중에서 "python"을 포함하고 있는 책을 제외한 책 이름을 반환하는 코드.

FlatMap메소드는 이중 배열이나 이중 리스트를 다뤄야할때 유용하지만 이중 배열 자체가 다루기 까다롭기 때문에 그래도 아직은 헷갈린다.

해결!



## 참고

https://mkyong.com/java8/java-8-flatmap-example/

https://codechacha.com/ko/java8-parallel-stream/

https://qkrrudtjr954.github.io/java/2017/10/15/difference-between-map-and-flatMap.html

https://madplay.github.io/post/difference-between-map-and-flatmap-methods-in-java