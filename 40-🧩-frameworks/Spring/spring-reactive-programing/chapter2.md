---
tags: [framework, spring]
---

# 리액티브 스트림즈(Reactive Streams)
## 2.1 리액티브 스트림즈(Reactive Streams)란?
- 데이터 스트림을 Non-Blocking이면서 비동기적인 방식으로 처리하기 위한 리액티브 라이브러리의 표준 사양
- 구현체
	- RxJava, Reactor, Akka Streams, Java9 Flow API 등...
## 2.2 리액티브 스트림즈 구성요소
- Publisher
	- 데이터를 생성하고 통지(발행, 게시, 방출)하는 역할을 한다
- Subscriber
	- 구독한 Publisher로부터 통지(발행, 게시, 방출)된 데이터를 전달받아서 처리하는 역할
- Subscription
	- `Publisher`에 요청할 데이터의 개수를 지정하고, 데이터의 구독 취소하는 역할을 한다
- Processor
	- Publisher와 Subscriber의 기능을 모두 가지고 있다.
	- 
![](https://private-user-images.githubusercontent.com/103120173/411304853-50f7c01e-fe22-4dc4-a2de-1171f2dca140.jpeg?jwt=eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJnaXRodWIuY29tIiwiYXVkIjoicmF3LmdpdGh1YnVzZXJjb250ZW50LmNvbSIsImtleSI6ImtleTUiLCJleHAiOjE3MzkwOTczODYsIm5iZiI6MTczOTA5NzA4NiwicGF0aCI6Ii8xMDMxMjAxNzMvNDExMzA0ODUzLTUwZjdjMDFlLWZlMjItNGRjNC1hMmRlLTExNzFmMmRjYTE0MC5qcGVnP1gtQW16LUFsZ29yaXRobT1BV1M0LUhNQUMtU0hBMjU2JlgtQW16LUNyZWRlbnRpYWw9QUtJQVZDT0RZTFNBNTNQUUs0WkElMkYyMDI1MDIwOSUyRnVzLWVhc3QtMSUyRnMzJTJGYXdzNF9yZXF1ZXN0JlgtQW16LURhdGU9MjAyNTAyMDlUMTAzMTI2WiZYLUFtei1FeHBpcmVzPTMwMCZYLUFtei1TaWduYXR1cmU9NDg1MmVjYmQwN2UwMDQwMGFiNDVlOGE0YjZlMmNmN2RhMzdlMDhiNTQ4ZjY5NjMwODgyYjZiYjRlMjYwYzEzMiZYLUFtei1TaWduZWRIZWFkZXJzPWhvc3QifQ.C890Y1OBaO-Rkut3HRdGfN98CvXSAHC4t7vO79ZDFXo)

- Kafka에서 다루는 Publisher, Subscriber 와 개념이 다를 수 있다.
- 리액티브 스트림즈에서는 Publisher, Subscriber 에서는 Publisher가 subscribe 메서드의 파라미터인 Subscriber를 구독하는 형태로 구독이 이루어집니다.
- 코드 관련해서 참조 p.44

## 2.3 코드로 보는 리액티브 스트림즈 컴포넌트
- 용어와 개념을 정리할 겸 간단히 정리합니다.
- Publisher
	- subscibe(Subscriber s)
		- Subscriber 를 등록하는 역할
- Subscriber
	- onSubscribe(Subscription s)
		- 구독 시작 시점에 어떤 처리를 하는 역할
	- onNext
		- Publisher 가 통지한 데이터 처리
	- onError
		- Publisher가 데이터 통지를 위한 처리 중 에러 발생시 에러 처리하는 역할
	- onComplete
		- Publisher가 데이터 통지를 완료했음을 알릴 때 호출. 후처리.
- Subscription
	- request(long n)
		- 구독한 데이터 개수 요청
		- 데이터 개수가 왜 중요한지 아직 모르겠다...??
	- cancel()
		- 구독 해지
- 익명 인터페이스 특성을 잘 이용해서 Publisher와 Subsciber 간 데이터를 주고받을 수 있다.

## 2.4 리액티브 스트림즈 관련 용어 정의
- Signal
	- Publisher와 Subscriber 간에 주고받는 상호작용
- Demand
	- Demand는 바로 Subsciber가 Publisher에 요청하는 데이터.
	- 더 구체적으로는 아직 전달하지 않은 데이터.
- Emit
	- 데이터를 내보내다
- Upstream/Downstream
	- 데이터가 위로/아래로 흐르다
- Operator
	- 연산자
- Source
	- 최초 생성된 무언가. 원본.
	- 보통 Data Source 로 사용.
## 2.5 리액티브 스트림즈의 구현 규칙
- (생략)

## 2.6 리액티브 스트림즈 구현체
- 책에서는 Java 기반의 라이브러리에 대해서만 언급한 것 같음.
- RxJava
	- .NET 환경의 리액티브 확장 라이브러리를 넷플릭스에서 Java 언어로 포팅하여 만든 JVM 기반의 대표적인 리액티브 확장 라이브러리.
	- 포팅? 이 뭐지?
- Project Reactor
	- Spring Framework 팀에 의해 주도적으로 개발된 리액티브 스트림즈의 구현체
- Akka Streams
	- JVM상에서의 동시성과 분산 애플리케이션을 단순화해주는 오픈소스 툴킷
- Java Flow API
	- Java 9 부터 Flow API 를 사용하여 리액티브 스트림즈 지원
	- 리액티브 스트림즈의 표준 사양이 SPI (Service Provider Interface) 로서 Java API 에 정의되어 있음.
	- 사용자들이 리액티브 스트림즈와 관련된 API를 제공하기 위해서 하나의 인터페이스만 바라볼 수 있는 단일 창구로서의 역할을 기대하기 때문?
	- 혹은 리액티브 스트림즈를 구현한 여러 구현체들을 Flow API 로 변환하는 상호 운용성을 지언하기 위함?

---
- 사실 이 부분의 챕터는 크게 와닿지는 않아서, 다른 챕터를 더 읽어보고 다시 돌아오기로 합니다.