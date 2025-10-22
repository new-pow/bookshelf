import kotlin.coroutines.Continuation
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

/**
 * 중단 마커(COROUTINE_SUSPENDED)가 반환되는 경우를 보여주는 예시
 *
 * COROUTINE_SUSPENDED란?
 * - suspend 함수가 실제로 중단될 때 반환되는 특수한 객체
 * - 콜 스택이 unwound되어 이전 호출자에게 제어가 반환될 때 사용
 * - 나중에 resume이 호출될 때까지 코루틴이 기다린다는 의미
 *
 * 두 가지 경우:
 * 1. 즉시 반환 (Immediately completed):
 *    - continuation을 resume하지 않고 바로 값을 반환
 *    - 중단이 발생하지 않음
 *
 * 2. 중단 후 비동기 처리 (Suspended):
 *    - COROUTINE_SUSPENDED를 반환
 *    - 나중에 continuation.resume()이 호출됨
 */

// 중단 마커 객체를 직접 사용하지 않고, Unit을 반환하는 suspend 함수를 시뮬레이션
object COROUTINE_SUSPENDED

fun main() {
    println("=== 중단 마커(COROUTINE_SUSPENDED)가 반환되는 경우 ===\n")

    println("[케이스 1] 즉시 완료되는 경우 (중단 마커 반환 안 함)")
    println("─".repeat(50))
    val cont1 = MyContinuationV3(name = "케이스1")
    immediatelyCompleted(cont1)
    println()

    println("[케이스 2] 비동기로 처리되는 경우 (중단 마커 반환)")
    println("─".repeat(50))
    val cont2 = MyContinuationV3(name = "케이스2")
    suspendedWithAsyncWork(cont2)
    println()
}

/**
 * 케이스 1: 즉시 완료 (suspend 함수가 중단되지 않음)
 * - continuation을 사용하지 않고 바로 반환
 * - 호출자가 즉시 결과를 받음
 */
fun immediatelyCompleted(cont: MyContinuationV3) {
    println("1. immediatelyCompleted() 호출 시작")
    println("   콜 스택: main → immediatelyCompleted")

    val result = "즉시 계산된 결과"
    println("2. 작업 완료 (중단 없음): $result")
    println("3. Continuation을 호출하지 않고 직접 반환")

    // 여기서 중단이 발생하지 않음
    // continuation.resume()을 호출하지 않음
    // suspend 함수이지만 실제로는 중단되지 않음 (non-suspending path)

    println("4. immediatelyCompleted() 반환 (중단 마커 반환 안 함)")
    println("   → 호출자가 즉시 제어를 받음\n")
}

/**
 * 케이스 2: 비동기 작업 (suspend 함수가 실제로 중단됨)
 * - continuation을 저장했다가 나중에 resume
 * - 호출자는 COROUTINE_SUSPENDED를 받음
 * - 호출 스택이 unwound됨
 */
fun suspendedWithAsyncWork(cont: MyContinuationV3) {
    println("1. suspendedWithAsyncWork() 호출 시작")
    println("   콜 스택: main → suspendedWithAsyncWork")

    when(cont.label) {
        0 -> {
            println("2. 상태 0: 비동기 작업 시작")
            cont.label = 1
            println("3. 다음 상태 설정: label = 1")

            // 비동기 작업을 수행하는 함수 호출
            asyncFetchData(cont)
            // 이 지점 이후는 실행되지 않음!
            // (continuation.resume()이 호출되고 콜 스택이 unwound되기 때문)
            // → 다시 돌아올 때는 상태 1로 시작함

            println("   !! 이 줄은 처음에는 출력되지 않음 !!")
        }
        1 -> {
            println("\n[재개] suspendedWithAsyncWork() 상태 1로 재개")
            println("   콜 스택: resumeWith → suspendedWithAsyncWork (이전 스택은 unwound)")
            val result = cont.result
            println("4. 비동기 작업 결과 받음: $result")
            println("5. suspendedWithAsyncWork() 완료")
        }
    }
}

/**
 * 비동기 작업을 시뮬레이션하는 함수
 * - Continuation을 저장했다가 나중에 resume
 * - 실제로는 네트워크 요청이나 다른 비동기 작업일 수 있음
 */
fun asyncFetchData(cont: MyContinuationV3) {
    println("4. asyncFetchData() 호출")
    println("   콜 스택: main → suspendedWithAsyncWork → asyncFetchData")

    // 비동기 작업을 시뮬레이션하기 위해 별도의 스레드에서 실행
    Thread {
        println("\n5. [비동기 작업 시작] 별도의 스레드에서 작업 중...")
        Thread.sleep(1000) // 1초 대기 (네트워크 요청 시뮬레이션)
        println("6. [비동기 작업 완료] 결과를 얻음: User(id=1, name='철수')")

        println("7. Continuation.resumeWith() 호출")
        println("   → 이전 콜 스택이 unwound되고 suspendedWithAsyncWork가 재개됨")
        cont.resumeWith(Result.success("User(id=1, name='철수')"))
    }.start()

    println("8. asyncFetchData() 반환")
    println("   → COROUTINE_SUSPENDED 마커를 반환 (또는 시뮬레이션)")
    println("   → 호출자(suspendedWithAsyncWork)의 콜 스택이 unwound됨\n")
}

class MyContinuationV3(
    override val context: CoroutineContext = EmptyCoroutineContext,
    val name: String = ""
) : Continuation<String> {

    var label = 0
    var result = ""

    override fun resumeWith(result: Result<String>) {
        this.result = result.getOrThrow()
        println("→ [$name] Continuation.resumeWith() 호출됨")
        println("→ [$name] 결과 저장: ${this.result}")
        println("→ [$name] suspendedWithAsyncWork() 다시 호출 (상태 1로)\n")

        // 다시 호출 (상태 1로 진행)
        suspendedWithAsyncWork(this)
    }
}
