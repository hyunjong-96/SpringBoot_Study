# #9 ItemProcessor

ItemProcessor는 `ChunkOrientedTasklet`을 구성하는 3가지중 필수구현이 아닌 것이다.

## 9-1. 변환

```java
@Bean
public ItemProcessor<Teacher, String> processor(){
  return teacher -> teacher.getName();
}
```

- Teacher -> String 으로 Writer로 들어가는 item의 타입을 변경시켜줄수있다.
- 타입 뿐만아니라 결과값도 변경가능

## 9-2. 필터

```java
@Bean
public ItemProcessor<Teacher, Teacher> processor(){
  return teacher -> {
    boolean isManTeacher = teacher.getSex().equal(Sex.MAN);
    if(isManTeacher){
      log.info(">>>>> Teacher name={}, Sex={}",teacher.getName(), teacher.getSex());
      return null;
    }
    return teacher;
  }
}
```

- ItemProcessor에서 null을 반환한 item은 Writer로 전달되지 않는다.
- item을 반환할때는 꼭 람다식을 사용한 item변수를 반환시켜준다.

## 9-3. 트랜잭션 범위

Spring Batch에서 **Chunk 단위가 트랜잭션의 범위**라고 했었다. 그렇기 때문에 `ItemProcessor`, `ItemWriter`든 모두 `Lazy Loading`이 가능하다.

## 9-4. ItemProcessor구현체

- ItemProcessorAdapter
- ValidatingItemProcessor
- CompositeItemProcessor

최근에는 대부분 Processor구현을 직접 구현을 많이하고 람다식으로 빠르게 구현할때가 많아서 ItemProcessorAdapter와 ValidatingItemProcessor는 사용을 많이 안한다고한다. 하지만 CompositeItemProcessor는 종종 사용한다고한다.

CompositeItemProcssor는 ItemProcessor간의 체이닝을 지원하는 것이다.

무슨말인고 하니, Processor에서 변환이 2번 필요할때 Processor에서 두번의 변환을 하는것은 Processor의 역할이 너무 커지기 떄문에 이때 사용해주는 것이라고 한다.

```java
@Bean
public CompositeItemProcessor compositeProcessor(){
  List<ItemProcessor> delegates = new ArrayList<>(2);
  delegates.add(processor1());
  delegates.add(processor2());
  
  CompositeItemProcessor processor = new CompositeItemProcessor<>();
  
  processor.setDelegates(delegates);
  
  return processor;
}

public ItemProcessor<Teacher, String> processor1(){
  return Teacher::getName();
}

public ItemProcessor<String, String> processor2(){
  return name -> "안녕하세요. "+name+"입니다."
}
```

- `Teacher -> String` 으로 `String -> String`으로 두번의 변경을 해주었을때 두개의 process를 담고 있는것이 `CompositeItemProcessor`이다.
- `CompositeItemProcessor`에 ItemProcessor List인 `delegates`을 할당만 하면 구현을 끝난다.
- 만약 같은 타입을 사용하는 process라면 제네릭을 사용하면 좀 더 안전한 코드가 될수 있다고 한다.

어렵지 않은 내용이니 결과만 딱 정리했다.