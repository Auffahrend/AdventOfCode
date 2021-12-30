package advent2021

import deepMutableCopy
import measure
import readResourceFile
import verifyResult
import wrapAround
import get
import set

/*

 */
private class Day25(
) {
    lateinit var initialState: List<List<Spot>>

    fun stableState(): Int {
        var step = 0
        var state = initialState
        val newEmptySpots = mutableSetOf<Coords>()
        do {
            step++
            newEmptySpots.clear()
            state = state
                .move(Spot.East, newEmptySpots)
                .move(Spot.South, newEmptySpots)
//                .also {
//                    println("After step $step:")
//                    it.onEach { row -> row.map { it.toChar() }.toCharArray().also { println(it) } } }
        } while (newEmptySpots.isNotEmpty())
        return step
    }

    constructor(testInput: String) : this() {
        initialState = testInput.lines().filterNot { it.isBlank() }
            .map {
                it.toCharArray().map {
                    when (it) {
                        'v' -> Spot.South
                        '>' -> Spot.East
                        else -> Spot.Empty
                    }
                }
            }
    }

    sealed class Spot {
        abstract fun from(p: Coords): Coords
        abstract fun toChar(): Char

        object Empty : Spot() {
            override fun from(p: Coords): Coords {
                TODO("Not yet implemented")
            }

            override fun toChar(): Char = '.'
        }

        object South : Spot() {
            override fun from(p: Coords): Coords = p.first to p.second - 1
            override fun toChar(): Char = 'v'
        }

        object East : Spot() {
            override fun from(p: Coords): Coords = p.first - 1 to p.second
            override fun toChar(): Char = '>'
        }
    }
}

private fun List<List<Day25.Spot>>.findEmptySpots(): Set<Coords> = this.flatMapIndexed { y, row ->
    row.mapIndexedNotNull { x, s -> if (s == Day25.Spot.Empty) x to y else null }
}.toSet()


private fun List<List<Day25.Spot>>.move(
    type: Day25.Spot,
    newEmptySpots: MutableSet<Coords>
): List<List<Day25.Spot>> {
    val newState = this.deepMutableCopy()
    findEmptySpots().onEach { spot ->
        wrapAround(type.from(spot)).let { from ->
            if (this[from] == type) {
                newState[from] = Day25.Spot.Empty
                newState[spot] = type
                newEmptySpots.add(from)
            }
        }
    }
    return newState
}

fun main() {
    listOf(
        { verifyResult(58, Day25(checkInput).stableState()) },
        { println("Result is " + Day25(testInput).stableState()) }
    ).onEachIndexed { i, test -> measure(test, i) }
}

private const val checkInput: String = "" +
        "v...>>.vv>\n" +
        ".vv>>.vv..\n" +
        ">>.>v>...v\n" +
        ">>v>>.>.v.\n" +
        "v>v.vv.v..\n" +
        ">.>>..v...\n" +
        ".vv..>.>v.\n" +
        "v.v..>>v.v\n" +
        "....v..v.>\n"

private val testInput by lazy { readResourceFile("/advent2021/day25-task1.txt") }