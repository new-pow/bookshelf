package bu1

import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

suspend fun main() {
    println("Before")
    suspendCoroutine<Unit> { cont ->
        Thread.sleep(1000L)
        cont.resume(Unit)
        println("Resume")
    } // Suspends here
    println("After")
}

