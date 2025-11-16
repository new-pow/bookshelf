import example1.delay
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout
import org.junit.jupiter.api.assertThrows
import kotlin.test.Test

// p.138
class Example1104Test {
    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun testTime2() = runTest {
        withTimeout(1000L) {
            advanceTimeBy(900L) // 가상시간 진행
        }
    }

    @Test
    fun testTime3() = runTest {
        assertThrows<TimeoutCancellationException> {
            withTimeout(1000L) {
                delay(1100L)
            }
        }

    }
}
