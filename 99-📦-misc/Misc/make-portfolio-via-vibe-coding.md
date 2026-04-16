# 💪 바이브코딩으로 포트폴리오 만들기
- 작성일: 2026. 04. 15 (수)
---
# 사전 준비사항
특별히 사용하고 싶은 툴이 있는지?
- `manus` 를 사용할까해요.
- `Github` 계정 만들기
	- 프로젝트 코드를 안전하게 관리하고, 다양한 AI 를 활용해서 개선할 수 있습니다.
- `Git` 컴퓨터에 설치하기
- 내가 쓰는 AI 컴퓨터에 설치하기
- `Vercel` 계정 만들기

---
## 개발 환경설정하기
- `homebrew` 깔기
	- https://brew.sh/
```
/bin/bash -c "$(curl -fsSL https://raw.githubusercontent.com/Homebrew/install/HEAD/install.sh)"
```
- `git` 깔기
	- github 에 가입되어 있지 않다면, 가입합니다.
```shell
brew install gh
# 로그인 하기
ls

#
**?** **Where do you use GitHub?** GitHub.com

**?** **What is your preferred protocol for Git operations on this host?** HTTPS

**?** **Authenticate Git with your GitHub credentials?** Yes

**?** **How would you like to authenticate GitHub CLI?** Login with a web browser
```
- 내 컴퓨터에 Github 레포지토리 다운 받기
```shell
# 해당 경로로 이동합니다
cd {경로}
# 상위 경로로 이동합니다. 
cd ..

# github 레포지토리 다운받기
git clone https://github.com/new-pow/doing-sth.git
```




---
## 디자인 테마 정리하기
-  크래딧을 아끼는 방법.
- Notion이나 ppt 등에 무드 보드 정리
	- 스크린 샷이나 출력 활용하여 문서화.
- GPT 등 다른 AI를 사용해서 디자인 명세 문서 작성
- Cursor 에 문서 전달
	- 디자인 폴더를 별도로 만들면 좋음.
- 섬세한 작업은 Figma 나 Framer 를 사용해도 좋음
	- 만약 IT 기획을 할것이라면, Figma 사용법을 익히는 것도 매우 좋습니다.

# 프롬프트 작성하기
GPT의 도움을 받아서 작성해봅니다.
- 목적
- UI 요건
	- 디자인 명세 정한것과함께
- 구조
- 관리 방식
	- 노션 특정 데이터 베이스와 동기화를 하거나, .md 파일로 정리하는 것을 추천함.
	- 챗봇 기능을 넣는 것도 괜찮을듯. (사이트 문서를 기반으로 나에 대한 답변해줌)
	- 조회수 기능 등?
	- SEO랑 콘텐츠 관리를 할 것인지?
---
# 오늘의 키워드들
- 마크다운 `.md` 혹은 `markdown`
	- AI가 글을 쓸때, 마크다운으로 글을 쓰고 이해해요. 예를 들어 이런거
```
# 제목1
## 제목2
- 할 일
```

