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
    println("=== Dispatchers.Default ===")
    val startDefault = System.currentTimeMillis()
    coroutineScope {
        repeat(1_000) {
            launch(Dispatchers.Default) {
                Thread.sleep(1000)
            }
        }
    }
    val timeDefault = System.currentTimeMillis() - startDefault
    println("소요 시간: ${timeDefault}ms\n")

    println("=== Dispatchers.Loom ===")
    val startLoom = System.currentTimeMillis()
    coroutineScope {
        repeat(1_000) {
            launch(Dispatchers.Loom) {
                Thread.sleep(1000)
            }
        }
    }
    val timeLoom = System.currentTimeMillis() - startLoom
    println("소요 시간: ${timeLoom}ms\n")

    println("=== 비교 결과 ===")
    println("Default: ${timeDefault}ms")
    println("Loom: ${timeLoom}ms")
    println("Loom이 ${timeDefault - timeLoom}ms 더 빠름")
}
