import kotlin.coroutines.suspendCoroutine

suspend fun main() {
    println("Before")
    suspendCoroutine<Unit> {  } // Suspends here
    println("After")
}

