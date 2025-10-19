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

---
# 시뮬레이션 응용문제
## 단일창구 대기행렬 문제
- 사전 변수
	- 주유대: 1대
	- 도착상황: 평균 15명/h
		- 일양확률변수 (=15/60)
	- 봉사시간: 평균 4분, 포아송 확률변수
	- 출발상황: tpump=0 & queue != 0?
	- 목적: 평균 대기행렬(Queue)의 길이는?
- 포아송 확률변수 발생 프로그램
	- 포아송 확률변수 발생공식:  ![](https://i.imgur.com/jaFbmSK.png)
```c
void poissn(long *np, float mean, int *pp)
{
	float prod, b, u;
	*pp = 0; 
	b = exp(-mean);
	prod = 1;
	random(np, &u);
	prod = prod*u; // 
	while (prod >= b)
	{
		random(np, &u);
		prod = prod*u;
	}
}
```

- 고객 도착사건 알고리즘
```c
call random(U)

if U<0.25 * 1 then
	arrive = 1 // 도착한것으로 한다.
	queue = queue + arrive // 도착행렬 길이
	totarr = totarr + 1 // 총 고객수 계산
	
// 고객이 출발했을때, queue에 고객이 있다면
if tpump = 0 and queue !=0 then
{
	queu = queue -1 // 손님 데려오기
	callpoissn(seed, mean, p) // 봉사시간 return p
	tpump = p // 봉사시간
}
```

| TIME | ARR | QUE | TPUMP |
| ---- | --- | --- | ----- |
| 8    | 0   | 1   | 1     |
| 9    | 1   | 1   | 0     |
- 전체 알고리즘
```c
read tstep, prarr, seed, mean
queue = 0 // 대기행렬길이
totque = 0 // 총대기시간
totarr = 0 // 도착한 총고객 수
tpump = 0 // 봉사시간
time = 0
tlimit = 100 // 시뮬레이션 시간
```
```c
while time < tlimit do
  {
	  time = time-tstep
      arrive = 0
      call random(seed, U)

      if U<prarr*tstep then // prarr 는 고객의 도착확률
      { 
	      arrive = 1
          queue = queue+arrive
          totarr=totarr+1  // 총 고객수 + 1
	   }

       if tpump>0 then
	   {
		  tpump = tpump-tstep
          if tpump < 0 then 
		  tpump = 0
	   }

	   if tpump=0 and queue≠0 then
	   { 
		  queue = queue-1
		  call poissn(seed, mean, p)
		  tpump = p    
	   }

       totque = totque+queue // 총 대기시간
   }(* end of while *)

    aveque = totque/(tlimit/tstep) // 평균 대기행렬길이
    print aveque, totarr
    stop
```

- 결과 분석

| TIME | ARRIVAL | QUEUE | TPUMP | 비고                     |
| ---- | ------- | ----- | ----- | ---------------------- |
| 0    | 0       | 0     | 0     |                        |
| 1    | 1       | 0     | 8     | 첫번째 고객 봉사시간 8          |
| 2    | 0       | 0     | 7     |                        |
| 3    | 0       | 0     | 6     |                        |
| 4    | 1       | 1     | 5     | totque = 1, totarr = 2 |
| 5    | 0       | 1     | 4     | totque = 2, totarr = 2 |
| 6    | 0       | 1     | 3     | totque = 3, totarr = 2 |
| 7    | 0       | 1     | 2     | totque = 4, totarr = 2 |
| 8    | 0       | 1     | 1     | totque = 5, totarr = 2 |
| 9    | 1       | 1     | 4     | totque = 6, totarr = 3 |
| 10   | 0       | 1     | 3     | totque = 7, totarr = 3 |
| 11   | 0       | 1     | 2     | totque = 8, totarr = 3 |
- totque 를 총 고객수로 나누면, 평균대기시간
- totque 를 총 시뮬레이션 시간으로 나누면, 평균대기행렬길이

- 확률변수는 어떻게 결정되는가? -> 14강에서 계속
	- 확률적 상황으로 표현
	- 실시스템에서 데이터 수집
	- 확률분포를 가정, 검정

---
# 모델링과 시뮬레이션
- 시스템이란 어느 목적을 위하여 하나 이상 서로 관련있는 구성요소가 결합된 것이다.
- **모델**이란 **시스템을 서술한 것**으로 축소된 물리적 대상이거나 수학적인 식이나 관계, 도형적 표현일 수 있다.
![](https://i.imgur.com/yGSdSr8.png)

- 시뮬레이션 모델의 이용 범주
	- 설명적 장치: 시스템이나 문제를 정의
	- 분석도구: 한계적 구성요소를 결정
	- 설계평가도구: 제안된 해결방안을 종합하고 평가함
	- 예측도구: 미래의 개발계획을 예측
- 시스템의 범위 결정 문제
- 외적요인들
	- 포함하거나
	- 무시하거나
	- 입력변수로 한다.
## 모델의 설계
- 용이한 경우
	- 물리적 규칙이 이용 가능하다.
	- 도형적 표현이 가능하다.
	- 입력, 출력, 구성요소의 변화가 통제 가능하다.
- 어려운 경우
	- 기본 규칙이 없다.
	- 표현하기 어려운 많은 절차적 요소
	- 랜덤(random) 구성 요소
	- 정량화가 어려운 정책적인 입력
	- 인간의 의사결정이 큰 영향을 주는 경우
### 모델 설계 과정 (모델링)
- 구성요소들간의 관계 표현
	- 모델작성 목적 수립
	- 시스템 영역 확정
	- 모델 상세성 확정
	- 평가척도 및 재설계
![](https://i.imgur.com/Ub29mfg.png)
- 시뮬레이션 모델의 종류
	- 결정적 모델과 확률적 모델
	- 정적 모델과 동적 모델
	- 이산 모델과 연속 모델
	- 물리적 모델과 수리적 모델
- 검증
	- 시뮬레이션 모델이 표현한 것이 정학하게 컴퓨터 프로그램으로 옮겨지고 제대로 수행되는지 확인하는 것이다.
- 타당성
	- 시뮬레이션 모델과 실시스템 간의 관계가 일치하는 여부 검증하는 것이다.
- 예시
	- 몬테칼로 시뮬레이션
## 시뮬레이션 특징
- 장점
	- 실제 구축없이 예측 평가 가능
	- 해석적 방법이 불가능한 경우 실험적 대안
		- 실시스템은 매우 복잡하여 공식화 하기 어려움
	- 비용이 높고 위험한 경우
	- 다양한 환경 설정과 반복실험 가능
	- 장(경제시스템), 단시간 시스템 연구가 가능
- 단점
	- 
