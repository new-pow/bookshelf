# 시뮬레이션
#방송통신대 #simulation 

## 시뮬레이션 개념
- 실시스템 Real System에서 실시스템의 성질을 나타내는 모델을 만들고 그 모델로 실험을 하는 것
## 시뮬레이션 기본요소
- 실시스템
- 시뮬레이터
- 모델
![](https://i.imgur.com/Il9rue5.png)
- 목적
	- 실제 시스템이 갖는 행위를 예측/평가/분석/훈련

## 시뮬레이션의 구분
- 확률변수 (random variable)의 사용 여부
	- 결정적 시뮬레이션 (deterministic simulation)
	- 확률적 시뮬레이션 (probabilistic simulation)

---
# 결정적 시뮬레이션
- 예시
	- 저축문제
	- 공의 탄성 문제
- 특징
	- 미래를 예측한다.
	- 시간변수를 사용한다.
	- 다양한 실험이 가능하다.
---
# 확률적 시뮬레이션
- 확률적 시뮬레이션
	- probabilistic simulation
	- stochastic simulation
- 확률 변수 random variable 사용
## 확률적 상황
- 실제 상황은 가변적이다. 따라서 확률 변수로 가정
- 확률변수 분포
	- 일양분포
		- ![](https://i.imgur.com/wBj5WPC.png)
	- 정규분포
		- ![](https://i.imgur.com/pK0hc6Z.png)
	- 지수분포
		- ![](https://i.imgur.com/5QWA312.png)
		- ![](https://i.imgur.com/lESRPB7.png)
## 난수
### `[0,1)`난수 발생
- 0은 발생 가능, 1은 발생하지 않음.
- 일양확률변수
	- 발생확률이 같다.
- 예측할 수 없다.
- 난수 U[0,1) 발생
	- 다양한 확률변수 발생공식 적용 가능
- 난수 발생 알고리즘
	- ![](https://i.imgur.com/N1r7S93.png)
	- 