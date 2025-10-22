package bu1

import kotlin.concurrent.thread

fun main() {
    repeat(100_000) {
        thread {
            Thread.sleep(1000L)
            print(".")
        }
    }

//    repeat(100_000) {
//        launch {
//            delay(100L)
//            print(".")
//        }
//    }
}

//fun main() = runBlocking {
//    repeat(100_000) {
//        launch {
//
//        }
//    }
//}
