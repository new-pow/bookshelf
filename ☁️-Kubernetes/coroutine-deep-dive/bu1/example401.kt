package bu1

import kotlin.coroutines.Continuation
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

var callStackDepth = 0

fun main() {
    printCallStack("[in] main")
    callStackDepth++
    myCoroutine(MyContinuation())
    callStackDepth--
    printCallStack("[out] main")
}

fun printCallStack(message: String) {
    val indent = "  ".repeat(callStackDepth)
    println("$indent[${Thread.currentThread().stackTrace.size - 4}] $message")
}

fun myCoroutine(cont: MyContinuation) {
    printCallStack("[in] myCoroutine(), label: ${cont.label}")
    callStackDepth++

    when(cont.label) {
        0 -> {
            printCallStack("상태 0: 사용자 데이터 조회 시작")
            cont.label = 1
            fetchUserData(cont)
        }
        1 -> {
            printCallStack("상태 1: 캐시 저장 시작")
            val userData = cont.result
            cont.label = 2
            cacheUserData(userData, cont)
        }
        2 -> {
            printCallStack("상태 2: 텍스트 뷰 업데이트")
            val userCache = cont.result
            updateTextView(userCache)
        }
    }

    callStackDepth--
    printCallStack("[out] myCoroutine()")
}

fun fetchUserData(cont: MyContinuation) {
    printCallStack("[in] fetchUserData()")
    callStackDepth++
    val result = "[서버에서 받은 사용자 정보]"
    printCallStack("작업완료: $result")
    printCallStack("[out] fetchUserData() → resumeWith() 호출")
    callStackDepth--
    cont.resumeWith(Result.success(result))
}

fun cacheUserData(user: String, cont: MyContinuation) {
    printCallStack("[in] cacheUserData()")
    callStackDepth++
    val result = "[캐쉬함 $user]"
    printCallStack("작업완료: $result")
    printCallStack("[out] cacheUserData() → resumeWith() 호출")
    callStackDepth--
    cont.resumeWith(Result.success(result))
}

fun updateTextView(user: String) {
    printCallStack("[in] updateTextView()")
    callStackDepth++
    printCallStack("작업완료: [텍스트 뷰에 출력 $user]")
    callStackDepth--
    printCallStack("[out] updateTextView()")
}

class MyContinuation(override val context: CoroutineContext = EmptyCoroutineContext)
    : Continuation<String> {

    var label = 0
    var result = ""

    override fun resumeWith(result: Result<String>) {
        this.result = result.getOrThrow()
        printCallStack("[in] Continuation.resumeWith()")
        callStackDepth++
        printCallStack("결과 저장: ${this.result}")
        callStackDepth--
        printCallStack("[out] Continuation.resumeWith() → myCoroutine() 재개")
        // 이 지점에서 콜 스택이 unwound되고, myCoroutine()이 다시 호출됨
        myCoroutine(this)
    }
}
