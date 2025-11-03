package example3

import example1.delay
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlin.coroutines.coroutineContext

suspend fun main() = coroutineScope {
    val channel = Channel<Int>() // 정수형 채널 생성
    launch {
        repeat(5) { index ->
            delay(1000)
            println("Producting next one")
            channel.send(index) // 채널에 값 전송
        }
    }

    launch {
        repeat(5) {
            val value = channel.receive() // 채널에서 값 수신
            println("Received $value")
        }
    }
}
