# API와 OpenAPI 소개
## 1.1 API 생태계란?
- API는 각 서비스가 제공할 수 있는 것이 무엇이고, 다른 서비스와 어떻게 상호작용할 수 있는지를 정의한다.
## 1.2 API 기술하기
- 전체를 이해할 수 있는 큰 그림을 그리려면 개별 서비스가 어떻게 연결돼 있는지를 알 아야 한다.

## 1.6 REST 란?
- RESTREpresentational State Transter는 네트워크로 연결된 시스템(특히 서버/클라이언트 시스템) 설계 방법에 대한 아이디어 모음
- 실무 현장에서는 HTTP 기반 API를 설계할 때 요구사항과 표준 또는 REST 원칙 준수 사이에서 트레이드오프(rade-off를 고려한 현명한 결정을 내려야 한다.
- REST는 요청-응답 모델을 사용하며, 무언가를 처리하기 위해 필요한 모든 정보가 요청에 포함돼 있으므로 상태를 별도로 관리하지 않는 무상태 stateless 방식이다.
- REST의 핵심 아이디어 '자원'

# API 설계의 특수성
1. API는 다른 서비스를 개발하기 위한 중간 제품
    - API를 제품으로서 개발하는 것과 백엔드 개발과는 다르다.
2. API 변화는 그 API를 사용한 모든 서비스에 문제를 발생시킴
	- 함부로 바꿀 수 없다.

## API 설계 원칙
- 협업 : 다양한 이해관계자들의 충분한 합의 (다수가 동의한 것)
- 성과 : 가치, 도메인을 중심에 둘 것 (쓸모있는 것)
- 기술 : 기술 선택은 용도에 맞출 것 (기술 중심이 아닐 것)
- 약속 : API는 고객과의 약속. 큰 책임을 고려할 것(신중히 설계)
	- MVP 방식의 점진적 발전이 통하지 않는다.
	- 그래서 변화가 극히 적다.
- 문서 : 무엇보다 문서가 우선될 것
	- API의 실체는 문서이다.

## 문서 작성 흐름
### 개발 중심 흐름
1. 개발부터하고 그 결과를 문서로 생성함
2. 개발이 변경되면 자동 생성기가 문서를 변화시킴
3. springDoc을 비롯한 많은 자동 문서 생성기를 이용한다.
	- 잘못됨. 문서 -> 코드 해야지, 코드 -> 문서이면 안된다.

### API 설계 원칙 흐름
1. 5대 원칙에 근거하여 API 문서가 먼저 가성됨
2. 도메인이나 고객의 변화에 따라 API의 변화가 생겨남
3. 실제 개발되는 코드나 언어와 무관하게 문서가 운영됨

### 개발 중심 흐름 -> API 설계 원칙 흐름
- 개발자가 아닌 직군이 규격화된 API

## 문서 작성 기본 요령
### ADDR 프로세스에 따라
1. 도메인 분석을 통한 작업스토리 추출 및 디지털 기능 식별
2. 작업스토리별 액티비티 단계 캡쳐
3. 이벤트 스토밍의 네러티브를 이용한 API 경계식별(애그리거트 분리)
4. 식별된 바운디드 컨텍스트 내의 구체적인 개별 API 정의
5. REST 구조에 맞는 실제 설계
- 참고 : 웹 API의 설계 원칙

### 참고할 수 있는 기법
- DDD의 여러 기법 동원
- 유저스토리매핑, 작업스토리, 스토리스토밍, 유즈케이스, 예제 매핑 등
	- 이벤트 스토밍 - 유저 네러티브를 중심으로 찾아낸다. 내러티브에 특화되어 있다. 너무 많은 내러티브를 가져오면 어렵다. 사회자가 주요 내러티브를 잘 정하느냐에 따라 성공 유무가 갈린다. 도메인 전문가가 있을 때 유리하다
	- 도메인 스토리텔링 - 참가자 간의 협력구조를 중심으로 도메인 탐색. 전체 도메인을 꿰뚫고 있는 전문가가 없어도 찾아나가는 과정을 겪는다
	- 유저 스토리 매핑 - 사용자 간 큰 활동의 흐름을 찾고 하위 단계 탐색. 시작은 유저에서 시작해서 유저의 중심 활동에서 하위 활동들을 찾아내고 어떠한 순서대로 하는지 기술함
	- 예제 매핑 - 구체적인 사례를 전개해가면서 하위 단계 탐색. 퍼소나 분석이랑 비슷함
	- 스토리 스토밍 - 사용자 간 상호작용과 그 산출물 분석
	- 유즈 케이스 - 다양한 내부 모델링 툴을 이용해 다각도로 도메인을 분석
- 실제 도메인을 파악하고 시장성 있는 기능이 무엇인지 분석하는 과정

# OPEN API
- YAML (나무위키 설명 추천)
- 3.1까지 버전 존재
	- JSON 을 쓸수있는가 차이점
- 하나의 문서로 많은 API를 기술할 수 있다

# 요청의 개념
- 요청은 사용자의 니즈를 나타낸다.
- 사용자의 니즈는 사용자가 받고 싶은 혜택을 표현함
- 니즈와 혜택을 일치시키는 과정이 요청의 정의

## 요청의 특성
- 사용자가 얻게 될 혜택을 추상화하여 포괄적으로 정의할 수록 요청 시의 변수가 많아짐

## 요청, 응답의 정의
- 요청 정의시 유의점
	- 메서드 적용을 엄격하게 할 것인가
	- 멱등성의 범위를 어떻게 정의할 것인가
	- 요청 변수는 공통 포멧을 적용할 것인가
	- 인증, 인가, 상태를 어떻게 처리할 것인가
- 응답 정의시 유의점
	- 모든 응답의 공통 포맷을 적용할 것인가
	- 응답의 상태별 공통 포멧을 적용할 것인가
	- 정상응답의 페이로드는 공통 포멧을 적용할 것인가
	- 응답코드의 공통 기준을 적용할 것인가
	- 하이퍼미디어를 적용할 것인가
		- 동적 endpoint
- 

---
# 생각한 것
- 개발자가 문서를 작성하는 것이 맞는가? 기획자가 작성해야 하지 않을까?
- 