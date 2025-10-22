import java.util.concurrent.Executors
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

private val executor = Executors.newSingleThreadScheduledExecutor {
    Thread(it, "scheduler").apply { isDaemon = true } // isDaemon 의 뜻: 백그라운드에서 실행되는 스레드
}

suspend fun main() {
    println("Start")

    suspendCoroutine<Unit> { continueIt ->
        executor.schedule({
            println("Hello, World!")
            continueIt.resume(Unit)
        }, 3, java.util.concurrent.TimeUnit.SECONDS)
    }

    println("End")
}
