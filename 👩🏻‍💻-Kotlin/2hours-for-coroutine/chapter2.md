# Coroutine builder

```
runBlocking {

}
```
- 새로운 코루틴을 만들고 루틴 세계와 코루틴 세계를 이어줍니다.
- 이 블록이 모두 처리될 때까지 block 됩니다.
- 테스트 등 특정 코루틴에서만 사용하는 것이 좋습니다.

---

```
val job = launch {
	delay() // 특정 시간만큼 멈추고 다른 코루틴으로 넘기기
	printWithThread("Hello")
}
```

```
val job = launch(start = CoroutineStart.LAZY) {
	delay() // 특정 시간만큼 멈추고 다른 코루틴으로 넘기기
	printWithThread("Hello")
}
job.start() // 이때 코루틴을 시작합니다.
job.cancel() // 종료
job.join() // 종료될때까지 대기
```
- 새로운 코루틴을 시작합니다.
- 반환값이 없는 코드를 실행합니다.
- `job` 을 반환받는데, 이것으로 코루틴을 제어할 수 있습니다.
---
```
val defer = async {
	getApiCall()
}

defer.await() // 연산 반환값을 가져옵니다.
```
- 새로운 코루틴을 시작합니다.
- defer를 통해 job 처럼 동일하게 코루틴을 제어할 수 있습니다.
- `.await()` 를 사용하여 연산 반환값을 가져옵니다.
- callback을 사용하지 않고 동기 방식으로 코드를 작성할 수 있습니다. 
- 주의할 점
	- `CoroutineStart.LAZY` 옵션과 함께 `await()` 를 사용할 경우, 연산 결과를 계속 기다리게됩니다. 그러므로 그 전에 `start()` 해주어야 합니다.

# 코루틴의 취소
- 취소하는 것이 중요한 이유
	- 필요하지 않은 코루틴을 적절히 취소해 자원을 아껴야 한다.
- `cancel()` 함수 사용하지만, 코루틴도 이를 협조해줘야합니다.

## 취소하는 방법
1. delay(), yield() 같은 coroutines 패키지의 suspend 함수를 사용해야합니다.
	- 이를 사용할 때 자동으로 취소여부를 체크합니다. 없으면 취소 여부 체크를 모든 동작이 완료되야 취소를 받아들입니다.
	- `CancellationException` 을 던지는 방식
1. 코루틴이 스스로 본인의 상태를 확인해 취소 요청을 받았으면 `CancellationException`을 던집니다.

```
lanch(Dispatchers.Default) { // 다른 스레드에서 동작하도록 설정
	if (isActive) {
		throw CancellationException()
	}
}

job.cancel() // main 스레드에서 동작
```

# 예외처리
- 새로운 root 코루틴을 만들고 싶을 때는? `coroutineScope`
```kotlin
CoroutineScope(Dispatchers.Default).launch { // 새로운 root 코루틴
	throw IllegalArgumentException()
} // 예외 바로 발생. 출력 후 코루틴 종료

val job = CoroutineScope(Dispatchers.Default).async { // 새로운 root 코루틴
	throw IllegalArgumentException()
}

job.await() // 이 때 예외 발생.
```

- `async` 에서 예외가 발생할 경우?
```kotlin
val job = async {
	throw IllegalArgumentException()
} // 바로 예외 발생
```

왜 이런 일이 발생할까요?
- 자식 코루틴의 예외는 부모에게 전파되기 때문.
	- 부모의 예외처리 성격을 띈다.
- root coroutine에서 예외가 전파될 것이 없어서 에러가 바로 출력되지 않음.

만약 자식 코루틴의 예외를 부모에게 전파하고 싶지 않다면?
```kotlin
async(SupervisorJob()) { // 자식 코루틴의 예외를 전파하지 않는다.
	throw IllegalArgumentException()
}

// await 통해 예외를 출력할 수 있긴함.
```

## 예외를 다루는 방법
### try-catch-finally
- 