package advent2021

import measure
import readResourceFile
import verifyResult

/*

 */
private class Day23(

) {
    fun solve(): Long {
        TODO("Not yet implemented")
    }

    constructor(testInput: String) : this(
    )

    private class Rooms(
        val first: List<Amphipod?>,
        val second: List<Amphipod?>,
        val third: List<Amphipod?>,
        val fourth: List<Amphipod?>,
    )
    }

    enum class Amphipod(val stepCost: Int) {
        A(1), B(10), C(100), D(1000)
    }
}

fun main() {
    listOf(
        { verifyResult(0, Day23(checkInput).solve()) },
        { println("Result is " + Day23(testInput).solve()) }
    ).onEachIndexed { i, test -> measure(test, i) }
}

private const val checkInput: String = "" +
        "#############\n" +
        "#...........#\n" +
        "###B#C#B#D###\n" +
        "  #A#D#C#A#\n" +
        "  #########\n"

private val testInput by lazy { readResourceFile("/advent2021/day23-task1.txt") }