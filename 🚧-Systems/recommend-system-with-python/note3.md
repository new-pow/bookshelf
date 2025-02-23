# Matrix Factorization (MF) 기반 추천
- 추천 시스템의 종류
	- 메모리 기반 알고리즘
		- 메모리에 있는 데이터를 계산해서 추천하는 방식
		- 개별 사용자 데이터 집중
		- 원래 데이터에 충실하게 사용할 수 있다.
		- 대규모 데이터에 느리게 반응한다.
	- 모델 기반 알고리즘
		- **데이터로부터 미리 모델을 구성 후 필요시 추천하는 방식**
		- 전체 사용자 패턴 집중
		- **대규모 데이터에 빠르게 반응**
		- 모델 생성 과정 오래 걸림
- CF 는 메모리 기반 알고리즘이었고, MF는 대표적인 모델 기반 알고리즘이다.

## MF 방식의 원리
- full matrix 를 분리하여 사용한다.
- CF의 경우 전체 행렬을 사용했었음.
![](https://i.imgur.com/cV78w56.png)
- Matrix Factorization(MF)는 **User와 Item 간의 평가 정보를 나타내는 Rating Matrix를 User Latent Matrix와 Item Latent Matrix로 분해**하는 기법을 말한다.
![](https://i.imgur.com/cC1CkY0.png)
![](https://i.imgur.com/NCBFsqW.png)

## SGD (Stochastic Gradient Decent) 를 사용한 MF 알고리즘
**[이부분 이해가 잘 안되므로 다시 들을 것!!]**
- MF 알고리즘 개념적 설명
	- 잠재 요인 K 선택
	- **P,Q 행렬 초기화**
	- 예측 평점 R_hat 계산
		- PxQ(t)
	- 실제 R과 R_hat 간 오차 계산 및 P,Q 수정
	- 기준 오차 도달 확인
- P, Q 행렬 초기화 중요
- 알고리즘의 절차
![](https://i.imgur.com/8mxHWEZ.png)

- overfitting 과적합 방지가 중요
	- 정규화 고려한 식-> 경향성 고려한 식
	- 우측 상단의 제곱이 있는 수식에 대해서.
	- 해당 [링크](https://engineershelp.tistory.com/297)를 참조해주셔서 행렬의 Determinant를 계산하는 방법을 확인해주셨으면 좋겠습니다.
	- 참고로 1x1, 2x2, 3x3 행렬의 Determiant는 링크대로 직접 손으로 계산하실 수 있으나, 4x4이상의 행렬의 경우에는 엄청 복잡한 과정(4x4 -> 3x3으로 바꾸고 계산)을 겪게 됩니다. 그래서 4x4이상부터는 컴퓨터의 선에서 해결가능하니 3x3까지의 determiant 계산하는 과정까지만 이해해주시면 될 것 같습니다.
![](https://i.imgur.com/VgPSnmk.png)

