import kotlin.coroutines.Continuation
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

/**
 * 코틀린 코루틴의 콜 스택(Call Stack) 동작 원리를 보여주는 예시
 *
 * 일반적인 함수 호출 vs 코루틴의 차이점:
 * - 일반 함수: 스택이 계속 쌓임 (깊어짐)
 * - 코루틴: resumeWith()를 통해 스택을 unwound한 후 재개 (깊이가 줄어듦)
 */

fun main() {
    println("=== 코루틴의 콜 스택 동작 원리 ===\n")
    println("초기 콜 스택 깊이: ${Thread.currentThread().stackTrace.size}\n")

    val continuation = MyContinuation()
    println("[1] main() 에서 myCoroutine() 호출")
    println("    콜 스택: main → myCoroutine\n")

    myCoroutine(continuation)

    println("\n[최종] main() 함수 종료")
    println("    콜 스택: main (완전히 unwound됨)")
}

fun myCoroutine(cont: MyContinuation) {
    when(cont.label) {
        0 -> {
            println("[상태 0] 데이터 조회 시작")
            println("    현재 콜 스택: main → myCoroutine → fetchUserData")
            cont.label = 1
            fetchUserData(cont)
            // fetchUserData에서 resumeWith가 호출되면, 스택이 여기서 unwound됨
            // resumeWith 내부에서 myCoroutine이 다시 호출됨
        }
        1 -> {
            println("\n[상태 1] 데이터 캐시")
            println("    현재 콜 스택: resumeWith → myCoroutine (이전 스택은 unwound)")
            val userData = cont.result
            cont.label = 2
            cacheUserData(userData, cont)
        }
        2 -> {
            println("\n[상태 2] UI 업데이트")
            println("    현재 콜 스택: resumeWith → myCoroutine")
            val cachedData = cont.result
            updateUI(cachedData)
        }
    }
}

fun fetchUserData(cont: MyContinuation) {
    println("        └─ fetchUserData() 실행 중...")
    // 실제로는 여기서 비동기 작업이 발생하지만,
    // 예시를 위해 동기적으로 처리
    val result = "User(id=1, name='철수')"
    println("        └─ 작업 완료: $result")
    println("        └─ resumeWith() 호출 후 콜 스택이 unwound됨\n")

    cont.resumeWith(Result.success(result))

    // 위의 resumeWith() 호출 이후로는 이 함수의 콜 스택이 사라짐
    // (콜 스택이 이미 반환됨)
}

fun cacheUserData(user: String, cont: MyContinuation) {
    println("        └─ cacheUserData() 실행 중...")
    val cached = "[$user] → 캐시됨"
    println("        └─ 작업 완료: $cached")
    println("        └─ resumeWith() 호출 후 콜 스택이 unwound됨\n")

    cont.resumeWith(Result.success(cached))
}

fun updateUI(data: String) {
    println("        └─ updateUI() 실행 중...")
    println("        └─ UI 업데이트: $data")
    println("        └─ 모든 작업 완료\n")
}

class MyContinuation(override val context: CoroutineContext = EmptyCoroutineContext)
    : Continuation<String> {

    var label = 0
    var result = ""

    override fun resumeWith(result: Result<String>) {
        this.result = result.getOrThrow()
        println("        → Continuation.resumeWith() 호출")
        println("        → 이전 함수의 콜 스택 unwound")
        // 이 지점에서 스택이 clean하게 되고, myCoroutine이 새로 호출됨
        myCoroutine(this)
    }
}
