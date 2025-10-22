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
