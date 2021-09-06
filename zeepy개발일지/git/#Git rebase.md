# #Git rebase



## 1. git rebase 란?

- 브랜치 병합 전략중 하나(merge, rebase)
- `merge`는 마지막 두개의 커밋(feature, main) 과 공통 조상(rebase) 총 3개의 커밋을 이용해 뱡합하는 `3-way merge`를 이용해서 새로운 커밋을 만드는 것.
- merge에서 conflict가 발생하는 경우는 rebase 에서 파생된 feature과 main 두 브랜치에서 모두 변경되었을 경우 발생한다.
  만약 둘 중 하나의 브랜치에서 변경점이 발생했다면 병합을 했을 경우 conflict가 발생하지않고 변경된 것이 적용된다.
- 그렇다면 `rebase`는 무엇일까?

<img width="761" alt="스크린샷 2021-09-04 오후 9 47 57" src="https://user-images.githubusercontent.com/57162257/132095063-b210b441-1025-401c-80bb-615e2f1228eb.png">

<img width="757" alt="스크린샷 2021-09-04 오후 9 49 01" src="https://user-images.githubusercontent.com/57162257/132095076-e1ab0aa0-1b35-49dc-acb2-0823f965966a.png">

- 위의 두 그림을 보면 feature과 master를 병합하여 새로운 브랜치를 만드는 것이 아닌 feature의 base를 master로 **재설정(rebase)**하는 것이다.

## 2. 적용하기

```bash
1. git checkout feature
2. git rebase master
3. git checkout master
4. git merge feature
```

1. `git checkout feature`

   rebase하고 싶은 브랜치로 head를 이동
   <img width="758" alt="스크린샷 2021-09-04 오후 9 50 42" src="https://user-images.githubusercontent.com/57162257/132095080-21efbf85-d019-4c2f-9a26-ef80c9bd9fff.png">

2. `git rebase master`

   master와 feature의 공통조상이 되는 base커밋부터 현재 브랜치까지의 변경사항 (`▵1`, `▵2`)을 구해서 patch로 저장해둔다.
   <img width="756" alt="스크린샷 2021-09-04 오후 9 50 56" src="https://user-images.githubusercontent.com/57162257/132095082-155e71a9-1957-4b8d-be94-1587035c79c0.png">

3. head를 master브랜치로 변경
   <img width="743" alt="스크린샷 2021-09-04 오후 9 51 05" src="https://user-images.githubusercontent.com/57162257/132095086-7162d509-e3e6-46bb-b673-ebad847abf42.png">

4. feature에서 변경된 점을 새로운 커밋 f1` 으로 생성한다.
   <img width="749" alt="스크린샷 2021-09-04 오후 9 51 18" src="https://user-images.githubusercontent.com/57162257/132095400-0980bea8-76ea-4663-93af-f7e106d8aed5.png">

5. 이하동문
   <img width="755" alt="스크린샷 2021-09-04 오후 9 51 36" src="https://user-images.githubusercontent.com/57162257/132095401-10db93a0-10b7-4e21-8a7b-19ecaf17e1aa.png">

6. feature가 f2` 를 가리킨다.
   <img width="755" alt="스크린샷 2021-09-04 오후 9 51 46" src="https://user-images.githubusercontent.com/57162257/132095406-dd71085c-be3f-4e85-a1ef-f4eeb0e13df1.png">

7.  

   ```bash
   git checkout master
   git merge feature
   ```

   master브랜치로 와서 master가 가리키고있는 m2커밋에서 f2` 로 이동시킨다.
   <img width="758" alt="스크린샷 2021-09-04 오후 9 51 58" src="https://user-images.githubusercontent.com/57162257/132095408-0566e514-16cc-4183-a7c7-b25e7c474af4.png">



### *삽질

**`git rebase master` 명령어를 실행하고나서 rebase에서 conflict가 발생했을때 rebase용 브랜치가 생성되었다.**

**conflict를 해결하고 난 뒤에 `git add .`후 `git rebase --continue` 를 해주면 계속해서 conflict를 해결할수 있고 모든 conflict를 해결하면 feature브랜치로 rebase가 되어있는 상태로 돌아오게된다.**

그리고 난 뒤에 꼭 master브랜치를 feature과 merge를 해주어야 한다!

## 3. merge vs rebase

merge와 rebase의 가장 큰 차이점은 **프로젝트의 히스토리**라고 생각한다.

merge로 병합을 했을 경우 커밋 내역에 merge commit이 추가로 적용되어진다.(어느 부분에서 병합되었는지 한눈에 볼수 있다)

rebase로 병합을 했을 경우 커밋 내역이 추가로 적용되지 않고 프로젝트 내용이 자연스럽게 하나의 흐름으로 이어져 적용된다.(git flow를 보기 쉬워진다.)



merge를 사용하던지 rebase를 사용하던지는 개바개 이지만 알아두어야 할점은, local에서 작업할때는 rebase를 하는것은 상관없지만, 이미 원격으로 push되어 있는 상태에서는 rebase를 하지 않는것이 원칙이라고 한다.



https://velog.io/@godori/Git-Rebase

https://backlog.com/git-tutorial/kr/stepup/stepup2_8.html

https://git-scm.com/book/ko/v2/Git-%EB%B8%8C%EB%9E%9C%EC%B9%98-Rebase-%ED%95%98%EA%B8%B0