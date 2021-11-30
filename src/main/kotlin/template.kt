import measure
import readResourceFile
import verifyResult
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime

/*

 */
private class DayXX(
) {
    fun solve(): Int {
        TODO("Not yet implemented")
    }

    constructor(testInput: String) : this(
    )

}

fun main() {
    listOf(
        { verifyResult(0, DayXX(checkInput).solve()) },
        { println("Result is " + DayXX(testInput).solve()) }
    ).onEachIndexed { i, test -> measure(test, i) }
}

private const val checkInput: String = "" +
        ""

private val testInput by lazy { readResourceFile("/advent2020/dayXX-task1.txt") }