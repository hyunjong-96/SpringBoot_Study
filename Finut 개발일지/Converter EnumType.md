# EnumType Conversions



params나 dto의 필드값으로 enum타입을 받아서 사용하려고 할떄, 문득 처음 controller로 받아왔을때 type이 string일지 enum일지 궁금했다. 그래서 검색을 하다보니 Conversions라는 키워드를 알게 되었고 공부를 하게되었다.

1. ```java
   public class StringToRoleConverter
     implements Converter<String, ROLE> {
   
       @Override
       public ROLE convert(String from) {
         try{
            return ROLE.of(from);
         }catch(IllegalArgumentException e){
           throw new NotMatchEnumType();
         }
       }
   }
   ```

   Enum type Role을 변환해주는 converter를 Converter를 상속받아와서 오버라이드 해준다.

2. 만든 Role Convert클래스를 converter에게 추가되었다고 Spring에게 재정의하여 알려줘야한다.

   ```java
   @Configuration
   public class WebConfig implements WebMvcConfigurer{
   	@Override
     public void addFormatters(FormatterRegistry registry){
       registry.addConverter(new StringToRoleConverter());
     }
   }
   ```

3. 후에 @RequestParams또는 @RequestBody를 통해 들어온 Enum type은 처음에 String타입으로 들어왔다가 convert에 의해 정의한 converter들에 의해서 Enum type으로 재정의 되는 것을 알수 있게 되었다.
   또한 타입이 맞지 않는다면 발생하는 예외처리도 재정의해준 converter에 의해서 발생시켜 핸들링 해줄수 있다.



## 참조

https://www.baeldung.com/spring-type-conversions

https://blog.outsider.ne.kr/826