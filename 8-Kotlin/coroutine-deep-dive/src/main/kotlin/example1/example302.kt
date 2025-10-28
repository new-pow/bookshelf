package example1

import java.util.concurrent.Executors
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

private val executor = Executors.newSingleThreadScheduledExecutor {
    Thread(it, "scheduler").apply { isDaemon = true } // isDaemon 의 뜻: 백그라운드에서 실행되는 스레드.
    // 스레드를 사용하기는 하지만, delay
}

suspend fun delay(timeMillis: Long) {
    suspendCoroutine<Unit> { continueIt ->
        println("delay1: " + Thread.currentThread().name)

        executor.schedule({
            continueIt.resume(Unit)
            println("delay2: " + Thread.currentThread().name)
        }, timeMillis, java.util.concurrent.TimeUnit.MILLISECONDS)

        println("delay3: " +Thread.currentThread().name)
    }
}

suspend fun main() {
    println("Start")
    println(Thread.currentThread().name)

    delay(2000L)

    println("End")
}
