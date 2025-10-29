# 코틀린 코루틴(Kotlin Coroutines: Deep Dive)
---
# 1부: 코틀린 코루틴 이해하기
## 1장: 코틀린 코루틴을 배워야하는 이유
- 코루틴이란 개념은 무엇인가?
- 코틀린 코루틴이 유용한 이유
	- 멀티플랫폼에서 작동시킬 수 있기 때문에 코틀린을 사용하는 모든 플랫폼을 넘나들며 사용할 수 있음.
	- 기존 코드 구조를 광범위하게 뜯어고칠 필요도 없음.
- 코루틴을 사용하는 예시
	- 여러 소스로부터 데이터를 얻어오는 요구사항이 있을때
	- 1. 스레드 전환을 시도할 수 있으나
		- 문제점
			- 스레드가 실행되었을때 멈출 수 있는 방법이 없으면 메모리 누수로 이어질 수 있음.
			- 스레드를 많이 생성하면 비용이 많이 듬.
			- 스레드를 자주 전환하면 복잡도를 증가시키며 관리하기도 어려움.
			- 코드가 쓸데없이 길어지고 이해하기 어려워짐.
	- 2. 콜백
		- 문제점
			- 콜백구조로는 병렬 처리가 힘듬.
			- 취소할 수 있도록 구현하려면 많은 노력이 필요.
			- 들여쓰기가 많아질 수록 코드는 읽기 힘듬 (콜백지옥)
	- 3. RxJava 등 다른 라이브러리
		- 너무 복잡
		- 새롭게 라이브러리에 대한 지식이 필요함.
- 코틀린 코루틴의 사용
	- 코루틴을 정의하면, 중단했다가 다시 실행할 수 있는 컴포넌트
	- 코루틴의 중단은 데이터가 오는 걸 기다릴 때(스레드를 블로킹하는 대신) 코루틴을 잠시 멈추는 방식으로 작동합니다.
	- 데이터가 준비되면 코루틴은 다시 메인 스레드를 할당받아 이전에 멈춘 지점부터 다시 시작
- 백엔드에서의 코틀린 코루틴 사용
	- 간결하다.
	- 동시성을 쉽게 구현할 수 있고, 동시성 테스트를 할 수 있으며, 코루틴을 취소할 수도 있다.
	- 스레드보다 코루틴을 시장하는 비용이 현저히 적다.
## 2장. 시퀀스 빌더
- 시퀀스
	- List, Set 과 같은 컬렉션과 비슷한 개념.
	- 필요할 때마다 값을 하나씩 계산하는 지연(lazy) 처리를 한다.
	- 특징
		- 요구되는 연산을 최소한으로 수행합니다. -> 어떻게?
		- 무한정이 될 수 있다. -> 어떻게?
		- 메모리 사용이 효율적이다.
			- 이펙티브 코틀린: 아이템 49
- 동작 방식
	- 다른 요소를 찾기 위해 멈췄던 지점에서 다시 실행이된다. -> main 함수와 시퀀스 제너레이터가 번갈아가면서 실행된다.
	- `yield()` 외에 다른 중단함수를 사용할 수 없다.
		- SequenceScope 에 RestrictsSuspension 어노테이션이 있기 때문. 리시버가 SequenceScope 가 아닐 경우 중단 함수를 호출하는 것을 허용하지 않는다.
		- `sequence {}`는 **lazy generator**를 만드는 DSL입니다.  즉, `yield()`나 `yieldAll()`을 사용해서 **값을 하나씩 생성하고**, 실제로는 **이터레이터 기반으로 동작**합니다.
		- “멈춤”은 **진짜 코루틴 suspension이 아니라**, 내부적으로 **상태 머신(state machine)**으로 변환되어 구현됩니다.
		- `RestrictsSuspension` 어노테이션의 역할
			- ```
			  @RestrictsSuspension
				public abstract class SequenceScope<in T> internal constructor() {
				    public abstract suspend fun yield(value: T)
				    public abstract suspend fun yieldAll(iterator: Iterator<T>)
				    ...
				}

			  ```
		  - `sequence {}` 블록 안에서는 `SequenceScope`를 리시버로 가진 `suspend fun yield()`는 호출 가능.
		  - `sequence {}`는 **코루틴처럼 보이지만 실제 코루틴 런타임을 사용하지 않습니다.**  즉, `suspend` 키워드는 문법적으로 사용되지만, **non-suspendable 컨텍스트**에서 상태 기계로 변환됩니다.
		  - 따라서 만약 `delay()`처럼 진짜 코루틴 suspension을 호출해버리면,  런타임에 suspend point를 재개할 수 있는 컨텍스트(`Continuation`)가 없어져 **비정상 상태**가 됩니다.
		  - 이를 방지하기 위해 `RestrictsSuspension`이 붙어 있습니다.  컴파일러가 “이 리시버가 아닌 곳에서 suspend 호출을 시도하면 컴파일 에러”를 내는 것입니다.
```kotlin
val seq = sequence {
    yield(1)
    yield(2)
}
```
- 위를 컴파일하면 이렇게 변환된다.
```kotlin
val seq = Sequence {
    val iterator = object : Iterator<Int> {
        var state = 0
        override fun next(): Int {
            when (state) {
                0 -> { state = 1; return 1 }
                1 -> { state = 2; return 2 }
                else -> throw NoSuchElementException()
            }
        }
        override fun hasNext() = state < 2
    }
    iterator
}
```

- flow 와의 비교

| 항목            | `sequence {}`                 | `flow {}`                  |
| ------------- | ----------------------------- | -------------------------- |
| 런타임           | 동기적, 상태 기계                    | 진짜 코루틴 (비동기 가능)            |
| suspend 함수 호출 | 불가 (`RestrictsSuspension`)    | 가능 (`delay`, `network`, 등) |
| 호출 방식         | `for`/`iterator()` (blocking) | `collect()` (suspend)      |
| 목적            | Lazy generator                | 비동기 스트림                    |

---
## 3장. 중단은 어떻게 작동할까?
- 코루틴이 중단되었을 때 Continuation 객체를 반환한다.
	- 멈췄던 곳에서 코루틴을 실행할 수 있다.
	- 스레드와의 다른점. -> 스레드는 저장이 불가능함.
- 중단했을 때, 코루틴은 어떤 자원도 사용하지 않는다.
	- 다른 스레드에서 시작할 수 있고
- Continuation
	- 직렬화와 역직렬화 가능
### 재개
- `suspendCoroutine` 의 경우 `resume` 으로 재개할 수 있다.
```kotlin
import kotlin.coroutines.resume  
import kotlin.coroutines.suspendCoroutine  
  
suspend fun main() {  
    println("Before")  
    println(Thread.currentThread().name)  
    suspendCoroutine<Unit> { cont ->  
        cont.resume(Unit)  
    } // Suspends here  
    println("After")  
}
```
- 현재 실무에서
	- ❌ 사용 안 함: suspendCoroutine, resumeWith (저수준)
	- ✅ 사용 중: suspend, launch, channel, delay (고수준)
- Channel 을 예시로 한다면
	- 1️⃣ LocalConsumer.kt - Channel로 중지/재개
		```kotlin
  override suspend fun consume(listener: Listener<Message>) {
      channel.consumeEach { message ->  // ← 여기서 중지된다!
          listener.handle(message)
      }
  }
		```

	- 원리:
		- consumeEach는 메시지를 받을 때까지 코루틴을 중지
		  -  메시지가 도착하면 자동으로 재개
		  - 내부적으로 suspendCoroutine이 숨겨져 있음

	-   2️⃣ Channel.send() - 중지/재개 예시
		```
		 suspend fun send(message: Message) {
	      channel.send(message)  // ← 버퍼가 찼으면 중지, 소비되면 재개}
		  ```
		
		 - 내부 동작 (개념적):
			```
			 // Channel.send()의 내부 원리 (실제로는 더 복잡함)
			  suspend inline fun <E> Channel<E>.send(element: E) {
			      suspendCoroutine<Unit> { continuation ->
		          // 메시지 추가 시도
		          // 성공 → continuation.resume(Unit)
		          // 실패 → 큐에 저장 후 나중에 resume
			      }
			  }
			```
 
- ScheduledExecutorService 를 사용할 수 있음.
	- 정해진 시간이 지나면 resume(Unit) 을 호출하도록 함.
- 28p. 이그제큐터는 스레드를 사용하긴 하지만 delay 함수를 사용하는 모든 코루틴의 전용 스레드입니다.
```kotlin
private val executor = Executors.newSingleThreadScheduledExecutor {  
    Thread(it, "scheduler").apply { isDaemon = true } // isDaemon 의 뜻: 백그라운드에서 실행되는 스레드.  
    // 스레드를 사용하기는 하지만, delay  
}  
  
suspend fun delay(timeMillis: Long) {  
    suspendCoroutine<Unit> { continueIt ->  
        println("delay1: " + Thread.currentThread())  
  
        executor.schedule({  
            continueIt.resume(Unit)  
            println("delay2: " + Thread.currentThread())  
        }, timeMillis, java.util.concurrent.TimeUnit.MILLISECONDS)  
  
        println("delay3: " +Thread.currentThread())  
    }}  
  
suspend fun main() {  
    println("Start")  
    println(Thread.currentThread())  
  
    delay(2000L)  
  
    println("End")  
}

// Start
// Thread[#1,main,5,main]
// delay1: Thread[#1,main,5,main]
// delay3: Thread[#1,main,5,main]
// End
// delay2: Thread[#21,scheduler,5,main]
```

### 값으로 재개하기
- 값으로 재개하는 것은 자연스럽다.
- 데이터를 받으면 스레드는 코루틴이 중단된 지점부터 재개한다.

### 예외로 재개하기
- 예제 마저해보기

### 함수가 아닌 코루틴을 중단시킨다
- 주의: 함수를 중단시키면 메모리 누수가 일어날 수 있음.

---
## 4장. 코루틴의 실제 구현
- 중요한점
	- 중단 함수는, 함수가 시작할때와 중단 함수가 호출되었을 때 상태를 가진다
	- continuation 객체는 상태를 나타내는 숫자와 로컬 데이터를 가지고 있다.
	- 함수의 컨티뉴에이션 객체가 이 함수를 부르는 다른 함수의 컨티뉴에이션 객체를 장식decorate 한다.
		- 모든 continuation 객체는 실행을 재개하거나 재개된 함수를 완료할 때 사용되는 콜스택으로 사용된다.
### 컨티뉴에이션 전달 방식
- 함수에서 인자를 통해 전달됩니다.
- 관례상 마지막 파라미터로 전달됩니다.
- 중단함수는 T 혹은 null 혹은 중단 마커를 반환합니다.
### 아주 간단한 함수
- 함수는 상태를 저장하기 위해 자신만의 continuation 객체가 필요하다.
	- 함수 자체의 continuation 으로 포장한다.
- 중단 객체를 반환하는 경우, 콜스택에 있는 모든 함수가 종료되며, 중단된 코루틴이 실행하던 스레드를 (다른 종류의 코루틴을 포함해) 실행가능한 코드가 사용할 수 있게 됩니다. (???) p. 39
### 상태를 가진 함수
- 함수가 중단된 후에 다시 사용할 지역변수나 파라미터와 같은 상태를 가지고 있다면, 함수의 continuation 객체에 상태를 저장해야합니다.
### 값을 받아 재개되는 함수
- 중단 함수로 값을 받아야하는 경우
- 파라미터와 반환값 모두 컨티뉴에이션 객체에 저장되어야 합니다.
### 콜 스택
- call stack  자료구조에 저장됩니다.
- 코루틴을 중단하면 스레드를 반환해 콜 스택에 있는 정보가 사라집니다.
- continuation 객체가 콜스택의 역할을 대신합니다.
	- 상태label
	- 함수의 지역변수, 파라미터
	- 재개 위치 정보를 가지고 있습니다.
- continuation 객체는 자신이 담당하는 함수를 먼저 호출합니다.
	- 함수의 실행이 끝나면 자신을 호출한 함수의 컨티뉴에이션을 재개합니다.
---
## 5장. 언어 차원에서의 지원 vs 라이브러리
- kotlin.coroutines
	- 언어 차원에서 지원하는 라이브러리
	- Continuation, suspendCoroutines, suspend 등 최소한의 키워드를 제공한다.
- kotlinx.coroutines
	- 별도 라이브러리
	- launch, async, Deferred 처럼 다양한 기능을 제공
---
# 2부. 코틀린 코루틴 라이브러리
## 6장. 코루틴 빌더
- 중단 함수가 시작되는 지점이 있습니다. -> 코루틴 빌더 coroutine builder
	- 중단 함수는 다른 중단함수에 의해 호출되어야 하기 때문입니다.
- 필수 코루틴 빌더
	- launch
	- runBlocking
	- async
### launch
- CoroutineScope 인터페이스의 확장함수
	- 구조화된 동시성의 핵심
- 실제 현업에서는 지양함.
- 메인함수가 끝나면 하위 코루틴이 중단됨.
- ?? Job 을 기다리면 되지 않을까? `join()` 등으로... p.60
#### 생애주기
```
launch 호출
  ↓
Job 생성 (즉시 반환)
  ├─ Job 사용 가능
  │  ├─ job.join() - 완료 대기
  │  ├─ job.cancel() - 취소
  │  └─ job.isActive - 상태 확인
  │
  └─ 백그라운드에서 코루틴 실행
     ├─ launch { } 내 코드 실행
     ├─ (시간 경과)
     └─ 코루틴 완료
       ↓
    job.isActive = false
```

#### 사용패턴
##### 패턴 1: Fire-and-Forget (반환값 버림)  
  
```kotlin  
scope.launch {  
    doSomething()  
}  
// Job 버림 → 완료 추적 불가  
```  
  
**특징**:  
- 가장 간단한 사용법  
- 결과나 완료 여부가 중요하지 않을 때 사용  
- 예: 로깅, 분석 이벤트 전송  
  
**위험성**:  
- 작업이 완료되기 전에 프로그램 종료 가능  
- 작업 상태를 알 수 없음  
  
##### 패턴 2: Job 저장 및 제어  
  
```kotlin  
val job = scope.launch {  
    doSomething()  
}  
  
// Job을 통한 제어  
job.join()          // 완료 대기  
job.cancel()        // 즉시 취소  
println(job.isActive)  // 실행 상태 확인  
```  
  
**특징**:  
- 코루틴 생명주기 명시적 관리  
- 필요시 언제든 취소 가능  
  
**사용 사례**:  
- 사용자가 취소할 수 있는 작업  
- 완료를 기다려야 하는 작업  
- 타임아웃 처리가 필요한 작업  
  
##### 패턴 3: CoroutineStart.LAZY (지연 시작)  
  
```kotlin  
val job = scope.launch(start = CoroutineStart.LAZY) {  
    doSomething()  
}  
  
// 이 시점에 코루틴은 생성되었지만 시작 안 됨  
job.start()  // 명시적으로 시작  
job.join()   // 완료 대기  
```  
  
**특징**:  
- 코루틴이 준비되지만 시작 지연  
- 시작 시점을 명시적으로 제어  
  
**사용 사례**:  
- 리소스 초기화 지연  
- 특정 조건 만족 시에만 시작  
- 시작 시점 최적화
#### Job 추적  
  
```kotlin  
fun processMessages(messages: List<Message>) {  
    val jobs = messages.map { message ->  
        scope.launch {  
            handleMessage(message)  
        }    
	}
    jobs.forEach { it.join() }  
    println("done")
}  
  
// 또는 awaitAll
suspend fun processMessagesOptimal(messages: List<Message>) {  
    messages.map { message ->  
        scope.launch {  
            handleMessage(message)  
        }    
	}.awaitAll()  
    println("done")  
}  
```



### runBlocking
- 블로킹이 필요한 경우
	- 메인함수의 경우, 프로그램이 너무 빨리 끝나면 안됨.
	- runBlocking 을 사용하면, 시작한 스레드를 중단시킨다. = 새로운 코루틴을 실행하고 완료될때까지 현재 스레드를 중단 가능한 상태로 블로킹한다.
	- 혹은 test 시에 사용하기도 함.
- 현재는 거의 사용되지 않음.
	- 유닛 테스트에서는 코루틴을 가상 시간으로 실행시키는 `runTest`  가 주로 사용되고 있음.
	- ? 저의 경우, `scheduler` 등 executor 코드에 사용중...
- main 함수는 suspend 를 붙여 중단 함수로 만드는 방법을 주로 사용함.
### async
- `Deferred<T>` 의 객체를 리턴하며, 작업이 끝나면 반환하는 중단 메서드인 `await ` 를 사용한다.
- 만약 값이 생성되기 전에 await 를 호출하면 값이 나올때까지 기다린다.
	- 값이 필요하지 않을 때는 launch 로 사용한다.
- 두가지 다른 곳에서 데이터를 얻어와 합치는 경우처럼, 두 작업을 병렬로 실행할 때 주로 사용됩니다.
- ?? 제일 많이 사용함.

### 구조화된 동시성
- 부모-자식 간의 구조화된 동시성
	- 자식은 부모로부터 context 를 상속받는다.
	- 부모는 모든 자식이 작업을 마칠때까지 기다린다.
	- 부모 코루틴이 취소되면 자식 코루틴도 취소된다.
	- 자식 코루틴에서 에러가 발생하면, 부모 코루틴 또한 에러로 소멸한다.
- `runBlocking`은 자식이 될 수 없으며, 루트 코루틴으로만 사용될 수 있다.

### coroutineScope
- 중단 함수 밖에서 스코프를 만들려면 coroutineScope 함수를 사용합니다.
	- 메인함수와 runBlocking 을 함께 사용하는 것보다 세련되었습니다. (왜지...?) p.70
- CoroutineScope
	- 코루틴은 스코프 내에서만 실행되며, 스코프가 종료되면 그 스코프 내에서 실행된 모든 코루틴도 자동으로 취소됩니다.
		- 실행 중인 Coroutine을 추적함으로써 작업을 더 이상 실행할 필요가 없을 때 취소할 수 있습니다.
	- 코루틴이 실행되는 범위로, 코루틴을 실행하고 싶은 Lifecycle에 따라 원하는 Scope를 생성하여 코루틴이 실행될 작업 범위를 지정할 수 있습니다.
	- 코루틴을 실행하는 범위를 제공하는 컨테이너.
	- CoroutineContext를 포함하며, 코루틴의 생명주기를 관리.
	- 사용하는 이유
		- 부모-자식 관계를 유지하여 구조적 동시성(structured concurrency) 보장.
		- 부모가 취소되면 자식도 자동으로 취소됨.
		- 여러 개의 코루틴을 하나의 범위 내에서 관리 가능.

---
##### 사례 1: 리스너 라우팅 (비동기 처리)  
  
**상황**: 메시지 수신 시 여러 핸들러를 병렬로 실행  
  
**구현 개요**:  
```  
메시지 도착  
  ↓Router.handle(message) 호출  
  ↓등록된 각 핸들러마다 launch 실행  
  ├─ launch { handler1(message) }  // 즉시 반환  
  ├─ launch { handler2(message) }  // 즉시 반환  
  └─ launch { handler3(message) }  // 즉시 반환  
  ↓Router.handle() 종료 (핸들러 완료 기다리지 않음)  
```  
  
**특징**:  
- **독립적 실행**: 각 핸들러는 별도 코루틴에서 실행  
- **격리된 예외**: 한 핸들러 예외가 다른 핸들러 영향 없음  
- **Fire-and-Forget**: Job을 저장하지 않음  
  
**코드 구조** (추상화):  
```kotlin  
// 라우터 구현  
class MessageRouter {  
    private val handlers = mutableListOf<suspend (Message) -> Unit>()  
  
    fun register(handler: suspend (Message) -> Unit) {  
        handlers.add(handler)  
    }  
  
    suspend fun route(message: Message) {  
        handlers.forEach { handler ->  
            routerScope.launch {  // 각 핸들러를 독립 코루틴에서 실행  
                try {  
                    handler(message)  
                } catch (e: Exception) {  
                    logger.error("Handler failed", e)  // 격리된 예외 처리  
                }  
            }  
        }        // 이 시점에 모든 핸들러 완료 보장 없음  
    }  
  
    private object routerScope : CoroutineScope {  
        override val coroutineContext =            Executors.newFixedThreadPool(workerCount)  
                .asCoroutineDispatcher()  
    }  
}  
```  
  
**장점**:  
- 메시지 수신 후 빠른 응답  
- 핸들러 간 독립성 보장  
- CPU 코어 수에 따른 자동 스케일링  
  
**주의사항**:  
- 모든 핸들러 완료가 중요한 경우 Job 수집 필요  
- 빠른 프로그램 종료 시 실행 중인 핸들러 미완료 가능  
  
##### 사용 사례 2: 메시지 소비 루프 (지연 시작)  
  
**구현 개요**:  
```  
BeBus 인스턴스 생성  
  ↓launch(start = CoroutineStart.LAZY) 호출  
  ├─ 코루틴 생성 (아직 시작 안 함)  
  └─ Job 저장  
  ↓필요한 시점에 job.start()  ↓메시지 소비 루프 시작  
  ├─ Channel에서 메시지 대기  
  └─ 메시지 도착 시 핸들러 호출  
```  
  
**특징**:  
- **LAZY 시작**: 코루틴 생성 후 시작 지연  
- **명시적 제어**: `job.start()` 호출로 시작  
- **생명주기 관리**: 버스 초기화 타이밍 조정  
  
**코드 구조** (추상화):  
```kotlin  
class MessageBus {  
    private val consumerJob by lazy {  
        busScope.launch(start = CoroutineStart.LAZY) {  
            messageConsumer.consume(messageHandler)  
            // (무한 대기)  
        }  
    }  
    // 초기화 시 consumerJob.start() 호출됨  
    private val messageConsumer by lazy {  
        createConsumer().also {  
            consumerJob.start()  // ← 명시적 시작  
        }  
    }  
    private object busScope : CoroutineScope {  
        override val coroutineContext =            Executors.newFixedThreadPool(workerCount)  
                .asCoroutineDispatcher()  
    }  
}  
```  
  
**사용 구조**:  
```kotlin  
// 1. 버스 인스턴스 생성 (consumerJob은 아직 LAZY)val bus = BeBusFactory.get("myTopic")  
  
// 2. 리스너 등록  
bus.listen(MyMessageHandler) 
// 대기 
  
// 3. messageConsumer 접근 → job.start() 자동 호출  
val messageConsumer = bus.getConsumer()  
  
// 4. 이제 메시지 consum 시작  
```

---
## 7장. 코루틴 컨텍스트
### CoroutineContext 인터페이스
- 원소나 원소들의 집합을 나타내는 인터페이스
- Job, CoroutineName, CoroutineDispatcher 와 같은 Element 객체들이 인덱싱된 집합
	- 맵이나 셋과 같은 컬렉션과 개념이 비슷함
	- 컬렉션 내 모든 원소는 그 자체만으로 컬렉션임
- 컨텍스트의 모든 원소는 식별 가능한 유일한 Key 를 가지고 있음.
- CoroutineContext 에서 원소 찾기
	- `get` 으로 key를 가진 원소를 찾을 수 있다.
- context 더하기
	- + 로 합치기 가능
	- 그러나 같은 키를 가지고 있다면 덮어쓰일수 있으니 주의!
- 비어있는 코루틴 컨텍스트 만들기 가능
- 컨텍스트 폴딩
	- fold 를 사용할 수 있다.
	- 누산기의 첫번째 값과 다음상태 계산방식을 정의하면 됨.
- 컨텍스트 빌더
	- 부모는 기본적으로 컨텍스트를 자식에게 전달한다. (상속받음)
	- 그래서 defaultContext + parentContext + childContext 가 된다.
- 중단 함수에서 컨텍스트 접근하기
	- 모든 중단 스코프에서 컨텍스트 접근이 가능하다.
- 컨텍스트 개별적으로 생성하기
	- `CoroutineContext.Element` 인터페이스를 구현하면 된다.
	- ??? 실무에서 사용한 적이 있는지? 예시가 있는지?
- 
---
## 8장. 잡과 자식 코루틴 기다리기
### Job 이란 무엇인가?
- 수명을 가지고 있으며, 취소가 가능하다.
- 수명은 상태로 나타낸다.
  
```  
launch 호출  
  ↓Job 생성 (즉시 반환)  
  ├─ Job 사용 가능  
  │  ├─ job.join() - 완료 대기  
  │  ├─ job.cancel() - 취소  
  │  └─ job.isActive - 상태 확인  
  │  
  └─ 백그라운드에서 코루틴 실행  
     ├─ launch { } 내 코드 실행  
     ├─ (시간 경과)  
     └─ 코루틴 완료  
       ↓    job.isActive = false
```

- p.86 도표에 라이프사이클이 나와있음.
### 코루틴 빌더는 부모의 Job 을 기초로 자신의 Job 을 생성한다.
- 모든 코루틴 빌더는 자신만의 Job 을 생성한다.
	- async 도 Job 인터페이스를 구현하고 있음.
- 추적이 가능한 이유: 부모의 잡은 자식 잡 모두 참조할 수 있으며, 자식또한 부모를 참조할 수 있다.
	- 모든 코루틴은 자신만의 Job 을 생성하며 인자 또는 부모 코루틴으로부터 온 잡은 새로운 잡의 부모로 사용한다. p.89
- 자식 코루틴을 기다리는 방법
	- `join()` 메서드를 사용한다.
- p.93
	- Job() 은 생성자 처럼 보이는 간단한 함수 ㅋㅋㅋ CompletableJob 이 반환된다.
		- complete() ,completeExceptionally(throwable) 이 가능하다.
---
## 9장. 취소
- 단순히 스레드를 죽이면 연결을 닫고 자원을 해제하는 기회가 없기 때문에 최악의 취소 방식
- 기본적인 취소: Job.cancel()
	- 호출한 코루틴은 첫번째 중단점에서 job 을 종료
	- 자식 job을 가지고 있다면 그또한 취소. 하지만 부모는 영향을 받지 않는다.
	- job 이 취소되며, 취소된 job 은 새로운 코루틴의 부모가 될 수 없다.
	- cancel() 에 인자로 예외를 넣으면, 취소된 원인을 명확하게 할 수 있다.
		- 인자가 되는 예외는 CancellationException 의 서브타입이어야 한다.
- `cancelAndJoin()`
	- `job.join()`을 뒤에 추가하면, 코루틴이 취소를 마칠때까지 중단되므로 경쟁상태가 발생하지 않는다. p.99 ???
	- 이를 편리하게 만든 함수.
	- job에 딸린 수많은 코루틴을 한번에 취소할 때 자주 사용된다.
### 취소는 어떻게 작동하는가?
- Job 의 상태가 Cancelling 으로 바뀜.
	- 첫번째 중단점에서 예외 `CancellationException` 을 던짐.
- 즉, 예외를 사용해 취소되므로 catch 하여 처리할수도잇음.
- 취소중 코루틴을 한번 더 호출하면?
	- 중단되거나, 다른 코루틴을 시작하는 것은 절대 불가능하다. -> 다른 코루틴 시작하려고하면 무시함.
	- 만약 코루틴이 이미 취소되었을때, 중단함수를 반드시 호출해야하는 경우 `withContext()`로 포장하는 방법을 많이 사용한다.
	- finally 로 캐치하여 `withContext(NonCancellable) {함수}` p.103 참조
- 자원을 해제하는 방법 `invokeOnCompletion()` 메서드 호출
	- `Completed`나 `Cancelled` 상태에 도달했을때 호출될 핸들러를 지정할 수 있다.
```
job.invokeOnCompletion {e ->
	// TODO
}
```
- Job 이 예외 없이 끝난다면 Null 을 파라미터로 받는다.
	- 코루틴이 취소되면 `CancellationException`이 됨
	- 코루틴을 종료시킨 예외를 받을 수도 있음.
- `onCancelling`, `invokeImmediately`도 참조 가능.
- 중단할 수 없는 것을 중단하기
	- 예를들어 Tread.sleep
	- `yield()` 로 중단점을 만들어준다. -> 취소나 디스패처로 스레드를 바꾸는 등을 할 수 있다.
	- 중간중간 job 상태를 추적한다. -> active 하지 않으면 job을 취소하거나, 예외를 던질 수 있다.
		- `ensureActive()`
### suspendCancellableCoroutine
- `invokeOnCacellation` 메서드 매우 중요!!!! p.109
---
## 10장. 예외처리
- 잡히지 않은 예외가 발생하면 코루틴이 종료된다.
	- 코루틴 빌더의 경우, 부모도 종료시키고 다른 자식들도 모두 취소시킨다.
	- runBlocking 의 경우, 예외를 다시 던지므로 프로그램을 종료시킬 수 있음.
- 종료를 멈추려면?
	- 코루틴에게 try catch 는 무시됨.
### SupervisorJob
- 자식에게 발생한 모든 예외를 무시. 
- 같은 job을 여러 코루틴에서 컨텍스트로 사용하는 것이 나은 방법임
### supervisorScope 로 래핑하는 방법
- 다른 코루틴에서 발생한 예외를 무시하고, 부모와의 연결을 유지한다.
- 서로 무관한 다수의 작업을 스코프 내에서 실행하면 된다.
### CancellationException 은 부모까지는 전파되지 않음.

---
# 11장. 코루틴 스코프 함수
-  GlobalScope 를 사용하는 것은 좋은 방법이 아니다.
	- GlobalScope은 싱글톤으로 어플리케이션 생명주기에 따라 동작한다.
	- 이 하위에서 호출되는 async 는 부모 코루틴과 아무런 관계가 없다.
		- 취소될 수 없다.
		- 관리가 어려워진다.
		- 부모가 취소되어도 async 내부 함수가 실행중인 상태를 유지하므로, 작업이 끝날 때까지 자원이 낭비된다.
		- 즉, 메모리 누수가 발생할 수 있으며 쓸데없이 CPU를 낭비한다.
		- 코루틴을 단위테스트하는 도구가 작동하지 않는다. **?? 왜?**
			- 테스트 scope 를 따로 구현해야합니다.
	- EmptyCoroutineContext 를 가졌다.
- Scope 를 인자로 넘기는 방법
	- 스코프에서 예상하지 못한 부작용이 발생할 수 있음.
		- async 예외가 발생하거나 cancel 메서드를 사용해 스코프를 최소하는 등 조작이가능.
## coroutineScope
- 스코프를 시작하는 중단 함수
- 리시버 없이 곧바로 호출이 가능하다.
- 새로운 코루틴을 생성하고, 그 코루틴이 끝날때까지 coroutinScope 를 호출한 코루틴을 중단한다.
- 생성된 scope
	- 바깥 스코프에서 coroutineContext 를 상속받는다.
	- context의 Job 을 오버라이딩한다. = 부모가 해야할 책임을 이어받는다.
	- 자신의 작업을 끝내기 전가지 모든 자식을 기다린다.
	- 부모가 취소되면 자식들을 모두 취소한다.
- coroutineBuilder 와는 달리, 스코프에 속한 자식에서 예외가 발생하면 다른 모든 자식이 취소되고 예외가 던져진다.
- 그래서 병렬로 작업을 수행할 경우, coroutineScope 를 사용하는 것이 좋다.
	- runBlocking 함수 대체
- ❓ async 를 1개만 할때는 안써야 좋을까?
	- `async`는 새로운 **Job**을 만들고, 예외 처리나 취소 처리가 한 단계 더 복잡해집니다.
    - 또한 예외가 발생해도 `await()`하기 전까지 전파되지 않기 때문에,  테스트나 구조적 동시성에서 디버깅이 더 어렵습니다.
	- 굳이 써야하는 경우? 한 개만 있어도 `async`가 의미 있을 때는 **지연 실행(lazy start)** 이 필요한 경우입니다.
		- ```
			  coroutineScope {
			    val deferred = async(start = CoroutineStart.LAZY) { heavyWork() }
			    // 무언가 준비 로직
			    deferred.start()
			    val result = deferred.await()
			}

		  ```
	
## 코루틴 스코프 함수
- 스코프를 만드는 다양한 함수
- supervisorScope
	- Job 대신 SupervisorJob 을 사용함.
- withContext
	- context 를 바꿀 때
- withTimeout
	- timeout 을 추가하고싶을 때
- ❓확장함수, 중단함수?

| 구분                             | 예시                                | 의미                                                                             |
| ------------------------------ | --------------------------------- | ------------------------------------------------------------------------------ |
| **확장 함수 (extension function)** | `fun CoroutineScope.launchTask()` | `CoroutineScope` 타입의 객체(예: ViewModelScope, lifecycleScope)에서 호출할 수 있는 일반 함수    |
| **중단 함수 (suspend function)**   | `suspend fun fetchData()`         | **코루틴 안에서만** 실행 가능한 함수. `delay()`, `withContext()` 같은 다른 suspend 함수들을 호출할 수 있음 |
### 코루틴 빌더와 코루틴 스코프 함수
- 코루틴 빌더
	- 코루틴을 **생성하고 실행**하는 함수입니다.  
	- 대표적으로 `launch`, `async`, `runBlocking` 등이 있습니다.
	- **특징**
		- 실제로 **새로운 코루틴을 시작**합니다.
		- 결과 타입이 다릅니다:
		    - `launch` → `Job` 반환 (결과 없음)
		    - `async` → `Deferred<T>` 반환 (결과 있음)
            - `runBlocking` → 현재 스레드를 블로킹하면서 실행
	- 호출 즉시 **CoroutineScope** 내부에서 코루틴을 실행합니다.
- 코루틴 스코프 함수
	- 코루틴을 실행할 **“범위(scope)”** 를 규정하는 함수입니다.
	- 대표적으로 `coroutineScope`와 `supervisorScope`가 있습니다.
	- 특징
		- 새 코루틴을 생성하지 않습니다.
		- 단지 “안전한 실행 범위”를 만들어, **내부에서 여러 빌더를 실행할 수 있게 함**
		- **자식 코루틴이 모두 끝날 때까지 suspend** 됩니다.
		- 예외 처리 방식이 다릅니다:
			- `coroutineScope`: 자식 코루틴 하나라도 실패하면 모두 취소
			- `supervisorScope`: 실패해도 다른 자식에는 영향 없음

|구분|코루틴 빌더|코루틴 스코프 함수|
|---|---|---|
|목적|코루틴을 **생성하고 실행**|**스코프(범위)** 정의|
|대표 함수|`launch`, `async`, `runBlocking`|`coroutineScope`, `supervisorScope`|
|코루틴 생성 여부|새 코루틴을 생성|❌ (기존 코루틴 안에서 스코프만 생성)|
|반환|`Job`, `Deferred<T>`, 또는 값|스코프 내 suspend 완료 시 결과 반환|
|예외 전파|부모 스코프에 전파|스코프 규칙에 따라 제어 (`coroutineScope`=전파, `supervisorScope`=독립)|
|동작 위치|항상 새로운 코루틴 시작|이미 실행 중인 코루틴 내에서 사용|

### withContext
- 부모 스코프의 컨텍스트를 대체한다.
- `withContext(EmptyCoroutineContext)` 와 `coroutineScope()` 는 동일하게 작동한다.
- 디스패처와 함께 종종 사용되곤 한다.
### supervisorScope
- 호출한 스코프로부터 사속받은 CoroutineScope 를 만들고 지정된 중단 함수를 호출합니다.
- Job을 SupervisorJob 으로 오버라이딩하여, 자식 코루틴이 예외를 던지더라도 취소되지 않습니다.
- 서로 독립적인 작업을 시작하는 함수에서 주로 사용됩니다.
- 주의!! launch 는 즉시 예외가 전파되므로 이를 막는 것으로 끝남.
	- **그러나 async 는 예외가 부모로 전파되는 것 외에, 추가적인 예외처리가 필요함. `await()` 를 호출하면 예외를 다시 던지기 때문이다.**
- 주의!! `withContext(supervisorJob())` 을 사용할 수 없음.
	- 기존 가지고 있던 Job을 사용하며, SupervisorJob() 이 해당 잡의 부모가 됩니다.
	- 따라서 자식 코루틴이 예외를 던지면 다른 자식들도 취소됩니다.
	- 의미가 없음
### withTimeout
- 인자로 들어온 람다식을 실행할 때 시간제한이 있다는 점이 다른점.
- 시간이 너무 오래걸리면 람다식은 취소되고 `TimeoutCancellationException : CancellationException` 을 던진다.
- 특히나 테스트할 때 유용하다. 특정 함수가 시간이 짧게 혹은 길게 걸리는지 확인하는 테스트 용도로도 사용한다.
- `runTest` 내부에서 사용하면 `withTimeout`은 가상 시간으로 작동한다.
- `TimeoutCancellationException` 은 코루틴만 취소되고 부모에게는 영향을 주지 않습니다.
- `withTimeoutOrNull` 은 같은 역할을 하되 시간이 지체되더라도 예외를 던지지 않습니다. 만약 시간이 지연되면 람다가 취소되고 null 을 반환합니다.

## 코루틴 스코프 함수 연결하기
- 서로 다른 코루틴 스코프 함수의 두 가지 기능이 모두 필요하다면, 코루틴 스코프 함수에서 다른 기능을 가지는 코루틴 스코프 함수를 호출합니다.
```
suspend fun calculate(): User =
	withContext(Dispatchers.Default) {
		withTimeoutOrNull(1000) {
			// do sth
		}
	}
```

### 추가적인 연산
- 작업을 수행하는 도중에 추가적인 연산을 수행하는 경우, 불필요한지 확인할 것.
	- ex. 동일한 스코프에서 launch 를 호출하는 방법
- 취소
	- 어디까지 취소가 전파되어야 하는지 확인할 것.
	- ex. 로깅을 위해 로직이 취소되어선 안됨.
- ❓ coroutine 으로 command 작업도 하고 있는지?
	- 만약 한다면, 중도 실패했을때 롤백이나 보상로직을 어떻게 구현하고 있는지?
- **따라서 추가적인 연산이 있을 경우, 다른 스코프에서 시작하는 편이 낫다.**
---
# 12장. 디스패처
- 디스패처를 이용해 코루틴이 실행되어야 할 스레드(또는 스레드 풀)을 결정할 수 있다.
	- RxJava 의 스케줄러와 비슷한 개념
- 코틀린 코루틴에서 코루틴이 어떤 스레드에서 실행될지 정하는 것은 CoroutineContext 이다.
## Dispatchers.Default
- 코드가 실행되는 컴퓨터의 CPU 개수와 동일한 수 (최소 두 개 이상)의 스레드 풀을 가지고 있다.
- 같은 시간에 특정 수 이상의 스레드를 사용하지 못하게 제한할 수 있다.
```
@OptIn(ExperimentalCoroutinesApi::class)  
suspend fun main() = coroutineScope(  
) {  
    val dis = Dispatchers.Default.limitedParallelism(3)  
    repeat(1000) {  
        launch(dis) {  
            List(1000) { Random.nextInt() }.maxOrNull()  
  
            val threadName = Thread.currentThread().name  
            println("Running on thread $threadName") // ~ 10개  
        }  
    }  
}
```

## 메인 디스패처
- 메인 스레드에서 코루틴을 실행하려면 Dispatchers.Main 를 사용하면 됩니다.
- 단, 메인 스레드가 블로킹되면 전체 애플리케이션이 멈춰버리므로 주의해야합니다.
- 주로 안드로이드, UI 상호작용하는 유일한 스레드이므로 클라이언트에서 사용합니다.
### IO 디스패처
- 시간이 오래걸리는 I/O 작업이나 블로킹 함수가 있는 라이브러리가 필요할 때 사용.
- 같은 시간에 사용할 수 있는 스레드 수를 제한한 디스패치가 필요.
	- IO 디스패처의 경우, 64개 (더 많은 코어가 있다면 해당 코어 수)로 제한됩니다.
- 스레드의 한도는 디스패처마다 독립적이기 때문에 다른 디스패처의 스레드를 고갈시키는 경우는 없습니다.
### 커스텀 스레드 풀을 사용하는 IO 디스패처
- limitedParallism 을 가장 잘 활용하는 방법은 스레드를 블로킹하는 경우가 잦은 클래스에서 자기만의 한도를 가진 커스텀 디스패처를 정의하는 것.
- 한도는 정해진 답은 없음. p.153 예시
### 정해진 수의 스레드 풀을 가진 디스패처
- p.153
- ExcutorService 나 Executor 인터페이스를 구현하며, asCoroutineDispatcher 함수를 이용해 디스패처로 변형하는 것도 가능.
- 단, asCoroutineDispatcher 함수로 만들어진 디스패처의 큰 문제점은 close 함수로 닫혀야한다는 것. 스레드 누수를 일으키는 경우가 있다. 주의!!!!
### 싱글스레드로 제한된 디스패처
- Excutors 를 사용하여 싱그ㅡㄹ스레드 디스패처를 만들 수 있음.
	- 주의!!! 더이상 사용되지 않을 때는 스레드를 반드시 닫아야함.
- Dispatchers.Default 나 병렬처리를 1로 제한한 Dispacters.IO 도 많이 사용함.
### 프로젝트 룸의 가상 스레드 사용하기
- JVM 가상스레드를 사용하고, 블로킹하는 방식
	- 블로킹되므로, 다른데는 별로 필요없지만 Dispacters.IO 대신 사용할수있음
	- R2DBC 를 사용하면 별로 필요없을듯????
### 제한받지 않는 디스패처
- Dispacters.Unconfined
- 시작한 스레드에서 실행됨. 재개되었을때는 재개한 스레드에서 실행됨.
- 단위 테스트를 할 때 유용. 시간을 동기화할 수 있음.
	- 근데 runTest 를 사용하면 따로 이런 방법은 필요없음.
- 스레드 스위칭을 일으키지 않으므로, 제한받지 않는 디스패처의 비용이 가장 저렴함.
### 컨티뉴에이션 인터셉터
- 티스패칭은 코틀린 언어에서 지원하는 컨티뉴에이션 인터셉션을 기반으로 작동한다.
- ContinuationInterceptor 라는 코루틴 컨텍스트는 코루틴이 중단되면 컨티뉴에이션 객체를 수정하고 포장한다.
	- 포장한다는 것은 다양한 방법으로 제어할 수 있다는 것.
### 작업 종류에 따른 각 디스패처 성능 비교
- 그냥 중단할 경우에는 사용하고 있는 스레드 수가 얼마나 많은지 문제가 되지 않는다.
- 블로킹할 경우, 스레드 수가 많을 수록 모든 코루틴이 종료되는 시간이 많아진다.
- CPU 집약연산에서는 Dispatchers.Default 가 가장 좋은 선택지
- 메모리 집약적인 연산을 처리한다면 더 많은 스레드를 사용하는 것이 좋다.
---
# 13장. 코루틴 스코프 만들기
## coroutineScope 팩토리 함수
- CoroutineScope 인터페이스를 구현한 클래스를 만들고 내부에서 코루틴 빌더를 직접 호출할 수 있다.
	- cancel, ensureActive 같은 다른 CoroutineScope 의 메서드를 직접 호출하면 문제가 발생할 수 있다.
	- 코루틴 스코프 인스턴스를 프로퍼티로 가지고 있다가 코루틴 빌더를 호출할때 사용하는 방법이 선호됨.
- CoroutineScope 팩토리 함수를 사용하면 쉽게 만들 수 있음.
## 안드로이드에서 스코프 만들기
- skip
## 백엔드에서 코루틴 만들기
- 스프링 부튼느 컨트롤러 함수가 suspend 로 선언되는 것을 허용하고 있다.
- [Ktor](https://ktor.io/) 에서 모든 핸들러는 기본적으로 중단함수
- 주로 필요한 작업들
	- 스레드 풀을 가진 커스텀 디스패처
	- 각각의 코루틴을 독립적으로 만들어주는 SupervisorJob
	- 적절한 에러코드에 응답하고 데드레터를 보내거나 발생한 문제에 대해 로그를 남기는 CoroutineExceptionHandler
```
private val threadPool = Executors.newFixedThreadPool(10).asCoroutineDispatcher()
```
## 추가적인 호출을 위한 스코프 만들기
- 추가 연산을 위한 스코프를 종종만듭니다.
- `SupervisorScope` 를 사용하는 것으로 충분
```kotlin
val analyticsScope = CoroutineScope(SupervisorJob())
```
---
# 14. 공유 상태로 인한 문제
## 동기화 블로킹
- 자바에서 사용하는 synchronized 블록이나 동기화된 컬렉션을 사용해 해결할 수 있음
- 주의
	- synchronized 블록 내부에서 중단함수를 사용할 수 없음.
	- synchronized 블록에서 코루틴이 자기 차례를 기다릴 때, 스레드를 블로킹한다.
## 원자성
- AtomicInteger 등을 사용하기
- 좀더 복잡한 경우에는 한계가 있을 수 있음
## 싱글스레드로 제한된 디스패처
- 싱글스레드 디스패처 사용 가능.
	- 공유 상태와 관련된 대부분의 문제를 해결할 수 있음.
- 코스 그레인드 스레드 한정 coarse-grained thred confinement
	- 디스패처를 싱글스레드로 제한한 withContext 로 전체함수를 래핑하는 방식
	- 함수 전체에서 멀티 스레딩의 이점을 누리지 못하는 문제가 있음.
- 파인 그레인드 스레드 한정 fine-grained thred confinement
	- 상태를 변경하는 구문들만 래핑
	- 좀 번거롭지만, 크리티컬 섹션이 아닌 부분이 블로킹되거나 CPU 집약적인 경우에는 더 나은 성능을 제공함.
## 뮤텍스
- 가장 인기있는 방식
- lock, unlock 하며 동작함.
- 주의
	- 중간에 예외가 발생할 경우, unlock 이 호출되지 않음 (데드락)-> withLock 사용
- `withLock` 을 사용하는 것이 좋음.
	- finally 블록에서 unlock 을 호출함.
- 스레드가 블로킹하는 대신 코루틴을 중단시킨다.
- 코루틴이 락을 두번 통과할 수 없으므로, 주의
- 코루틴이 중단되었을 때 뮤텍스를 풀 수 없으므로 주의.
- 따라서 전체함수를 뮤텍스로 래핑하는것은 지양해야한다.

## 세마포어
- 뮤텍스와 비슷한 방식이지만 둘 이상 접근할 수 있음.
- acquire, release, withPermit 함수를 가지고 있음.
- 공유 상태로 인해 생기는 문제를 해결할 수는 없지만, 동시 요청을 처리하는 수를 제한할 때 사용할 수 있음.
---
# 15장. 코틀린 코루틴 테스트하기
- runBlocking 사용. assert 를 지원하는 도구만 사용해도 충분함.
## 시간의존성 테스트하기
- delay 를 사용하여 가짜 함수를 지연시킬 수 있음.
### TestCoroutineScheduler와 StandardTestDispatcher
- TestCoroutineScheduler 는 delay 를 가상 시간동안 실행하여 실제 시간이 흘러간 상황과 동일하게 작동하기 때문에 정해진 시간만큼 기다리지않도록 변경가능.
	- `advanceTimeBy`
- StandardTestDispatcher 는 TestCoroutineScheduler 를 만들기 때문에 명시적으로 만들지 않아도 된다.
## runTest
- 가상으로 시간이 흘러가도록 할 수 있음.
- 