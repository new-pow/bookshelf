# 데이터베이스의 필요성
- 사회 트렌드 변화
	- Legacy System : 과거에 만들어진 S/W 또는 기술
	- Smart Devices : 많은 기기를 사용하게 됨
	- Social Media : 어느 누가 누구와 어떤 관계를, 어떤 이야기를 하는지가 알수있어짐.
	- Smart Space : 생활 곳곳에서 우리의 정보를 습득하여 정보를 제공해주기 시작함
- 어마어마하게 데이터가 쌓이기 시작했다.
	- 쌓인 데이터들과 다양한 서비스를 연결시킨 무언가가 있었을 것
	- 바로 "데이터베이스 시스템"
---
# 데이터베이스의 이해
## 데이터베이스의 역할
- 데이터의 생성
	- 
- 데이터의 처리
	- 빅데이터 처리: 데이터 수집, 저장, 분석 -> 필요한 정보들을 만들어낼 수 있습니다.
	- AI: 데이터 기반 학습
- 데이터 관리
	- 대량의 데이터를 저장 및 관리하고 필요한 데이터를 신속히 검색할 수 있도록 보조하는 장치에 대한 요구 증가 -> 데이터 베이스
- 데이터베이스
	- 한 조직의 여러 응용시스템을 다수의 사용자가 공용으로 사용하기 위해 통합, 저장, 관리하는 장치

## 데이터 관리의 역사
### 파일 처리 시스템
- 데이터베이스가 개발되기 전 데이터 관리에 사용
- 업무별 애플리케이션이 사용하는 데이터를 개발 데이터 파일에 저장, 관리하는 시스템
- 문제점
	- 데이터 종속의 문제
	- 데이터 중복의 문제
	- 무결성 훼손의 문제
		- 데이터의 정확성을 보장
		- 데이터의 값과 값에 대한 제약 조건을 동시에 만족 -> 힘듦
	- 동시 접근의 문제
---
# 데이터 베이스 특징
- 데이터 베이스 사용의 의미
	- 애플리케이션이 직접적으로 파일에 접근하지 못하도록 함
	- 브로커의 역할
	- 데이터를 사용하는 영역과 데이터를 관리하는 영역을 분리함으로써 파일처리 시스템이 가진 문제를 해결했다.
- 자기 기술성
- 프로그램과 데이터의 격리 및 추상화
- 다중 뷰 제공