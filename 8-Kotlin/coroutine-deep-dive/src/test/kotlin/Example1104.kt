import example1.delay
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.withTimeout
import org.junit.jupiter.api.assertThrows
import kotlin.test.Test

// p.138
class Example1104Test {
    @Test
    fun testTime2() = runTest {
        withTimeout(1000L) {
            delay(900L)
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
