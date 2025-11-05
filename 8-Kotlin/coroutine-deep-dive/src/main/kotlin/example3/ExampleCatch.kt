package example3

import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.runBlocking

// ========== 테스트 1: 단일 catch ==========
fun test1_SingleCatch() {
    println("=== Test 1: 단일 catch ===")
    val f = flow {
        emit("A")
        throw IllegalStateException("1st error")
    }

    runBlocking {
        repeat(3) {
            f.catch { e -> println("Catch[$it]: $e") }
                .collect { value -> println("Collect[$it]: $value") }
        }
    }
}

// ========== 테스트 2: 다중 catch (체인) ==========
fun test2_MultipleCatchChain() {
    println("\n=== Test 2: 다중 catch (체인) ===")
    val f = flow {
        emit("A")
        throw IllegalStateException("1st error")
    }

    runBlocking {
        f
            .catch { e ->
                println("First catch: $e")
                emit("Recovered from first")
            }
            .catch { e ->
                println("Second catch: $e")
                emit("Recovered from second")
            }
            .collect { value -> println("Collected: $value") }
    }
}

// ========== 테스트 3: 다중 catch + 에러 전파 ==========
fun test3_MultipleCatchWithErrorPropagation() {
    println("\n=== Test 3: 다중 catch + 에러 전파 ===")
    val f = flow {
        emit("Value-1")
        throw RuntimeException("Flow error")
    }

    runBlocking {
        f
            .catch { e ->
                println("First catch (doesn't emit): ${e.message}")
                // 여기서 emit하지 않으면 에러 전파
            }
            .catch { e ->
                println("Second catch: ${e.message}")
                emit("Recovered in second")
            }
            .collect { value -> println("Collected: $value") }
    }
}

// ========== 테스트 4: 조건부 catch ==========
fun test4_ConditionalCatch() {
    println("\n=== Test 4: 조건부 catch ===")
    val f = flow {
        emit("Data-1")
        throw IllegalArgumentException("Wrong argument")
    }

    runBlocking {
        f
            .catch { e ->
                // IllegalArgumentException만 처리
                if (e is IllegalArgumentException) {
                    println("Handled IllegalArgumentException: ${e.message}")
                    emit("Default value")
                } else {
                    throw e  // 다른 예외는 전파
                }
            }
            .collect { value -> println("Collected: $value") }
    }
}

// ========== 테스트 5: catch 없이 에러 발생 ==========
fun test5_ErrorWithoutCatch() {
    println("\n=== Test 5: catch 없이 에러 발생 ===")
    val f = flow {
        emit("Value")
        throw Exception("Uncaught error")
    }

    runBlocking {
        try {
            f.collect { value -> println("Collected: $value") }
        } catch (e: Exception) {
            println("Exception caught in try-catch: ${e.message}")
        }
    }
}

// ========== 테스트 6: catch에서 다시 에러 발생 ==========
fun test6_ErrorInCatch() {
    println("\n=== Test 6: catch에서 다시 에러 발생 ===")
    val f = flow {
        emit("Initial")
        throw RuntimeException("Original error")
    }

    runBlocking {
        try {
            f
                .catch { e ->
                    println("First catch: ${e.message}")
                    throw IllegalStateException("New error in catch")
                }
                .collect { value -> println("Collected: $value") }
        } catch (e: Exception) {
            println("Final exception caught: ${e.message}")
        }
    }
}

fun main() {
    test1_SingleCatch()
    test2_MultipleCatchChain()
    test3_MultipleCatchWithErrorPropagation()
    test4_ConditionalCatch()
    test5_ErrorWithoutCatch()
    test6_ErrorInCatch()
}

