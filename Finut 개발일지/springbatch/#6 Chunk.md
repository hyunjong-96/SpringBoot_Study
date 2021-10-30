# #6 Chunk 지향 처리



## 6-1  Chunk

Spring Batch에서의 Chunk란 데이터 덩어리로 작업 할때 각 커밋 사이에 처리되는 row 수.

**즉, Chunk 지향처리란 한 번에 하나씩 데이터를 읽어 Chunk라는 덩어리를 만든 뒤, Chunk단위로 트랜잭션을 다루는 것.**

**여기서 Chunk단위로 트랜잭션을 수행하기 때문에 실패할 경우 Chunk만큼만 롤백되어 이전에 커밋된 트랜잭션 범위까지는 반영이 된다.**

1. Chunk-1![image-20211014113151128](/Users/flab1/Library/Application Support/typora-user-images/image-20211014113151128.png)
2. Chunk-2![image](https://user-images.githubusercontent.com/57162257/137240567-e37f9dd4-e568-414b-9c05-16aa302b20f5.png)

그림에서 보다싶이 Chunk단위에는 ItemReader, ItemProcessor, ItemWriter가 있고, 그 안에 Item단위에는 ItemReader와 ItemProcessor가 있다.

Chunk-1그림에서는 Chunk단위에 하나의 Item이 있다고 가정하는것이고, Chunk-2그림에서는 두개의 item을 만들어 다루는 과정을 보여주고 있다.

그림을 정리하자면

1.  Reader에서 데이터를 하나 읽어온다.
2. 읽어온 데이터를 Processord에서 가공한다.
3. 가공된 데이터를 모아서 Chunk단위만큼 쌓이면 ItemWriter에 전달하고 Writer는 일괄저장된다.

**즉, Chunk지향 처리란 Chunk단위로 Reader와 Process를 통해 가공된 데이터를 묶어서 처리한다이다.**

Chunk지향처리를 Java코드로 표현하면 이렇다.

```java
for(int i=0; i<totalSize; i+=chunkSize){ // chunkSize 단위로 묶어서 처리
    List items = new Arraylist();
    for(int j = 0; j < chunkSize; j++){
        Object item = itemReader.read()
        Object processedItem = itemProcessor.process(item);
        items.add(processedItem);
    }
    itemWriter.write(items);
}

```

Chunk Size별로 reader와 processor로 데이터를 가공하고 item을 write한다.



## 6-2 ChunkOrientedTasklet

//itemReader, itemProcess, itemWriter공부한후 돌아오겠다



## 참고

https://jojoldu.tistory.com/331?category=902551

https://dahye-jeong.gitbook.io/spring/spring/2021-02-15-spring-boot/2020-03-23-batch/2021-02-09-batch-chunk