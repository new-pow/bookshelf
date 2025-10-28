import example1.delay
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.withTimeout
import kotlin.test.Test

// p.138
class Example1104Test {
    @Test
    fun testTime2() = runTest {
        withTimeout(1000L) {
            delay(900L)
        }
    }

    @Test(expected = TimeoutCancellationException::class)
    fun testTime3() = runTest {

    }
}
