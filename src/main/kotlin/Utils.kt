import kotlin.time.ExperimentalTime
import kotlin.time.measureTime

fun readResourceFile(path: String) =
    Utils::class.java.getResource(if (path.startsWith("/")) path else "/$path")!!.readText()

fun <T> verifyResult(expected: T, actual: T) {
    if (expected != actual) {
        throw RuntimeException("Actual result $actual differs from expected $expected")
    }
}

@OptIn(ExperimentalTime::class)
fun measure(test: () -> Unit, i: Int) {
    val duration = measureTime(test)
    println("Test $i succeeded in $duration")
}

private class Utils