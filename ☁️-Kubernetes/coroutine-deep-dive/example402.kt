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
    println("=== 코루틴의 콜 스택(Call Stack) 동작 원리 ===\n")
    println("초기 콜 스택 깊이: ${Thread.currentThread().stackTrace.size}\n")

    val continuation = MyContinuationV2()
    println("[1] main() 에서 myCoroutineV2() 호출")
    println("    콜 스택: main → myCoroutineV2\n")

    myCoroutineV2(continuation)

    println("\n[최종] main() 함수 종료")
    println("    콜 스택: main (완전히 unwound됨)")
}

fun myCoroutineV2(cont: MyContinuationV2) {
    when(cont.label) {
        0 -> {
            println("[상태 0] 데이터 조회 시작")
            println("    현재 콜 스택: main → myCoroutineV2 → fetchUserDataV2")
            cont.label = 1
            fetchUserDataV2(cont)
            // fetchUserDataV2에서 resumeWith가 호출되면, 스택이 여기서 unwound됨
            // resumeWith 내부에서 myCoroutineV2이 다시 호출됨
        }
        1 -> {
            println("\n[상태 1] 데이터 캐시")
            println("    현재 콜 스택: resumeWith → myCoroutineV2 (이전 스택은 unwound)")
            val userData = cont.result
            cont.label = 2
            cacheUserDataV2(userData, cont)
        }
        2 -> {
            println("\n[상태 2] UI 업데이트")
            println("    현재 콜 스택: resumeWith → myCoroutineV2")
            val cachedData = cont.result
            updateUIV2(cachedData)
        }
    }
}

fun fetchUserDataV2(cont: MyContinuationV2) {
    println("        └─ fetchUserDataV2() 실행 중...")
    // 실제로는 여기서 비동기 작업이 발생하지만,
    // 예시를 위해 동기적으로 처리
    val result = "User(id=1, name='철수')"
    println("        └─ 작업 완료: $result")
    println("        └─ resumeWith() 호출 후 콜 스택이 unwound됨\n")

    cont.resumeWith(Result.success(result))

    // 위의 resumeWith() 호출 이후로는 이 함수의 콜 스택이 사라짐
    // (콜 스택이 이미 반환됨)
}

fun cacheUserDataV2(user: String, cont: MyContinuationV2) {
    println("        └─ cacheUserDataV2() 실행 중...")
    val cached = "[$user] → 캐시됨"
    println("        └─ 작업 완료: $cached")
    println("        └─ resumeWith() 호출 후 콜 스택이 unwound됨\n")

    cont.resumeWith(Result.success(cached))
}

fun updateUIV2(data: String) {
    println("        └─ updateUIV2() 실행 중...")
    println("        └─ UI 업데이트: $data")
    println("        └─ 모든 작업 완료\n")
}

class MyContinuationV2(override val context: CoroutineContext = EmptyCoroutineContext)
    : Continuation<String> {

    var label = 0
    var result = ""

    override fun resumeWith(result: Result<String>) {
        this.result = result.getOrThrow()
        println("        → Continuation.resumeWith() 호출")
        println("        → 이전 함수의 콜 스택 unwound")
        // 이 지점에서 스택이 clean하게 되고, myCoroutineV2이 새로 호출됨
        myCoroutineV2(this)
    }
}
