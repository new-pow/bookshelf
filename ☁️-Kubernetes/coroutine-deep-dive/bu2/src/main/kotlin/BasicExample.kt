import kotlinx.coroutines.*

/**
 * 코루틴 라이브러리를 사용한 첫 번째 예시
 * kotlinx-coroutines-core 라이브러리의 기본 사용법
 */

fun main() = runBlocking {
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

/**
 * launch: Job을 반환하며, 결과값을 받을 수 없음
 */
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

/**
 * async: Deferred를 반환하며, await()로 결과값을 받을 수 있음
 */
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

/**
 * 순차 실행: 한 작업이 끝나고 다음 작업 시작
 */
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

/**
 * 병렬 실행: 여러 작업을 동시에 시작
 */
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

/**
 * 비동기 작업 시뮬레이션
 */
suspend fun fetchData(name: String): String {
    delay(1000) // 1초 대기 (네트워크 요청 시뮬레이션)
    return "[$name] 완료"
}
