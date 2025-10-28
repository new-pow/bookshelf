package example2

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlin.random.Random

suspend fun main() = coroutineScope(
) {
    Dispatchers.Default.limitedParallelism(5)
    repeat(1000) {
        launch {
            List(1000) { Random.nextInt() }.maxOrNull()

            val threadName = Thread.currentThread().name
            println("Running on thread $threadName") // ~ 10개
        }
    }
}
