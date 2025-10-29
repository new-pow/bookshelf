package example2

import kotlinx.coroutines.*
import java.util.concurrent.Executors
import kotlin.coroutines.coroutineContext

val LoomDispatcher = Executors.newVirtualThreadPerTaskExecutor()
    .asCoroutineDispatcher()

// 확장
val Dispatchers.Loom: CoroutineDispatcher
    get() = LoomDispatcher

suspend fun main() {
    coroutineScope {
        repeat(1_000) {
            launch(Dispatchers.Default) {
                Thread.sleep(1000)
            }
        }
    }.let(::println)
}
