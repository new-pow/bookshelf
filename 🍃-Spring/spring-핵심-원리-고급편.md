# 스프링 핵심 원리 고급편
## 개요
- [link](https://www.inflearn.com/course/%EC%8A%A4%ED%94%84%EB%A7%81-%ED%95%B5%EC%8B%AC-%EC%9B%90%EB%A6%AC-%EA%B3%A0%EA%B8%89%ED%8E%B8/dashboard)
- 기간: 2026. 01. 13 ~
- 목표
	- 반드시 한 번은 정복해야 한다.
	- 스프링에 대한 이해를 높이기

# 새로 알게 된 것
## 2026.01.13
- `@RestController`는 @Controller + @ResponseBody의 조합으로, 메서드의 반환값을 HTTP 응답 본문에 직접 작성. @Controller 어노테이션을 쓸 경우 반환값에 대한 뷰페이지를 찾게된다.
- zsh 와일드카드 오류가 있었음
	- 오류 발생 
		- ```
		  	  curl localhost:8080/v0/request?itemId=hello
		  	  # zsh: no matches found: localhost:8080/v0/request?itemId=hello
		  ```
	  - 해결: URL을 따옴표로 감싸면 됨.
		  - ```
			    curl "localhost:8080/v0/request?itemId=hello"
			```
	- 원인: zsh가 ?를 와일드카드 패턴으로 해석
