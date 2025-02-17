# 추천 시스템 실습을 위한 리서치

- 추천 알고리즘에 대한 학습과 추천 시스템 설계-구축에 대한 서치는 별개로 진행되어야 할 것 같습니다.
## 추천 시스템 구축을 위한 서치
### 추천 서빙 시스템 아키텍처: 높은 생산성을 위한 아키텍쳐 및 ML Flywheel | 2024 당근 테크 밋업
- https://youtu.be/Cs09fzdJo5Y?si=lapprFYxlCCazW8F
- 당근의 추천 시스템 아키텍처 예제를 엿볼 수 있음.
- 어떤 후처리를 할지, 어떤 유저에게 추천할지 등을 참고할 수 있음.
![](https://i.imgur.com/apIZhEv.png)
![](https://i.imgur.com/Fe69V6r.png)

### 후처리의 모든 것 (서빙전 필터링)
- https://eda-ai-lab.tistory.com/612
- 


## 추천 알고리즘 학습에 대한 서치
### [Coursera 강의] Nearest Neighbor Collaborative Filtering
- [https://www.coursera.org/learn/collaborative-filtering?specialization=recommender-systems](https://www.coursera.org/learn/collaborative-filtering?specialization=recommender-systems)
- 구독제 유료 강의이지만 7일 무료로 이용 가능. 그 이후로는 $49 가 소요됨.
- 알고리즘에 대한 설명이 다른 강의보다 깊음. (실용적이지는 않을 수도....)
- 실습의 수준
	- 사용자-사용자 연관성 행렬 만들기
	- 이웃 사용자 다섯명의 평점들을 통해 사용자-사용자의 모든 영화들에 대해 예상평점을 구하기
	-  사용자에 대한 예상평점을 계산하고 상위 3개의 영화 구하기 

### [Coursera 강의] 추천시스템 소개: 비개인화 및 콘텐츠 기반
- 구독제 유료 강의이지만 7일 무료로 이용 가능. 그 이후로는 $49 가 소요됨.
- 강의는 2주 분량
- 실습의 수준
	- 평균 평점 기준 상위 영화
	- 개수별 인기 영화
	- 좋아요 비율별 인기 영화
	- 토이 스토리와의 연관성
	- 토이 스토리와의 상관관계
	- 남성/여성 평균 평점 차이
	- '좋아요'의 남성과 여성 차이

### [유튜브] ⭐️ 추천시스템 설계 2024 국민대학교
- 30~60분 영상 24편
- https://www.youtube.com/watch?v=6yUo4uFNaVY&list=PLfHiwT6Xug9NGd-QgBwPLHrsTk7t40naB&index=2 (실습파트)
- **대학강의라 프로젝트에 완전 적용하기는 좀 힘들수도 있음**. python 프로젝트에 대한 학습과 병행해야함.
- 알고리즘에 대한 설명과 그 실습을 함께 할 수 있음.
- 실습 난이도가 높지 않은듯
- 실습 할 수 있는 것
	- finding similar Items
	- Rating Prediction
	- Implicit Feedback
	- User-free Model

## [Inflean] ⭐️ Python을 이용한 개인화 추천시스템
![](https://image.yes24.com/goods/110328538/XL)
- python을 이용한 개인화 추천시스템 도서 기반의 인프런 강의
	- https://www.yes24.com/Product/Goods/92150193
- 목차로 보았을 때 실습 비율이 그렇게 많은것같지는 않음.
- 한국어로 되어있고, 질의응답이 가능하다는 장점이 있음.
- 알고리즘 중심

## [Udemy] 머신 러닝 & AI로 추천 시스템 구축하기
- https://www.udemy.com/course/best-recommender-system/
- 상세하게 알려주어서 시간적으로 여유있을 때 보면 좋을 것 같음.
- 설명이 쉬움.
- 실습해보는 것이 많아 좋아보임
- 아파치 스파크, AWS 까지 시스템 구축을 위한 다른 툴을 확인할 수 있을 거같음.
- *전부 들어본 것이 아니라 그 외의 부분에 대해서는 알 수 없음*
![](https://i.imgur.com/l3MnnLf.png)


## [블로그] 추천 시스템 구현하기: 협업 필터링 알고리즘
- https://www.jaenung.net/tree/470
- 간단한 연습코드 따라해보기 좋았음.

## [유튜브 영상] 유튜브 알고리즘으로 보는 딥러닝 기반 추천시스템
- 20분 영상
- https://youtu.be/uBWuUII__U0?si=t5C6v6gn23z_efP9
- 유튜브에서 실제로 추천 시스템이 어떻게 구성되어있는지 

## [유튜브 영상] 추천 시스템의 원리와 구축 사례
- 1시간 영상
- AWS personalizer 소개 영상에 가까웠음.
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

- 일회성 영상
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
- 일회성 영상
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

---
### 최규민님의 추천 시스템 발표자료들
- 실질적으로 초보자를 위한 온보딩이라기보다는 추천 시스템에 대한 시각을 익히고 넓히는 데에 도움이 많이 된 발표 자료들.
#### [PDF] 2021 개인화 추천은 어디로 가고 있는가?
- https://www.slideshare.net/slideshow/ss-246630743/246630743#9
- 추천 서비스의 목적을 상기하는데 도움이 되었음.

#### [PDF] 2019 추천시스템 이제는 돈이 되어야 한다.
- https://www.slideshare.net/slideshow/ss-164511610/164511610
- 의사결정의 과정을 참고하기에 좋음.
- 어떤 알고리즘을 얼만큼 사용하는가...

##### [PDF] 2018 눈으로 듣는 음악 추천 시스템
- https://www.slideshare.net/slideshow/ss-113740836/113740836
- Matrix Factorization
- CF + CBF
- ![](https://i.imgur.com/UlZIS83.png)



### [PDF] 2014 Live Brodcasting 추천시스템 발표 자료
- https://www.slideshare.net/slideshow/deview2014-live-broadcasting/39688044
- 클래식한 추천시스템 (cosign 기반)을 참고하기 좋음.
- 이해하기 편한 장표

