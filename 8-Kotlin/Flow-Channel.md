# Kotlin Coroutine: Flow와 Channel

## 목차
1. [개요](#개요)
2. [원리 이해](#원리-이해)
3. [Channel: 동기적 통신](#channel-동기적-통신)
4. [Flow: 비동기적 데이터 스트림](#flow-비동기적-데이터-스트림)
5. [Channel vs Flow 비교](#channel-vs-flow-비교)
6. [실전 예제](#실전-예제)
7. [메시지 버스에서의 활용](#메시지-버스에서의-활용)

---

## 개요

Kotlin의 코루틴에서 **데이터 통신**을 위한 두 가지 주요 메커니즘:

- **Channel**: 두 코루틴 간 직접적인 동기 통신
- **Flow**: 비동기 데이터 스트림, 콜드(Cold) 기반

둘 다 코루틴 간 데이터 전달을 가능하게 하지만, 구현 방식과 사용 패턴이 다릅니다.

---

## 원리 이해

### Channel의 내부 동작

**Channel**은 **선입선출(FIFO) 큐** 기반의 동기 통신 메커니즘입니다:

1. **생산자-소비자 대기 메커니즘**
   - 생산자가 데이터를 `send()`하면 버퍼 상태를 확인
   - 버퍼가 찬 경우: 생산자 코루틴은 일시 중단(suspend)
   - 소비자가 데이터를 `receive()`하면 버퍼에서 제거
   - 버퍼에 여유가 생기면 대기 중인 생산자 재개(resume)

2. **Hot Stream 특성**
   - Channel 생성 시점부터 데이터 생성 시작
   - 구독 여부와 상관없이 메모리에 누적
   - 이미 방출된 데이터는 다시 받을 수 없음

3. **메모리 모델**
   ```
   [생산자] → [Buffer] → [소비자]
             ↓
        대기 가능 (동기 점)
   ```

### Flow의 내부 동작

**Flow**는 **함수형 람다 기반**의 비동기 데이터 스트림입니다:

1. **Lazy Evaluation (지연 평가)**
   - Flow 정의: 코드 실행 안 함 (선언만)
   - `collect()` 호출: 구독 시점에 실행 시작
   - 각 연산자는 이전 연산자로부터 값을 받을 때까지 대기

2. **Cold Stream 특성**
   - 각 구독자마다 독립적으로 flow가 실행됨
   - 같은 Flow를 여러 곳에서 구독하면 각각 별도 실행
   - 메모리 효율적 (값이 필요할 때만 생성)

3. **백프레셔 지원**
   - 소비자가 느리면 생산자는 자동으로 속도 조절
   - 메모리 누적 방지

4. **스트림 구조**
   ```
   Flow 정의 (미실행)
      ↓ collect() 호출
   Flow 실행 시작
      ↓ emit()
   [연산자 체인] (map → filter → ...)
      ↓
   [소비자]
   ```

### Hot vs Cold Stream 비교

| 측면 | Hot Stream (Channel) | Cold Stream (Flow) |
|------|-----|------|
| **실행 시점** | 생성과 동시에 시작 | 구독할 때 시작 |
| **여러 구독자** | 같은 데이터 공유 | 각자 독립 실행 |
| **메모리** | 버퍼에 누적 | 필요시에만 생성 |
| **구독 여부** | 관계없이 진행 | 구독 시작 후 진행 |
| **예시** | 라이브 방송 | 비디오 온디맨드 |

---

## Channel: 동기적 통신

### Channel이란?

**Channel**은 두 개 이상의 코루틴 간에 **값을 전달하고 수신**하는 **동기적** 통신 채널입니다.

- 송신자(Producer)와 수신자(Consumer) 간의 직접 통신
- `send()`로 데이터 전송
- `receive()`로 데이터 수신
- Hot stream - 즉시 실행되고, 구독 여부와 상관없이 데이터 생성

### 기본 구조

```kotlin
// Channel 생성
val channel = Channel<Int>()

// 송신자 (Producer)
launch {
    repeat(5) { index ->
        channel.send(index)  // 값 전송
    }
}

// 수신자 (Consumer)
launch {
    repeat(5) {
        val value = channel.receive()  // 값 수신
        println("Received: $value")
    }
}
```

### 실제 프로젝트 예제: Example226.kt

```kotlin
suspend fun main(): Unit = coroutineScope {
    val channel = Channel<Int>() // 정수형 채널 생성

    // Producer 코루틴
    launch {
        repeat(5) { index ->
            delay(1000)  // 1초마다 전송
            println("Producting next one")
            channel.send(index) // 채널에 값 전송
        }
    }

    // Consumer 코루틴
    launch {
        repeat(5) {
            val value = channel.receive() // 채널에서 값 수신
            println("Received $value")
        }
    }
}
```

**실행 흐름:**
```
Producting next one
Received 0
Producting next one
Received 1
Producting next one
Received 2
...
```

### Channel의 특징

| 특징 | 설명 |
|------|------|
| **통신 방식** | 동기적 (Synchronous) |
| **생성 시점** | Hot - 즉시 실행 |
| **버퍼 옵션** | Buffer를 설정 가능 |
| **닫기** | `channel.close()` 필수 |
| **에러 처리** | 예외 전파 가능 |

### Channel의 종류

```kotlin
// 1. Unlimited Buffer Channel
val channel = Channel<Int>(Channel.UNLIMITED)

// 2. Buffered Channel
val channel = Channel<Int>(capacity = 10)

// 3. Rendezvous Channel (기본값, 버퍼 없음)
val channel = Channel<Int>()

// 4. Conflated Channel (최신 값만 유지)
val channel = Channel<Int>(Channel.CONFLATED)
```

### 실제 사용 예제

```kotlin
suspend fun main(): Unit = coroutineScope {
    // 무한 채널 (버퍼 제한 없음)
    val channel = Channel<String>(Channel.UNLIMITED)

    // Producer: 메시지 계속 전송
    launch {
        repeat(3) { i ->
            channel.send("Message $i")
            println("Sent: Message $i")
        }
        channel.close()  // 채널 닫기
    }

    // Consumer: 모든 메시지 받기
    launch {
        for (message in channel) {  // for-in 루프로 순회
            println("Received: $message")
        }
    }
}
```

---

## Flow: 비동기적 데이터 스트림

### Flow란?

**Flow**는 **비동기 데이터 스트림**을 나타내는 타입으로:

- 여러 값을 **순차적으로** 방출(emit)
- **Cold stream** - 구독할 때만 실행 (Lazy evaluation)
- 백프레셔(Backpressure) 지원
- 간단한 에러 처리

### Flow의 특징

| 특징 | 설명 |
|------|------|
| **통신 방식** | 비동기적 (Asynchronous) |
| **생성 시점** | Cold - 수집할 때 실행 |
| **버퍼링** | 기본적으로 버퍼링 없음 |
| **마지막 값** | 완료(complete) 이벤트 |
| **에러 처리** | try-catch 또는 catch 연산자 |

### Flow 기본 구조

```kotlin
// Flow 생성
val flow = flow<Int> {
    repeat(5) { index ->
        delay(1000)
        emit(index)  // 값 방출
    }
}

// Flow 수집
flow.collect { value ->
    println("Received: $value")
}
```

### Flow 예제 코드

```kotlin
fun numberFlow(): Flow<Int> = flow {
    repeat(5) { index ->
        delay(1000)  // 비동기 작업 시뮬레이션
        println("Emitting: $index")
        emit(index)  // 값 방출
    }
}

suspend fun main() {
    numberFlow()
        .map { it * 2 }              // 변환 연산자
        .filter { it > 2 }            // 필터링 연산자
        .collect { value ->
            println("Received: $value")
        }
}
```

**실행 흐름:**
```
Emitting: 0
Emitting: 1
Received: 2
Emitting: 2
Received: 4
Emitting: 3
Received: 6
Emitting: 4
Received: 8
```

### Flow 주요 연산자

#### 변환 연산자

```kotlin
// map: 각 값을 변환
flow.map { it * 2 }

// filter: 조건에 맞는 값만 통과
flow.filter { it > 10 }

// flatMap: Flow를 평탄화
flow.flatMap { value ->
    flow {
        emit(value)
        emit(value * 2)
    }
}
```

#### 수집 연산자

```kotlin
// collect: 모든 값을 순회
flow.collect { value -> println(value) }

// first: 첫 번째 값 받기
val first = flow.first()

// last: 마지막 값 받기
val last = flow.last()

// toList: 모든 값을 리스트로 변환
val list = flow.toList()
```

#### 에러 처리

```kotlin
flow
    .catch { exception ->
        println("Error occurred: ${exception.message}")
    }
    .collect { value ->
        println("Received: $value")
    }
```

#### 완료 처리

```kotlin
flow
    .onCompletion { exception ->
        if (exception == null) {
            println("Flow completed successfully")
        } else {
            println("Flow failed: ${exception.message}")
        }
    }
    .collect { value ->
        println("Received: $value")
    }
```

### StateFlow와 SharedFlow

Flow의 특수한 형태들:

#### StateFlow
최신 상태를 유지하며 새 구독자에게도 즉시 방출

```kotlin
val stateFlow = MutableStateFlow<Int>(0)

stateFlow.value = 1      // 상태 업데이트

stateFlow.collect { state ->
    println("State: $state")
}
```

#### SharedFlow
여러 구독자에게 값을 공유

```kotlin
val sharedFlow = MutableSharedFlow<Int>()

// 값 방출
sharedFlow.emit(1)

// 구독
sharedFlow.collect { value ->
    println("Received: $value")
}
```

---

## Channel vs Flow 비교

### 핵심 차이점

| 구분 | Channel | Flow |
|-----|---------|------|
| **스트림 타입** | Hot (즉시 실행) | Cold (lazy) |
| **통신 방식** | 동기 (Synchronous) | 비동기 (Asynchronous) |
| **백프레셔** | 자동 제어 | 지원함 |
| **닫기 처리** | `close()` 필수 | 자동 처리 |
| **버퍼** | 설정 가능 | 기본 없음 |
| **에러 처리** | 예외 전파 | catch 연산자 |
| **메모리** | 값을 메모리에 유지 | 필요한 만큼만 처리 |

### 언제 뭘 사용할까?

#### Channel을 사용해야 할 때:
- ✅ 여러 코루틴 간 **직접 통신**이 필요할 때
- ✅ 송신자와 수신자가 **동시에 실행**되어야 할 때
- ✅ **버퍼가 필요**한 경우
- ✅ **즉시 실행** 필요 (Hot stream)
- ✅ Producer-Consumer 패턴

#### Flow를 사용해야 할 때:
- ✅ **데이터 스트림** 처리 (예: API 호출, 센서 데이터)
- ✅ **여러 연산자** 체이닝이 필요할 때
- ✅ **백프레셔** 처리 필요
- ✅ **메모리 효율**이 중요할 때
- ✅ **비동기 데이터 변환**
- ✅ **리액티브 프로그래밍** 패턴

---

## 실전 예제

### 예제 1: Channel로 작업 분배하기

```kotlin
suspend fun main(): Unit = coroutineScope {
    // 작업 채널
    val jobChannel = Channel<String>()

    // Producer: 작업 생성
    launch {
        repeat(3) { i ->
            val job = "Task-$i"
            println("Producing: $job")
            jobChannel.send(job)
            delay(500)
        }
        jobChannel.close()
    }

    // Consumer 1: 작업 처리
    launch {
        for (job in jobChannel) {
            println("Worker-1 processing: $job")
            delay(1000)
        }
    }
}
```

### 예제 2: Flow로 데이터 변환하기

```kotlin
fun getUserDataFlow(userId: Int): Flow<String> = flow {
    // 1. 사용자 정보 로드
    delay(1000)
    emit("User#$userId info loaded")

    // 2. 사용자 게시물 로드
    delay(1000)
    emit("Posts for User#$userId loaded")

    // 3. 사용자 댓글 로드
    delay(1000)
    emit("Comments for User#$userId loaded")
}

suspend fun main() {
    getUserDataFlow(1)
        .map { it.uppercase() }
        .filter { it.contains("LOADED") }
        .onEach { println("Processing: $it") }
        .collect { result ->
            println("Final: $result")
        }
}
```

### 예제 3: 실시간 데이터 업데이트 (StateFlow)

```kotlin
class TemperatureSensor {
    private val _temperature = MutableStateFlow<Float>(20f)
    val temperature: StateFlow<Float> = _temperature.asStateFlow()

    suspend fun startMonitoring() {
        repeat(5) {
            delay(1000)
            _temperature.value = 20f + it * 0.5f
            println("Temperature updated: ${_temperature.value}°C")
        }
    }
}

suspend fun main() = coroutineScope {
    val sensor = TemperatureSensor()

    launch {
        sensor.startMonitoring()
    }

    launch {
        sensor.temperature.collect { temp ->
            println("Current temp: $temp°C")
        }
    }
}
```

### 예제 4: 에러 처리

```kotlin
fun unreliableFlow(): Flow<Int> = flow {
    repeat(5) { i ->
        if (i == 3) {
            throw Exception("Error at index $i")
        }
        emit(i)
    }
}

suspend fun main() {
    unreliableFlow()
        .map { it * 2 }
        .catch { exception ->
            println("Caught error: ${exception.message}")
            emit(-1)  // 에러 발생 시 대체값 방출
        }
        .collect { value ->
            println("Value: $value")
        }
}
```

---

## 결론

### 핵심 정리

1. **Channel**: 코루틴 간 동기 통신
   - Hot stream, 즉시 실행
   - 버퍼 설정 가능
   - Producer-Consumer 패턴에 적합

2. **Flow**: 비동기 데이터 스트림
   - Cold stream, Lazy evaluation
   - 풍부한 연산자 지원
   - 백프레셔 지원
   - 리액티브 프로그래밍에 적합

3. **실전 사용**
   - Channel: 작업 분배, 코루틴 간 메시지 전송
   - Flow: 데이터 변환, API 응답 처리, 실시간 업데이트

### 성능 고려사항

- **Channel**: 동기 방식이므로 빠른 응답이 필요할 때
- **Flow**: 비동기 처리로 인한 오버헤드가 있지만 메모리 효율적

### 학습 권장 순서

1. 코루틴 기본 개념 이해
2. Channel로 코루틴 간 통신 학습
3. Flow와 연산자 학습
4. StateFlow/SharedFlow로 상태 관리
5. 실제 프로젝트에서 적용

---

## 메시지 버스에서의 활용

### 아키텍처 개요

### 메시지 흐름 아키텍처

```
┌─────────────────────────────────────────┐
│      BeBus 메시지 버스                  │
├─────────────────────────────────────────┤
│  1. Message 송신                        │
│     ↓                                   │
│  2. Store에 저장 (Event Sourcing)      │
│     ↓                                   │
│  3. 동기 리스너 처리 (sync)            │
│     ↓                                   │
│  4. Producer → Channel으로 전송        │
│     ↓                                   │
│  5. Consumer가 Channel에서 수신        │
│     ↓                                   │
│  6. 비동기 리스너 처리 (async)         │
└─────────────────────────────────────────┘
```

### Channel 활용 방식

**LocalBroker**에서 토픽(Topic)별로 독립적인 Channel을 관리합니다:

1. **토픽 채널 생성**
   - 각 토픽마다 하나의 Channel 생성
   - 타입: `Channel<Message>`
   - Rendezvous Channel (버퍼 없음, 동기 방식)

2. **메시지 전송 과정**
   - Producer가 Channel의 `send()` 메서드 호출
   - Message를 Channel에 전달
   - 동기적으로 버퍼에 저장됨

3. **메시지 수신 과정**
   - Consumer가 `consumeEach`로 Channel 순회
   - 한 번에 하나의 메시지 처리
   - 연쇄적으로 모든 메시지 처리

### 비동기 처리 전략

 **이중 리스너 모델** 사용:

| 리스너 타입 | 실행 시점 | 용도 |
|-----------|---------|------|
| **동기 리스너** | 메시지 송신 직후 즉시 | 유효성 검증, 긴급 처리 |
| **비동기 리스너** | 별도 스레드 풀에서 처리 | 시간이 걸리는 작업, 부가 기능 |

### 스레드 풀 관리

**고정 크기 스레드 풀** 사용:

```
스레드 풀 크기 계산: max(4, CPU 코어수 / 2) 이상 64 이하

예시:
- 4 코어 CPU: 최소 4개 스레드
- 8 코어 CPU: 4개 스레드
- 16 코어 CPU: 8개 스레드
- 128 코어 이상: 64개 스레드
```

### Channel 사용이 적합한 이유

1. **Hot Stream 필요**
   - 메시지는 수신자 여부와 관계없이 처리되어야 함
   - 구독하지 않은 메시지는 버려질 수 있음

2. **즉시 동기 처리**
   - Producer-Consumer 간 즉각적인 데이터 교환
   - 동기 리스너의 빠른 응답 필요

3. **버퍼 제어**
   - Rendezvous Channel로 과도한 메모리 누적 방지
   - 생산자-소비자 속도 자동 조절

4. **구조적 단순성**
   - 중앙집중식 메시지 큐 구현
   - 명확한 송수신 지점

### 실제 사용 패턴

#### 패턴 1: 이벤트 발행

메시지 버스에 이벤트를 송신하면:

1. 이벤트는 먼저 Store에 저장됨 (감사 추적)
2. 동기 리스너들이 즉시 처리됨 (검증 등)
3. Channel을 통해 비동기 리스너로 전달됨
4. 별도 스레드에서 비동기 처리 실행

#### 패턴 2: 비동기 리스너 등록

두 가지 리스너 모드를 지원:

- **비동기 리스너** (기본값): 별도 스레드에서 실행, 시간이 걸리는 작업 (메일, DB 저장)
- **동기 리스너**: 메시지 송신 직후 즉시 실행, 빠른 검증 작업

#### 패턴 3: 타입 기반 라우팅

리스너 라우터가 메시지 타입 확인 후 매칭:

- **정확한 타입 매칭**: 구체적 메시지 타입에 등록된 리스너
- **상위 타입 매칭**: 다형성으로 상위 타입 리스너도 호출

### Channel vs Flow 선택 관점

Flow 대신 Channel을 선택한 이유:

| 요구사항 | Channel | Flow |
|---------|---------|------|
| **즉시 처리** | ✅ 필수 | ❌ 지연 평가 |
| **메시지 보관** | ✅ 버퍼 제어 | ❌ 기본 버퍼 없음 |
| **Producer-Consumer** | ✅ 최적화 | ❌ 단방향 흐름 |
| **메모리 효율** | ⚠️ 버퍼 크기 제어 가능 | ✅ 필요시만 생성 |
| **연산자 체이닝** | ❌ 제한적 | ✅ 풍부 |

### 확장 가능성

Flow를 추가로 활용할 수 있는 부분:

1. **리스너 체이닝**
   - 비동기 리스너에서 Flow 사용
   - 여러 처리 단계를 명확하게 구조화

2. **메시지 변환**
   - Envelope → Message 추출
   - map, filter 등 연산자로 데이터 정제

3. **결과 수집**
   - 여러 리스너 결과 모음
   - 최종 결과 처리 (logging, monitoring)

---

## 참고 자료

- [Kotlin Coroutines Flow Documentation](https://kotlinlang.org/api/kotlinx.coroutines/kotlinx-coroutines-core/kotlinx.coroutines.flow/)
- [Kotlin Coroutines Channel Documentation](https://kotlinlang.org/api/kotlinx.coroutines/kotlinx-coroutines-core/kotlinx.coroutines.channels/)
- Kotlin Coroutines: Deep Dive (프로젝트)