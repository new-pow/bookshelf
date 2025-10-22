import kotlin.coroutines.Continuation
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

/**
 * example401: 콜 스택 깊이 추적
 * 콜 스택이 어떻게 변하는지 들여쓰기와 스택 깊이로 시각화
 */
fun main401() {
    println("=== Example401: 콜 스택 깊이 추적 ===\n")
    printCallStack401("[in] main")
    callStackDepth401++
    myCoroutine401(MyContinuation401())
    callStackDepth401--
    printCallStack401("[out] main")
}

var callStackDepth401 = 0

fun printCallStack401(message: String) {
    val indent = "  ".repeat(callStackDepth401)
    println("$indent[${Thread.currentThread().stackTrace.size - 4}] $message")
}

fun myCoroutine401(cont: MyContinuation401) {
    printCallStack401("[in] myCoroutine(), label: ${cont.label}")
    callStackDepth401++

    when(cont.label) {
        0 -> {
            printCallStack401("상태 0: 사용자 데이터 조회 시작")
            cont.label = 1
            fetchUserData401(cont)
        }
        1 -> {
            printCallStack401("상태 1: 캐시 저장 시작")
            val userData = cont.result
            cont.label = 2
            cacheUserData401(userData, cont)
        }
        2 -> {
            printCallStack401("상태 2: 텍스트 뷰 업데이트")
            val userCache = cont.result
            updateTextView401(userCache)
        }
    }

    callStackDepth401--
    printCallStack401("[out] myCoroutine()")
}

fun fetchUserData401(cont: MyContinuation401) {
    printCallStack401("[in] fetchUserData()")
    callStackDepth401++
    val result = "[서버에서 받은 사용자 정보]"
    printCallStack401("작업완료: $result")
    printCallStack401("[out] fetchUserData() → resumeWith() 호출")
    callStackDepth401--
    cont.resumeWith(Result.success(result))
}

fun cacheUserData401(user: String, cont: MyContinuation401) {
    printCallStack401("[in] cacheUserData()")
    callStackDepth401++
    val result = "[캐쉬함 $user]"
    printCallStack401("작업완료: $result")
    printCallStack401("[out] cacheUserData() → resumeWith() 호출")
    callStackDepth401--
    cont.resumeWith(Result.success(result))
}

fun updateTextView401(user: String) {
    printCallStack401("[in] updateTextView()")
    callStackDepth401++
    printCallStack401("작업완료: [텍스트 뷰에 출력 $user]")
    callStackDepth401--
    printCallStack401("[out] updateTextView()")
}

class MyContinuation401(override val context: CoroutineContext = EmptyCoroutineContext)
    : Continuation<String> {

    var label = 0
    var result = ""

    override fun resumeWith(result: Result<String>) {
        this.result = result.getOrThrow()
        printCallStack401("[in] Continuation.resumeWith()")
        callStackDepth401++
        printCallStack401("결과 저장: ${this.result}")
        callStackDepth401--
        printCallStack401("[out] Continuation.resumeWith() → myCoroutine() 재개")
        myCoroutine401(this)
    }
}
