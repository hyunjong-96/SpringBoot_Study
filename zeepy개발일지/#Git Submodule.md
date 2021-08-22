# #Git Submodule



## 1. Git Submodule 이란?

- 하나의 저장소(repository)에서 존재하는 또 다른 별개의 저장소이다.
- 현재 프로젝트에서는 파이어베이스, sns로그인의 private키 등을 보다 안전하게 다루기 위해 사용한다.
- Submodule이 접근권한이 Private로 걸려있기때문에 허용된 개발자 이외의 사람들이 보호된 정보를 볼수 없기때문에 사용하기 용이하다고 생각한다.



## 2. Git Submodule 적용

```bash
git submodule add [자식저장소주소] [디렉토리_path]

git submodule add https://github.com/Zeepy... /src/main/resource...
```

- 위 명령어를 작성하게 되면 root디렉토리의 .`gitmodules` 에

  ```git
  [submodule "~submodule 저장 경로"]
  	path = [submodule 저장 경로]
  	url = [submodule repository url]
  ```

  와 같은 내용이 추가된것을 확인할수 있다.

- 이 시점에서는 `path` 에 작성되어있는 경로에 디렉터리가 생성되지만 그 안에는 아무것도 존재하지 않는다.
  그렇게 떄문에 submodule을 가져와야한다.

  ```bash
  git submodule updarte --init --recursive
  ```

- 그러면 해당 디렉터리에 submodule의 정보들을 가져온다. 그 디렉터리로 이동하면 submoduled의 커밋코드가 적혀있을꺼고, git status로 상태를 확인해보면 `detached Head` 라는 붉은색 상태가 나와있다.
  이는 **어떤 브랜치의 상태도 아니다.** 라는 뜻으로 커밋, 푸시를 해도 main module에서는 커밋한 submodule을 찾을수 없다.

- Detached Head상태를 해결해주기 위해

  ```bash
  git submodule foreach git checkout main
  ```

  명령어를 이용해 submodule을 main branch로 checkout해주어 현재 main project가 가지고 있는 submodule이 submoduled의 main branch를 보고있게 하여 사용해준다.

- 그런다음 사용해주면된다.



## 3. Submodule 수정 및 수정 적용

- 현재 내가 사용하고 있는 submodule에서 수정할 사항이 있어 수정을 한 후 submodule의 main branch 에 푸시를 하기위해서는 꼭 해야하는게 **`선 submodule푸시, 후 main project푸시`** 이다.
- submodule을 먼저 푸시하지않고 main project를 먼저 푸시하니까 변경된 submodule을 main project에서 읽지 못하거 이전 커밋내용을 보고있더라..



## 출저

https://pinedance.github.io/blog/2019/05/28/Git-Submodule

https://kyubot.tistory.com/129