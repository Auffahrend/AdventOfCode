@file:OptIn(ExperimentalStdlibApi::class)

package advent2021

import measure
import readResourceFile
import verifyResult
import java.lang.StrictMath.abs

/*
--- Day 5: Hydrothermal Venture ---

You come across a field of hydrothermal vents on the ocean floor! These vents constantly produce large, opaque clouds, so it would be best to avoid them if possible.

They tend to form in lines; the submarine helpfully produces a list of nearby lines of vents (your puzzle input) for you to review. For example:

0,9 -> 5,9
8,0 -> 0,8
9,4 -> 3,4
2,2 -> 2,1
7,0 -> 7,4
6,4 -> 2,0
0,9 -> 2,9
3,4 -> 1,4
0,0 -> 8,8
5,5 -> 8,2

Each line of vents is given as a line segment in the format x1,y1 -> x2,y2 where x1,y1 are the coordinates of one end the line segment and x2,y2 are the coordinates of the other end. These line segments include the points at both ends. In other words:

    An entry like 1,1 -> 1,3 covers points 1,1, 1,2, and 1,3.
    An entry like 9,7 -> 7,7 covers points 9,7, 8,7, and 7,7.

For now, only consider horizontal and vertical lines: lines where either x1 = x2 or y1 = y2.

So, the horizontal and vertical lines from the above list would produce the following diagram:

.......1..
..1....1..
..1....1..
.......1..
.112111211
..........
..........
..........
..........
222111....

In this diagram, the top left corner is 0,0 and the bottom right corner is 9,9. Each position is shown as the number of lines which cover that point or . if no line covers that point. The top-left pair of 1s, for example, comes from 2,2 -> 2,1; the very bottom row is formed by the overlapping lines 0,9 -> 5,9 and 0,9 -> 2,9.

To avoid the most dangerous areas, you need to determine the number of points where at least two lines overlap. In the above example, this is anywhere in the diagram with a 2 or larger - a total of 5 points.

Consider only horizontal and vertical lines. At how many points do at least two lines overlap?

--- Part Two ---

Unfortunately, considering only horizontal and vertical lines doesn't give you the full picture; you need to also consider diagonal lines.

Because of the limits of the hydrothermal vent mapping system, the lines in your list will only ever be horizontal, vertical, or a diagonal line at exactly 45 degrees. In other words:

    An entry like 1,1 -> 3,3 covers points 1,1, 2,2, and 3,3.
    An entry like 9,7 -> 7,9 covers points 9,7, 8,8, and 7,9.

Considering all lines from the above example would now produce the following diagram:

1.1....11.
.111...2..
..2.1.111.
...1.2.2..
.112313211
...1.2....
..1...1...
.1.....1..
1.......1.
222111....

You still need to determine the number of points where at least two lines overlap. In the above example, this is still anywhere in the diagram with a 2 or larger - now a total of 12 points.

Consider all of the lines. At how many points do at least two lines overlap?

 */
private class Day05 {
    private val lines: List<Line>

    private constructor(lines: List<Line>) {
        this.lines = lines
    }

    fun solve(): Int {
        val ventsMap: Map<Vector, Int> = produceVentsMap(lines)
        return ventsMap.count { (_, count) -> count > 1 }
    }

    private fun produceVentsMap(lines: List<Line>): Map<Vector, Int> =
        lines.flatMap { it.points }
            .groupBy { it }
            .mapValues { (_, v) -> v.size }

    constructor(testInput: String) : this(testInput.lines().filterNot { it.isEmpty() }.map { Line(it) })

    private data class Line(val from: Vector, val to: Vector) {
        constructor(s: Pair<String, String>) : this(Vector(s.first), Vector(s.second))
        constructor(s: String) : this(s.split(" -> ").let { it[0] to it[1] })

        val direction = (to - from).direction
        fun isAxial(): Boolean = direction.x == 0 || direction.y == 0
        val points: List<Vector> by lazy {
            buildList {
                var p = from
                add(from)
                while (p != to) {
                    p += direction
                    add(p)
                }
            }
        }
    }

    private data class Vector(val x: Int, val y: Int) {
        constructor(coordinates: Pair<String, String>) : this(coordinates.first.toInt(), coordinates.second.toInt())
        constructor(s: String) : this(s.split(",").let { it[0] to it[1] })

        operator fun minus(other: Vector): Vector = Vector(x - other.x, y - other.y)
        operator fun plus(other: Vector) = Vector(x + other.x, y + other.y)

        val direction: Vector by lazy {
            when {
                x * x + y * y == 1 -> this
                y == 0 -> Vector(x / abs(x), y)
                x == 0 -> Vector(x, y / abs(y))
                abs(x) == abs(y) -> Vector(x / abs(x), y / abs(y))
                else -> TODO("Can't normalize vector $this")
            }
        }
    }
}

fun main() {
    listOf(
        { verifyResult(12, Day05(checkInput).solve()) },
        { println("Result is " + Day05(testInput).solve()) }
    ).onEachIndexed { i, test -> measure(test, i) }
}

private const val checkInput: String = "" +
        "0,9 -> 5,9\n" +
        "8,0 -> 0,8\n" +
        "9,4 -> 3,4\n" +
        "2,2 -> 2,1\n" +
        "7,0 -> 7,4\n" +
        "6,4 -> 2,0\n" +
        "0,9 -> 2,9\n" +
        "3,4 -> 1,4\n" +
        "0,0 -> 8,8\n" +
        "5,5 -> 8,2\n"

private val testInput by lazy { readResourceFile("/advent2021/day05-task1.txt") }