import example1.delay
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout
import org.junit.jupiter.api.assertThrows
import kotlin.test.Test

// p.138
class Example11042Test {
    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun testTime2() = runTest {
        withContext(Dispatchers.Default.limitedParallelism(1)) {
            withTimeout(1000L) {
                delay(900L)
            }
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun testTime3() = runTest {
        assertThrows<TimeoutCancellationException> {
            withContext(Dispatchers.Default.limitedParallelism(1)) {
                withTimeout(1000L) {
                    delay(1100L)
                }
            }
        }

    }
}
