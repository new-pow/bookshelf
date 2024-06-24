# Section 1. 운영 체제, 프로세스, 스레드의 근본 이해하기
## CPU
- 중앙 처리 장치 central processing unit
-  CPU는 컴퓨터 부품과 정보를 교환하면서 연산하고 외부로 출력합니다.
- 다음의 과정을 반복합니다
	- 명령어 페치
	- 디코딩
	- 메모리 주소에서 데이터 읽어오기
	- 명령어 실행
- 이 때 CPU는 다음 실행할 명령어가 저장된 메모리 주소를 레지스터로부터 읽어옵니다.

### PC 프로그램 카운터
![](https://blogger.googleusercontent.com/img/b/R29vZ2xl/AVvXsEjyhl0xDINDjBwMG859RGJLRrEIsrxmZmoiF66mSPQWeT-U2Gq5UpdnCwKGxD0pQPYHNerZfywGRV9IiEf6emOC5nEIcqEcgQMnhYT4XcWrzAfp_f207sS3_wHZ0nJQW8oEezWrkc0MHtw/s400/007_Registers.png)
(출처 : https://truemind5.blogspot.com/2017/03/01-3.html)
- CPU가 명령어를 가져올 때마다 program counter는 다음 명령어를 가르키기 위해 변경됩니다.
	- 매번 증가만 하는 것은 아닙니다.
- 이 PC의 시작지점은 바로 main 함수의 메모리 주소입니다.
	- 프로그램 코드로부터 찾습니다.
- 매번 그런 것은 아닙니다만...
	- 프로그램 카운터의 초기 값은 컴퓨터 시스템의 특정 아키텍처 및 설계에 따라 다릅니다.
	- 대부분의 경우 프로그램 카운터는 프로그램 실행이 시작되는 메모리 주소로 설정되지만, 인터럽트 핸들러나 운영 체제 루틴과 같은 예외도 있습니다.
- 고급 언어에서는 프로그램 카운터가 프로그래머에게 직접 표시되지 않습니다.
	- 프로그래머는 프로그램 카운터를 명시적으로 조작할 필요 없이 함수, 루프, 조건부 등 언어에서 제공하는 추상화를 사용하여 작업합니다.

### 코어
- 