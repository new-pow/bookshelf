//import kotlinx.coroutines.*
//import kotlinx.coroutines.flow.Flow
//import kotlinx.coroutines.flow.flow
//import kotlinx.coroutines.flow.collect
//
///**
// * kotlinx-coroutines 고급 예시
// * Flow를 사용한 반응형 프로그래밍
// */
//
//fun main() = runBlocking {
//    println("=== Flow를 이용한 반응형 프로그래밍 ===\n")
//
//    println("[1] 기본 Flow")
//    println("─".repeat(50))
//    basicFlow()
//
//    println("\n[2] Flow를 사용한 데이터 스트림")
//    println("─".repeat(50))
//    dataStreamFlow()
//
//    println("\n[3] Exception handling in Flow")
//    println("─".repeat(50))
//    exceptionHandlingFlow()
//}
//
///**
// * 기본 Flow: 여러 값을 시간에 따라 생성
// */
//suspend fun basicFlow() {
//    val numbers = flow {
//        for (i in 1..3) {
//            delay(100)
//            emit(i)
//        }
//    }
//
//    numbers.collect { value ->
//        println("값: $value")
//    }
//}
//
///**
// * Flow를 사용한 데이터 스트림 처리
// */
//suspend fun dataStreamFlow() {
//    val userFlow = getUsersFlow()
//
//    userFlow.collect { user ->
//        println("사용자: $user")
//    }
//}
//
//fun getUsersFlow(): Flow<String> = flow {
//    println("사용자 1 로드 중...")
//    delay(500)
//    emit("User(id=1, name='철수')")
//
//    println("사용자 2 로드 중...")
//    delay(500)
//    emit("User(id=2, name='영희')")
//
//    println("사용자 3 로드 중...")
//    delay(500)
//    emit("User(id=3, name='민준')")
//}
//
///**
// * Flow에서 예외 처리
// */
//suspend fun exceptionHandlingFlow() {
//    val flow = flow {
//        for (i in 1..3) {
//            if (i == 2) {
//                throw Exception("Error at $i")
//            }
//            emit(i)
//        }
//    }
//
//    try {
//        flow.collect { value ->
//            println("값: $value")
//        }
//    } catch (e: Exception) {
//        println("예외 처리: ${e.message}")
//    }
//}
