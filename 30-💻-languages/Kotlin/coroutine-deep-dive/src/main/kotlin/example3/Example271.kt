package example3

import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.combine

// 1. 기본 람다식
//fun main() {
//    val f : () -> Unit = {
//        println("A")
//        println("B")
//        println("C")
//    }
//
//    f()
//    f()
//}

// 2. suspend
//suspend fun main() {
//    val f : suspend () -> Unit = {
//        println("A")
//        delay(2000L)
//        println("B")
//        delay(2000L)
//        println("C")
//        delay(2000L)
//    }
//
//    f()
//    f()
//}

// 3. emit
//suspend fun main() {
//    val f: suspend ((String) -> Unit) -> Unit = { emit ->
//        emit("A") // emit는 매개변수로 전달된 람다식
//        emit("B")
//        emit("C")
//    }
//
//    f { println(it) }
//    f { println(it) }
//}

// 4. 중단함수가 되어야함.
//fun interface FlowCollector { // 함수형 인터페이스
//    suspend fun emit(value: String)
//}
//
//suspend fun main() {
//    val f: suspend (FlowCollector) -> Unit = {
//        it.emit("A")
//        it.emit("B")
//        it.emit("C")
//    }
//
//    f { println(it) } // 람다식을 FlowCollector 타입으로 전달
//    f { println(it) }
//}

// 5. flowCollector
//fun interface FlowCollector { // 함수형 인터페이스
//    suspend fun emit(value: String)
//}
//
//interface Flow {
//    suspend fun collect(collector: FlowCollector)
//}
//
//suspend fun main() {
//    // 내부 로직
//    val builder: suspend FlowCollector.() -> Unit = {
//        emit("A")
//        emit("B")
//        emit("C")
//    }
//    // flow 객체 생성
//    val flow: Flow = object : Flow {
//        override suspend fun collect(collector: FlowCollector) {
//            collector.builder()
//        }
//    }
//
//    flow.collect { println(it) }
//}

// 6. 플로우 생성 간단하게 만들기 위해 flow cnrk
//fun interface FlowCollector { // 함수형 인터페이스
//    suspend fun emit(value: String)
//}
//
//interface Flow {
//    suspend fun collect(collector: FlowCollector)
//}
//
//fun flow(builder: suspend FlowCollector.() -> Unit): Flow {
//    return object : Flow {
//        override suspend fun collect(collector: FlowCollector) {
//            collector.builder()
//        }
//    }
//}
//
//suspend fun main() {
//    // flow 객체 생성
//    val f = flow {
//        emit("A")
//        emit("B")
//        emit("C")
//    }
//
//    f.collect { println(it) }
//    f.collect { println(it) }
//}

// 7. 제네릭 타입으로 변경
fun interface FlowCollector<T> { // 함수형 인터페이스
    suspend fun emit(value: T)
}

interface Flow<T> {
    suspend fun collect(collector: FlowCollector<T>)
}

fun <T> flow(builder: suspend FlowCollector<T>.() -> Unit): Flow<T> {
    return object : Flow<T> {
        override suspend fun collect(collector: FlowCollector<T>) {
            collector.builder()
        }
    }
}

suspend fun main() {
    // flow 객체 생성
    val f = flow {
        emit("A")
        emit("B")
        emit("C")
    }

    Channel.RENDEZVOUS
    generateSequence{}

    f.collect { println(it) }
    f.collect { println(it) }
}
