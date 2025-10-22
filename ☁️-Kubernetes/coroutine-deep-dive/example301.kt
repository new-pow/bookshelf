import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

suspend fun main() {
    println("Before")
    println(Thread.currentThread().name)
    suspendCoroutine<Unit> { cont ->
        cont.resume(Unit)
    } // Suspends here
    println("After")
}

