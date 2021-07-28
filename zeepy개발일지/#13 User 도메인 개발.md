# #13 User 도메인 개발

----------------------------

## Status 406, "Not Acceptable"

![image](https://user-images.githubusercontent.com/57162257/127344057-e9ae4424-807a-429c-b513-7fcbc0bcb50f.png)

![image](https://user-images.githubusercontent.com/57162257/127344315-0f27dbc6-d0b0-4b81-80af-b3f1a758344d.png)

사용자의 address를 뽑아오려고 하니까 406을 처음 받아봤다. 

검색결과 보통 jackson라이브러리가 추가되어있지 않아 발생한다고 한다. 하지만 jackson라이브러리는 이미 깔려있었고 확인해보니, 

반환 Dto인 AddressResDto에 @Getter가 없었다.

