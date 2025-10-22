import java.util.concurrent.Executors
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

private val executor = Executors.newSingleThreadScheduledExecutor {
    Thread(it, "scheduler").apply { isDaemon = true } // isDaemon 의 뜻: 백그라운드에서 실행되는 스레드.
    // 스레드를 사용하기는 하지만, delay
}

suspend fun delay(timeMillis: Long) {
    suspendCoroutine<Unit> { continueIt ->
        executor.schedule({
            continueIt.resume(Unit)
        }, timeMillis, java.util.concurrent.TimeUnit.MILLISECONDS)
    }
}

suspend fun main() {
    println("Start")

    delay(2000L)

    println("End")
}
