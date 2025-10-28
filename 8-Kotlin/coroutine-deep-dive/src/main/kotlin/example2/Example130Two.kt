@file:JvmName("Example130Kt")

package example2

import kotlinx.coroutines.*

// pg. 130
suspend fun main() = runBlocking(CoroutineName("PARENT")) { // name
    println("Before")
    longTask()
    println("After")
}

suspend fun longTask() = coroutineScope {
    launch {
        delay(1000L)
        val name = coroutineContext[CoroutineName]?.name
        println("[$name] Finished task 1")
    }
    launch {
        delay(2000L)
        val name = coroutineContext[CoroutineName]?.name
        println("[$name] Finished task 2")
    }
    // 여기서 멈추고있는거같음.
}
