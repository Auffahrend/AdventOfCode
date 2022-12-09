import kotlin.time.ExperimentalTime
import kotlin.time.measureTime

open class BaseTest {
    fun <T> verifyResult(expected: T, actual: T) {
        if (expected != actual) {
            throw RuntimeException("Actual result $actual differs from expected $expected")
        }
    }

    @OptIn(ExperimentalTime::class)
    fun measure(test: () -> Unit) {
        val method = Thread.currentThread().stackTrace[2].methodName
        val duration = measureTime(test)
        println("Test '$method' succeeded in $duration")
    }

}
