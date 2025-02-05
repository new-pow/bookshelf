# 추천 시스템 실습을 위한 리서치
## [강의] Recommender Systems and Deep Learning in Python
- https://www.udemy.com/course/the-ultimate-beginners-guide-to-python-recommender-systems/?couponCode=ST5MT020225BROW
- 

## [유튜브 영상] 추천 시스템의 원리와 구축 사례
- https://www.youtube.com/watch?v=MTAc8-ygAaM
-  배움
	- 기본적인 추천 시스템에 대한 이해가 가능했음.
	- 아마존에서 제공하는 솔루션을 확인할 수 있음.
- 추천 알고리즘의 종류
	- Hybrid
		- Collaborative + Content-based
	- Collaborative Filtering 협업 필터링
		- User-based filtering
		- Item-based filtering
		- 장점
			- content 를 분석할 필요는 없음
			- 사용자 행동 로그만으로 추천 계산가능
			- 다양한 곳에 적용 가능
		- 단점
			- cold-start 문제 발생
	- Content-based Filtering
		- 장점
			- content 분석만으로 추천 가능
			- 사용자 행동 로그 없이 추천 계산 가능
			- cold start 문제 완화
		- 단점
			- content가 풍부한 곳에 적용 간으
			- 적용 범위가 비교적 제한적 (music, movies 등은 적용이 쉽지 않음)
	- 추천 적용 방식
		- 개인화 Personalization
		- 비개인화 Non-Personalization
![](https://i.imgur.com/9WF4nGL.png)
![](https://i.imgur.com/m8JxBkY.png)
- 클라우드를 이용하는 방법
	- 기계 학습을 위해 **Amazon Personalize** 사용 가능
		- 사용자의 인터액션을 기준으로 학습
		- 세가지 데이터가 필요
			- User 정보
			- Items 메타 데이터
			- (User-Item) Interaction 사용자의 Item에 대한 행동 로그 데이터
![](https://i.imgur.com/i5LXtqn.png)
			- 사용할 수 있는 레시피
				- User personalization 개인화
				- SIMS 비슷한 상품
				- Personalized-rank 개인화된 랭킹
- 좋은 추천이란?
	- coverage
	- relevance
		- 추천하는 상품이 사용자의 선호도나, 상품 정확도
	- serendipity
		- `Surprise`
- 실시간으로 구축하는 방식
![](https://i.imgur.com/ag8CMv3.png)
![](https://i.imgur.com/a0wgn1P.png)
- 배치하여 구축하는 방식
![](https://i.imgur.com/DG7231V.png)



## [유튜브 영상] 현대적인 추천 시스템 구축을 위한 여정
- https://www.youtube.com/watch?v=69igsWcmW5g
- 목적 : 실제 서비스에서 어떻게 쓰이는지 학습한다
- 배움
	- Pinterest 모델이 추천 모델에 굉장히 선진적이다.
	- Go, Goroutine 을 활용하면 좋음. 추천 언어
	- 변천사를 보기에는 좋지만, 너무 깊은 수준에 대한 이야기가 많음
- **Contents Based Filtering Recommender**
	- 라이너의 첫번째 추천 기술
- Label Smoothing 방식
- Pixie 방식
	- 데이터가 좋으면 효율성이 굉장히 좋음
- 
![](https://i.imgur.com/7DstDLp.png)

- Outcome
	- 
- 추가적인 개선: 추천 시스템을 구축하자
![](https://i.imgur.com/Q2pKKyY.png)
- 필터링 : 이미 추천된 것은 다시 추천된건 filter out
	- bloom filter 적용
- 

## [유튜브 영상] 실시간 추천 시스템 구축하기
- 실제로 어떤 유저 행동 로그 스키마를 사용했는가 참고할 수 있음
- 실제 서비스에서 어떻게 쓰이는지 학습한다
- **Session-based Recommender**
- 배움 고유
	- 외부 Reference & Cloud Service 적극 활용
	- 트레이드 오프를 고려하여 제품에 맞는 전략적인 선택
- 기존구조
- [실시간 추천 시스템 구축하기 | 인프콘2023](https://www.youtube.com/watch?v=C507r5p1WvE)
	- 라이너의 예시, 실시간 추천 시스템
		- session-based recommender
			- 사용자의 한 세션내에서 다음에 보여줄 컨텐츠를 추천해줌
- 데이터 정의
	- 유저 행동 로그 스키마 설계
	- ![](https://i.imgur.com/xPRftbK.png)
- 데이터 처리
	- 실시간 스트리밍 파이프라인 구성
		- batch-processing : 컴퓨터 리소스 적으로 더 효율적
		- stream processing : 데이터가 발생하때마다 데이터를 처리해주기 때문에 실시간성이 강함
	- Apache Beam 파이프라인
![](https://i.imgur.com/WZDcpVB.png)

- 데이터 활용
	- Online Feature Store 구축
![](https://i.imgur.com/Q8xd6Dg.png)
- medleware `FeatureRead API` 를 구축
- 