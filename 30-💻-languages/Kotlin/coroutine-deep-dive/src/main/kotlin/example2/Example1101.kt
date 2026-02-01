package example2

import example1.delay
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.runBlocking


fun main() = runBlocking {
    val a = coroutineScope {
        delay(1_000)
        10
    }
    println("a is calculated")
    val b = coroutineScope {
        delay(1_000)
        20
    }
    println("b is calculated")

    println(a)
    println(b)
}
