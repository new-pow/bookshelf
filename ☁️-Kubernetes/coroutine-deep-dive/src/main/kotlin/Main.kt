/**
 * 코루틴 라이브러리 예시 실행기
 *
 * 사용법:
 * - gradle run                         (기본값: basic)
 * - gradle run --args="basic"          (기본 코루틴 예시)
 * - gradle run --args="advanced"       (Flow 고급 예시)
 */

fun main(args: Array<String>) {
    val example = if (args.isNotEmpty()) args[0].lowercase() else "basic"

    when(example) {
        "basic" -> runBasicExample()
        "advanced" -> runAdvancedExample()
        else -> {
            println("사용 방법:")
            println("  gradle run                    - 기본 코루틴 예시")
            println("  gradle run --args=\"basic\"    - 기본 코루틴 예시")
            println("  gradle run --args=\"advanced\" - Flow 고급 예시")
            println()
            runBasicExample()
        }
    }
}

fun runBasicExample() {
    kotlinx.coroutines.runBlocking {
        println("=== kotlinx-coroutines 라이브러리 예시 ===\n")

        println("[1] launch - 결과를 반환하지 않는 코루틴")
        println("─".repeat(50))
        exampleLaunch()

        println("\n[2] async - 결과를 반환하는 코루틴")
        println("─".repeat(50))
        exampleAsync()

        println("\n[3] 순차 실행")
        println("─".repeat(50))
        exampleSequential()

        println("\n[4] 병렬 실행")
        println("─".repeat(50))
        exampleParallel()

        println("\n=== 모든 예시 완료 ===")
    }
}

fun runAdvancedExample() {
    kotlinx.coroutines.runBlocking {
        println("=== Flow를 이용한 반응형 프로그래밍 ===\n")

        println("[1] 기본 Flow")
        println("─".repeat(50))
        basicFlow()

        println("\n[2] Flow를 사용한 데이터 스트림")
        println("─".repeat(50))
        dataStreamFlow()

        println("\n[3] Exception handling in Flow")
        println("─".repeat(50))
        exceptionHandlingFlow()
    }
}
