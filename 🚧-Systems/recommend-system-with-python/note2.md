# 협업 필터링 추천 시스템
- 어떤 아이템에 대해 비슷한 취향을 가진 사람들은 다른 아이템또한 비슷한 취향을 가질 것이다.
## 협업 필터링 Collaborative Filtering : CF
- 가정
	- 취향이 비슷한 사람들의 집단이 존재함을 가정

## 유사도 지표
- CF 에서는 사용자간 유사도를 구하는 것이 매우 핵심

### 상관계수
- 무슨 이야기인지 모르겠다... ㅜㅜ 수식은 일단 버리고간다
![{\displaystyle {\text{피 어 슨 상 관 계 수 }}={{\text{공 분 산 }} \over {{\text{표 준 편 차 }}\cdot {\text{표 준 편 차 }}}}}](https://wikimedia.org/api/rest_v1/media/math/render/svg/bea8f7bc27ce7e5ec0758f63620d11e628153b44)

rXY=∑in(Xi−X¯)(Yi−Y¯)n−1∑in(Xi−X¯)2n−1∑in(Yi−Y¯)2n−1![{\displaystyle r_{XY}={{{\sum _{i}^{n}\left(X_{i}-{\overline {X}}\right)\left(Y_{i}-{\overline {Y}}\right)} \over {n-1}} \over {{\sqrt {{\sum _{i}^{n}\left(X_{i}-{\overline {X}}\right)^{2}} \over {n-1}}}{\sqrt {{\sum _{i}^{n}\left(Y_{i}-{\overline {Y}}\right)^{2}} \over {n-1}}}}}}](https://wikimedia.org/api/rest_v1/media/math/render/svg/135b1f3a3a7a31a050f8b7f9325e5b14db99e37a)

따라서

rXY=∑in(Xi−X¯)(Yi−Y¯)∑in(Xi−X¯)2∑in(Yi−Y¯)2![{\displaystyle r_{XY}={{\sum _{i}^{n}\left(X_{i}-{\overline {X}}\right)\left(Y_{i}-{\overline {Y}}\right)} \over {{\sqrt {\sum _{i}^{n}\left(X_{i}-{\overline {X}}\right)^{2}}}{\sqrt {\sum _{i}^{n}\left(Y_{i}-{\overline {Y}}\right)^{2}}}}}}](https://wikimedia.org/api/rest_v1/media/math/render/svg/ee1e03b44aabd2904cca430279faad515c617891)

- -1~1 사이의 값을 가진다.
	- 0 유사도가 거의 없음
### 코사인 유사도
- 협업 필터링에서는 코사인 유사도를 사용한다.
![{\displaystyle {\text{similarity}}=\cos(\theta )={A\cdot B \over \|A\|\|B\|}={\frac {\sum \limits _{i=1}^{n}{A_{i}\times B_{i}}}{{\sqrt {\sum \limits _{i=1}^{n}{(A_{i})^{2}}}}\times {\sqrt {\sum \limits _{i=1}^{n}{(B_{i})^{2}}}}}}}](https://wikimedia.org/api/rest_v1/media/math/render/svg/2a8c50526e2cc7aa837477be87eff1ea703f9dec)
- 각 아이템을 하나의 차원으로 사용
	- 사용자의 평가값은 좌표값
- 두 사용자간의 평가값 유사할 수록 코사인 값은 커지고, theta 값은 작아짐.
- -1~1 사이의 값
- 이해하기 좋았던 [Link](https://wikidocs.net/24603)

### 자카드 계수
- 타니모토 계수의 변형
- 교집합 /  합집합
- 이진수 데이터라면 매우 좋은 결과 (1, 0 만 있다면)

## 기본 CF 알고리즘
- 모든 사용자 간 평가의 유사도 계산
- 추천 대상과 다른 사용자간 유사도 추출
- 추천 대상이 평가하지 않은 아이템에 대한 예상 평가값 계산
	- 평가값 = 다른 사용자 평가 * 다른 사용자와의 유사도
- 아이템 중 예상 평가값이 가장 높은 N개 추천

---
- pandas `set_index` 에 관한 사용법 -> https://wikidocs.net/154819
## 이웃을 고려한 CF
단순 CF 알고리즘 개선 방법에서 개선
- K Nearest Neighbors KNN 방법
- Thresholding 방법

## 사용자의 평가 성향을 고려한 CF
- 같은 평점이지만 평점 평균에 따라 다른 의미를 가질 수 있다
- **집단과 사용자간 평점 평균 차이로 보정해줄 수 있다.**

## 그 외 CF 정확도 개선 방법
- 신뢰도 가중 방법
	- 사용자간 유사도가 같더라도, 공통 평가 아이템이 더 많은 쪽이 신뢰도가 높다.
	- 그러나 직접적으로 공통 평가 아이템을 직접 사용하기보다는, 공통 평가 아이템이 최소 일정 수 이상인 경우에만 유사한 사용자군으로 간주할 수 있다.

---
## 사용자 기반 CF 와 아이템 기반 CF
- 사용자 기반 CF
	- 지금까지는 사용자를 기준으로 비슷한 취향의 사용자를 찾는 방식으로 진행했다. VBCF
	- 취향이 비슷한 사용자 집단 알아내기
	- 공통적으로 좋게 평가한 아이템 추천
	- 장점
		- 데이터가 풍부한 경우 정확한 추천
	- 단점
		- 결과에 대한 위험성 존재. 갑자기 터무니없는 추천도 가능.
		- 계속 업데이트 해줘야함.
- 아이템 기반 CF : IBCF
	- 사용자의 평가 패턴을 기반으로 아이템 간의 유사도를 계산
	- 예측 대상 사용자가 아이템의 평점과 다른 각 아이템과의 평점을 가중 평균한 것을 그 아이템의 예측값으로 사용한다.
	- 장점
		- 계산이 빠름 -> 대규모 데이터를 사용하느 사이트에서 자주 사용됨
		- 충분한 정보가 없는 경우 유리
	- 단점
		- 업데이트에 대한 영향이 적음
---
# 추천 시스템의 성과측정 지표
- 데이터를 train set 과 test set 으로 분리
	- train set : 학습용
	- test set : 정확도 계산
	- 보통 0.7:0.3, 0.9:0.1 사이에서 결정
- Train set 을 사용해서 학습하고 test set 으로 평가
- 예상 평점과 실제 평점 차이를 계산 후 정확도 측정
- train, test 셋에 들어가는 유저, 아이템을 바꿔가면서 (크로스 밸리데이션 이라고도 함.) 정확도 측정해가면서 모델 성능을 측정할 필요가 있음.
## 각 아이템의 예상 평점과 실제 평점 차이
- RMSE
	- 연속적인 성격의 값에서만 활용할 수 있음.
## 추천한 아이템과 사용자의 실제 선택과 비교
- 현실에서는 이진값인 경우가 매우 많다.
- 다음과 같은 지표들이 있을 수 있다.
	- 정확도 accuracy
		- 올바르게 예측된 아이템의 수 / 전체 아이템의 수
		- 사용자가 아이템을 선택했는지, 혹은 제대로 선택하지 않았는지
	- 정밀도 precision
		- 올바르게 추천된 아이템의 수 / 전체 아이템의 수
		- 추천한 아이템 중에서 실제 선택한 아이템 수. 10개 추천해서 6개 사용자가 선택했다면 0.6 정밀도.
	- 재현율 recall
		-  올바르게 추천된 아이템의 수 / 사용자가 실제 선택한 전체 아이템 수
		- 사용자가 실제로 선택한 전체 아이템 중에서 올바르게 추천한 아이템인가? 10개 담았는데, 추천된 것을 6개 골랐다면 재현율 0.6
	- 정밀도와 재현율의 조화 평균 (F1 score)
		- 2x정밀도x재현율 / 정밀도 + 재현율
		- 정밀도와 재현율은 트레이드오프인데, 이를 조화롭게 유지하고있는가
	- 범위 coverage
		- 추천이 가능한 사용자 수(혹은 아이템 수) / 전체 사용자 수(혹은 아이템 수)
		- 정밀도와 범위는 트레이드 오프.
- TPR
	- True positive rate
	- TP / TP + FN
- FPR
	- False Positive Rate
	- FP / FP+TN