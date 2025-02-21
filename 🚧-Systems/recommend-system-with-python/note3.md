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
- 