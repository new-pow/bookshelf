# Chapter 16. HTTP 서버를 약한 결합 구성으로 변경하기
## 들어가기 전에

- 해당 챕터에서는
	- 실용적인 Server 구조체를 정의한다.
	- Beyond the Twelve-Factor App 관점을 적용한다.
### 참고 : Beyond the Twelve-Factor App 이란?
- 케빈 호프만의 무료 전자책 '클라우드 앱의 12가지 요소를 넘어서'를 뜻합니다.
- 무료 전자책 [링크](https://www.google.com/search?q=Beyond+the+Twelve-Factor+App&oq=Beyond+the+Twelve-Factor+App&gs_lcrp=EgZjaHJvbWUqBggAEEUYOzIGCAAQRRg7MgYIARAAGB4yBggCEAAYHjIGCAMQRRg8MgYIBBBFGDzSAQc1MTlqMGo3qAIAsAIA&sourceid=chrome&ie=UTF-8#:~:text=Beyond%20the%20Twelve%2DFactor%20App%20%2D%20GitHub,raw.githubusercontent.com%20%E2%80%BA%20books%20%E2%80%BA%20master)
- 

## 060 환경 변수로부터 설정 불러오기
- 변경할 내용
	- 변경 전 : 어플리케이션 실행시 포트 번호를 인수로 받는다.
	- 변경 후 : 환경 변수로부터 각 정보를 읽어오는 패키지를 추가하여 환경변수를 사용한다.
- Beyond the Twelve-Factor App 5. Configuration, Credentials, and Code
	- 구성은 배포에 따라 달라질 수 있는 모든 값을 의미합니다. (예 : DB 연결 정보, AWS와 같은 자격 증명 등)