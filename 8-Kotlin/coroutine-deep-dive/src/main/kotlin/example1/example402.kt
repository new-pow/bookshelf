package example1

import kotlin.coroutines.Continuation
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

/**
 * example402: 콜 스택 unwinding 설명
 * 콜 스택이 어떻게 unwound되는지 명확하게 보여줌
 */
fun main402() {
    println("=== Example402: 콜 스택 unwinding 과정 ===\n")
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
    val result = "User(id=1, name='철수')"
    println("        └─ 작업 완료: $result")
    println("        └─ resumeWith() 호출 후 콜 스택이 unwound됨\n")

    cont.resumeWith(Result.success(result))
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
        myCoroutineV2(this)
    }
}
