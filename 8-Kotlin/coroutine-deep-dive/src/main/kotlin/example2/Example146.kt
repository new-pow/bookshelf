package example2

import kotlinx.coroutines.*
import kotlin.random.Random

@OptIn(ExperimentalCoroutinesApi::class)
suspend fun main() = coroutineScope(
) {
    val dis1 = Dispatchers.Default.limitedParallelism(3)
    val dis2 = Dispatchers.Default.limitedParallelism(1)
    repeat(1000) {
        launch(dis2) {
            List(1000) { Random.nextInt() }.maxOrNull()
            val threadName = Thread.currentThread().name
            println("Running on thread $threadName") // ~ 10개
        }
    }
}
