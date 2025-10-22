import kotlin.coroutines.Continuation
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

/**
 * example404: 두 가지 실행 경로 비교
 * Non-suspending path vs Suspending path
 */
fun main404() {
    println("=== Example404: Suspend 함수의 두 가지 실행 경로 ===\n")

    println("[경로 1] Non-suspending path (캐시에서 바로 읽기)")
    println("─".repeat(60))
    val cache1 = mutableMapOf("user1" to "철수")
    val cont1 = MyContinuationV4("경로1")
    fetchDataV4(cache1, "user1", cont1)
    println()

    println("[경로 2] Suspending path (네트워크 요청)")
    println("─".repeat(60))
    val cache2 = mutableMapOf<String, String>()
    val cont2 = MyContinuationV4("경로2")
    fetchDataV4(cache2, "user2", cont2)
    println("   → 비동기 작업이 배경에서 진행 중...")
    Thread.sleep(2000)
    println()
}

fun fetchDataV4(cache: Map<String, String>, key: String, cont: MyContinuationV4) {
    printStackV4("[fetchDataV4] 호출 시작")

    when(cont.label) {
        0 -> {
            println("   상태 0: 데이터 조회")

            val cachedValue = cache[key]

            if (cachedValue != null) {
                println("   ✓ [Non-suspending path] 캐시에서 찾음: $cachedValue")
                println("     - continuation을 호출하지 않음")
                println("     - 중단 마커 반환 안 함")
                println("     - 콜 스택이 unwound되지 않음")
                cont.result = cachedValue
                printStackV4("[fetchDataV4] 반환 (중단 없음)")
                return
            }

            println("   ✗ [Suspending path] 캐시에 없음 → 네트워크 요청")
            cont.label = 1
            println("   상태를 1로 변경")

            fetchFromNetworkV4(key, cont)

            println("   !! 이 줄은 처음에는 출력되지 않음 !!")
        }

        1 -> {
            println("\n   [재개] 상태 1: 비동기 작업 완료")
            val result = cont.result
            println("   ✓ 결과 받음: $result")
            printStackV4("[fetchDataV4] 반환 (비동기 완료)")
        }
    }
}

fun fetchFromNetworkV4(key: String, cont: MyContinuationV4) {
    println("   → fetchFromNetworkV4() 호출")
    println("     - COROUTINE_SUSPENDED 반환")
    println("     - 콜 스택이 unwound됨")

    Thread {
        println("\n   [비동기 배경 작업]")
        println("   → 네트워크 요청 중... (key=$key)")
        Thread.sleep(1500)

        val result = when(key) {
            "user1" -> "User(id=1, name='철수')"
            "user2" -> "User(id=2, name='영희')"
            else -> "Unknown"
        }

        println("   → 응답 받음: $result")
        println("   → Continuation.resumeWith() 호출")
        cont.resumeWith(Result.success(result))
    }.start()
}

fun printStackV4(message: String) {
    println("$message [스택 깊이: ${Thread.currentThread().stackTrace.size - 4}]")
}

class MyContinuationV4(
    val name: String = "",
    override val context: CoroutineContext = EmptyCoroutineContext
) : Continuation<String> {

    var label = 0
    var result = ""

    override fun resumeWith(result: Result<String>) {
        this.result = result.getOrThrow()
        println("   [Continuation.resumeWith()] [$name]")
        println("   → 결과 저장: ${this.result}")
        println("   → fetchDataV4() 상태 1로 재개")

        fetchDataV4(emptyMap(), "", this)
    }
}
