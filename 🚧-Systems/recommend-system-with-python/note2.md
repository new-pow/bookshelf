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
