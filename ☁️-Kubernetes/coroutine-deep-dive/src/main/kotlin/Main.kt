import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

/**
 * 코루틴 학습 예시 메인 실행기
 *
 * 사용법:
 * - gradle run                         (기본값: main - 메뉴)
 * - gradle run --args="main"           (메뉴 선택)
 * - gradle run --args="basic"          (kotlinx-coroutines 기본 사용법)
 * - gradle run --args="advanced"       (Flow 고급 예시)
 * - gradle run --args="example401"     (콜 스택 깊이 추적)
 * - gradle run --args="example402"     (콜 스택 unwinding)
 * - gradle run --args="example403"     (중단 마커 반환)
 * - gradle run --args="example404"     (두 가지 실행 경로)
 */

fun main(args: Array<String>) {
    val example = if (args.isNotEmpty()) args[0].lowercase() else "main"

    when(example) {
        "basic" -> runBasicExample()
        "advanced" -> runAdvancedExample()
        "example401" -> main401()
        "example402" -> main402()
        "example403" -> main403()
        "example404" -> main404()
        "main", "" -> showMenu()
        else -> {
            showMenu()
        }
    }
}

fun showMenu() {
    println("=== 코루틴 학습 예시 ===\n")
    println("[라이브러리 사용법]")
    println("  gradle run --args=\"basic\"      - 기본 코루틴 (launch, async, 순차/병렬)")
    println("  gradle run --args=\"advanced\"   - Flow를 이용한 반응형 프로그래밍")
    println()
    println("[콜 스택 동작 원리]")
    println("  gradle run --args=\"example401\"  - 콜 스택 깊이 추적")
    println("  gradle run --args=\"example402\"  - 콜 스택 unwinding 과정")
    println("  gradle run --args=\"example403\"  - 중단 마커(COROUTINE_SUSPENDED) 반환")
    println("  gradle run --args=\"example404\"  - Non-suspending vs Suspending 경로")
    println()
}

fun runBasicExample() {
    kotlinx.coroutines.runBlocking {
        println("=== kotlinx-coroutines 라이브러리 예시 ===\n")

        println("[1] launch - 결과를 반환하지 않는 코루틴")
        println("─".repeat(50))
        exampleLaunch()

        println("\n[2] async - 결과를 반환하는 코루틴")
        println("─".repeat(50))
        exampleAsync()

        println("\n[3] 순차 실행")
        println("─".repeat(50))
        exampleSequential()

        println("\n[4] 병렬 실행")
        println("─".repeat(50))
        exampleParallel()

        println("\n=== 모든 예시 완료 ===")
    }
}

fun runAdvancedExample() {
    kotlinx.coroutines.runBlocking {
        println("=== Flow를 이용한 반응형 프로그래밍 ===\n")

        println("[1] 기본 Flow")
        println("─".repeat(50))
        basicFlow()

        println("\n[2] Flow를 사용한 데이터 스트림")
        println("─".repeat(50))
        dataStreamFlow()

        println("\n[3] Exception handling in Flow")
        println("─".repeat(50))
        exceptionHandlingFlow()
    }
}

// ===== Basic Example 함수들 =====

suspend fun exampleLaunch() {
    coroutineScope {
        val job = launch {
            println("launch 코루틴 시작")
            delay(100)
            println("launch 코루틴 완료")
        }
        job.join()
    }
}

suspend fun exampleAsync() {
    coroutineScope {
        val deferred = async {
            println("async 코루틴 시작")
            delay(100)
            println("async 코루틴 완료")
            42
        }
        val result = deferred.await()
        println("결과값: $result")
    }
}

suspend fun exampleSequential() {
    val start = System.currentTimeMillis()

    println("작업 1 시작")
    val result1 = fetchData("data1")
    println("작업 1 완료: $result1")

    println("작업 2 시작")
    val result2 = fetchData("data2")
    println("작업 2 완료: $result2")

    val elapsed = System.currentTimeMillis() - start
    println("총 소요 시간: ${elapsed}ms")
}

suspend fun exampleParallel() {
    coroutineScope {
        val start = System.currentTimeMillis()

        println("모든 작업 시작")
        val result1 = async { fetchData("data1") }
        val result2 = async { fetchData("data2") }

        val data1 = result1.await()
        val data2 = result2.await()

        println("작업 1 결과: $data1")
        println("작업 2 결과: $data2")

        val elapsed = System.currentTimeMillis() - start
        println("총 소요 시간: ${elapsed}ms")
    }
}

suspend fun fetchData(name: String): String {
    delay(1000)
    return "[$name] 완료"
}

// ===== Advanced Example 함수들 =====

suspend fun basicFlow() {
    val numbers = flow {
        for (i in 1..3) {
            delay(100)
            emit(i)
        }
    }

    numbers.collect { value ->
        println("값: $value")
    }
}

suspend fun dataStreamFlow() {
    val userFlow = getUsersFlow()

    userFlow.collect { user ->
        println("사용자: $user")
    }
}

fun getUsersFlow(): Flow<String> = flow {
    println("사용자 1 로드 중...")
    delay(500)
    emit("User(id=1, name='철수')")

    println("사용자 2 로드 중...")
    delay(500)
    emit("User(id=2, name='영희')")

    println("사용자 3 로드 중...")
    delay(500)
    emit("User(id=3, name='민준')")
}

suspend fun exceptionHandlingFlow() {
    val flow = flow {
        for (i in 1..3) {
            if (i == 2) {
                throw Exception("Error at $i")
            }
            emit(i)
        }
    }

    try {
        flow.collect { value ->
            println("값: $value")
        }
    } catch (e: Exception) {
        println("예외 처리: ${e.message}")
    }
}
