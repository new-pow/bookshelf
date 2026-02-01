---
tags: [misc]
---

# 자원 생성
- HTTP 명세상 Get, Delete 메서드에는 요청 바디를 함께 요청할 수 없다.

# 인증과 인가
- 인증 : 누구인지 증명하는 일
- 인가 : 특별한 행위나 특정 자원에 대한 접근을 허가받는 것

## 토큰과 시크릿
- 토큰 token
	- 재생성하기 어려운 것
	- 어떤 것에 대한 접근을 가능하게 해주는 것
- 시크릿 secret
	- 토큰과 비슷하지만, 웹사이트에 직접 방문해서 확인할 수 있다.
	- 보통 API로 획득되지 않는다.

## OpenAPI의 인가 처리 방식
- `securitySchemes` 하위에 `securityScheme`을 추가하고 이름 지정
- 지정한 보안 이름을 security 필드 아래의 배열에 추가한다.
- 지원하는 인가(보안) 방식
    - apiKey
    - http
	    - Authorization 헤더를 통해 username과 password를 전달한다.
    - oauth2
	    - 인증 위임 OAuth 2.0 프로토콜
    - openIdConnect
	    - OAuth2.0에 탐색 자동화나 사용자 상세 정보를 획득할 수 있는 표준화된 엔드포인트 같은 기능을 추가한 확장판이다.
- 보안이 선택적으로 적용되게 하려면 security 항목에 비어있는 객체를 추가한다.
- 스코프 배열은 적용할 보안 스킴이 oauth2.0 방식일 때만 스코프를 배열 원소로 갖고, 그 외는 빈 배열이다.

![](https://private-user-images.githubusercontent.com/103120173/333670159-a6e45cca-de0d-4c77-b1e0-fc33bcd194d5.png?jwt=eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJnaXRodWIuY29tIiwiYXVkIjoicmF3LmdpdGh1YnVzZXJjb250ZW50LmNvbSIsImtleSI6ImtleTUiLCJleHAiOjE3MTY1Njk3NjYsIm5iZiI6MTcxNjU2OTQ2NiwicGF0aCI6Ii8xMDMxMjAxNzMvMzMzNjcwMTU5LWE2ZTQ1Y2NhLWRlMGQtNGM3Ny1iMWUwLWZjMzNiY2QxOTRkNS5wbmc_WC1BbXotQWxnb3JpdGhtPUFXUzQtSE1BQy1TSEEyNTYmWC1BbXotQ3JlZGVudGlhbD1BS0lBVkNPRFlMU0E1M1BRSzRaQSUyRjIwMjQwNTI0JTJGdXMtZWFzdC0xJTJGczMlMkZhd3M0X3JlcXVlc3QmWC1BbXotRGF0ZT0yMDI0MDUyNFQxNjUxMDZaJlgtQW16LUV4cGlyZXM9MzAwJlgtQW16LVNpZ25hdHVyZT1iNDVhZDBmNjkzYTQyZGQ1ZjYyNzY3ZGNlZjRlNmY0OGRiMjMzODY2YmZjNDk4ZTI0MWExZjZkYTI2MzdjNWY5JlgtQW16LVNpZ25lZEhlYWRlcnM9aG9zdCZhY3Rvcl9pZD0wJmtleV9pZD0wJnJlcG9faWQ9MCJ9.arAMsMlrPe1SKmbAupv07l2Yl6DdMIWj4RJZ7dpyItM)

- 왜 보안스킴을 사용하는가?
	- `Authorization` 헤더를 파라미터로 기술할 수도 있지만, 보안스킴을 사용하는 이유는 보안 목적임을 분명하게 드러내기 위해서이다.
	- 도구를 통한 자동화를 가능하게 한다.
	- 보안스킴은 `Oauth2.0`과 같은 보안 요구사항을 기술하는 데도 사용할 수 있다.

# OpenAPI 와 스웨거를 활용한 API 설계 우선 방식
- 

