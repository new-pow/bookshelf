package example2

import kotlinx.coroutines.*

// pg. 130
suspend fun main() = runBlocking(CoroutineName("PARENT")) { // name
    println("Before")
    val job =launch(CoroutineName("CHILD")) {
        longTask2()
    }
    delay(1_500)
    job.cancel() // 취소
    println("After")
}

suspend fun longTask2() = coroutineScope {
    launch {
        delay(1000L)
        val name = coroutineContext[CoroutineName]?.name
        println("[$name] Finished task 1")
    }
    launch {
        delay(2000L)
        val name = coroutineContext[CoroutineName]?.name
        println("[$name] Finished task 2") // 이게 중간에 작업이 끝나서 print되지 않음.
    }
}
