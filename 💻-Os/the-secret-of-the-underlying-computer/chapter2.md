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


## 프로세스와 스레드
![](https://upload.wikimedia.org/wikipedia/commons/2/25/Concepts-_Program_vs._Process_vs._Thread.jpg)
- 프로세스
	- 운영체제로부터 시스템 자원을 할당받는 단위입니다.
	- 프로그램이 실행되어 적재시 code, data, stack, heap 영역을 할당받고, PC를 포함하여 프로세스라고 합니다.
- CPU는 이 프로세스를 빠르게 전환하며 여러가지를 조금씩 실행하여 싱글코어임에도 멀티태스킹을 하는 것처럼 구현하였습니다.

### 프로세스간 통신과 스레드 간 통신
- 메모리 공유
	- IPC는 독립된 메모리 공간을 사용하기 때문에 이를 공유하기 위해서 별도 메커니즘이 필요합니다.
	- 스레드는 별도의 스택 영역을 가지며, heap, code 영역 등을 같은 프로세스 내 다른 스레드들과 공유합니다. 그러나 별도 스택영역, PC가 필요하기 때문에 프로세스의 메모리 공간이 소모됩니다.
- 성능
	- 프로세스를 따로 생성하는 데 오버헤드가 있습니다.
	- 스레드간 통신이 일반적으로 IPC보다 빠릅니다. 메모리를 직접 공유하기 때문에 오버헤드가 적습니다.
	- IPC는 시스템 콜을 사용하거나 IO작업을 사용하는 등 추가 작업이 있을 수 있습니다.
- 복합성과 안정성
	- 스레드간 통신에서 동기화를 잘 관리해야합니다. 동시성 문제가 있을 수 있습니다.

### 스레드 풀
- 스레드를 여러 개 생성해두고, 스레드가 처리할 작업이 생기면 해당 스레드에 처리를 요청합니다.
![](https://img1.daumcdn.net/thumb/R1280x0/?scode=mtistory2&fname=https%3A%2F%2Fk.kakaocdn.net%2Fdn%2Fp5S5V%2Fbtqwg3zeAQP%2FtNjcoOvjL3LBOIRPa7Z61k%2Fimg.png)
출처 : https://devahea.github.io/2019/04/21/Spring-WebFlux%EB%8A%94-%EC%96%B4%EB%96%BB%EA%B2%8C-%EC%A0%81%EC%9D%80-%EB%A6%AC%EC%86%8C%EC%8A%A4%EB%A1%9C-%EB%A7%8E%EC%9D%80-%ED%8A%B8%EB%9E%98%ED%94%BD%EC%9D%84-%EA%B0%90%EB%8B%B9%ED%95%A0%EA%B9%8C/

- 스레드 풀의 개수는 많다고 좋지 않습니다. 메모리를 과다하게 점유하거나 스레드 전환으로 생기는 부담 등의 문제가 있을 수 있습니다.
	- CPU 집약적인 작업 `스레드 수 = CPU 코어 수`
	- 입출력 집약적인 작업 `N*(1+WT/CT)`
	- 그러나 실제 상황을 기반으로 테스트를 실시하여 필요한 스레드 수를 결정하는 것이 좋습니다.

## 스레드가 독점하는 스택 영역
- thread context
		- PC register, stack pointer
- 스레드 전용 저장소


### 스레드 안전한 코드를 쓰기 위해서는 어떻게 해야할까?
- 데이터를 리턴합니다.
- 재진입성
	- 어떤 함수가 한 스레드에 의해 호출되어 실행 중일 때, 다른 스레드가 그 함수를 호출하더라도 그 결과가 각각에게 올바로 주어져야 합니다. (값으로 전달)
	- 동적으로 할당된 데이터를 리턴하거나 **호출자 제공 스토리지를 사용**합니다.
```
/* reentrant function (a better solution) */

char *strtoupper_r(char *in_str, char *out_str) 
{ 
	int index; 
	
	for (index = 0; in_str[index]; index++) 
	out_str[index] = toupper(in_str[index]); 
	out_str[index] = 0 
	
	return out_str; 
}
```
- **상호 배제**: 공유 자원을 꼭 사용해야 할 경우 해당 자원의 접근을 세마포어 등의 락으로 통제합니다.
```
/* pseudo-code threadsafe function */ 
int increment_counter(); 
{ 
	static int counter = 0; 
	static lock_type counter_lock = LOCK_INITIALIZER; 
	
	pthread_mutex_lock(counter_lock); 
	counter++; 
	pthread_mutex_unlock(counter_lock); 
	return counter; 
}
```
- **스레드 지역 저장소**: 공유 자원의 사용을 최대한 줄여 각각의 스레드에서만 접근 가능한 저장소들을 사용함으로써 동시 접근을 막습니다.
- **원자 연산**: 공유 자원에 접근할 때 원자 연산을 이용하거나 '원자적'으로 정의된 접근 방법을 사용함으로써 상호 배제를 구현할 수 있습니다.

## 코루틴
- 코루틴은 Co(함께, 서로) + routine(규칙적 일의 순서, 작업의 집합) 2개가 합쳐진 단어로 함께 동작하며 규칙이 있는 일의 순서를 뜻합니다.
![](https://velog.velcdn.com/images/hc-kang/post/bb9bbcc2-1326-49ee-b42b-eda4349c77c8/image.jpeg)
- 루틴의 특징
	- 루틴은 진입하는 곳이 한 곳입니다.
	- 한 번 시작하면 종료될 때까지 멈추지 않습니다.
	- 루틴이 종료되면 그 루틴에서 사용했던 정보가 메모리에서 초기화 되어 사용할 수 없습니다.
- 단, 코루틴은 중단-재개의 기능이 있습니다.
	- 코루틴이 완전히 종료되기 전까지 메모리에서 해당 코루틴에 대한 정보가 계속 남아있다는 점이 매우 중요합니다.
- 이론적으로 메모리 공간이 충분하다면 코루틴 개수에 제한은 없으며 코루틴 간 전환이나 스케줄링은 전적으로 사용자상태에서 일어나기 때문에 운영 체제가 개입할 필요가 없습니다.

### 스레드와 코루틴의 차이점
- 스레드
    - 프로세스 내에서 힙 영역을 공유하고 있기 때문에 스레드간 컨텍스트 스위칭할 때는 스택 영역이 교체됩니다.
    - OS에 의해서 컨텍스트 스위칭이 일어납니다.
- 코루틴
    - 코루틴이 다른 스레드에서 실행될 경우, 스택 영역이 교체됩니다.
    - 코루틴이 같은 스레드에서 실행될 경우, 메모리를 교체할 필요가 없으므로 스레드보다 컨텍스트 스위칭 비용이 적습니다.
    - 코드를 통해 개발자가 코루틴이 다른 코루틴에게 실행을 양보하게 할 수 있습니다. (`yield` 함수 등 subroutine 간의 상호작용)
![](https://velog.velcdn.com/images/hc-kang/post/9011fdfb-54b7-4908-a188-84ce75037f1c/image.jpeg)


