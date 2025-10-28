package example2

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlin.random.Random

@OptIn(ExperimentalCoroutinesApi::class)
suspend fun main() = coroutineScope(
) {
    val dis = Dispatchers.Default.limitedParallelism(3)
    repeat(1000) {
        launch(dis) {
            List(1000) { Random.nextInt() }.maxOrNull()

            val threadName = Thread.currentThread().name
            println("Running on thread $threadName") // ~ 10개
        }
    }
}
