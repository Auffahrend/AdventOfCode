package advent2021

import measure
import readResourceFile
import verifyResult

/*
--- Day 13: Transparent Origami ---

You reach another volcanically active part of the cave. It would be nice if you could do some kind of thermal imaging so you could tell ahead of time which caves are too hot to safely enter.

Fortunately, the submarine seems to be equipped with a thermal camera! When you activate it, you are greeted with:

Congratulations on your purchase! To activate this infrared thermal imaging
camera system, please enter the code found on page 1 of the manual.

Apparently, the Elves have never used this feature. To your surprise, you manage to find the manual; as you go to open it, page 1 falls out. It's a large sheet of transparent paper! The transparent paper is marked with random dots and includes instructions on how to fold it up (your puzzle input). For example:

6,10
0,14
9,10
0,3
10,4
4,11
6,0
6,12
4,1
0,13
10,12
3,4
3,0
8,4
1,10
2,14
8,10
9,0

fold along y=7
fold along x=5

The first section is a list of dots on the transparent paper. 0,0 represents the top-left coordinate. The first value, x, increases to the right. The second value, y, increases downward. So, the coordinate 3,0 is to the right of 0,0, and the coordinate 0,7 is below 0,0. The coordinates in this example form the following pattern, where # is a dot on the paper and . is an empty, unmarked position:

...#..#..#.
....#......
...........
#..........
...#....#.#
...........
...........
...........
...........
...........
.#....#.##.
....#......
......#...#
#..........
#.#........

Then, there is a list of fold instructions. Each instruction indicates a line on the transparent paper and wants you to fold the paper up (for horizontal y=... lines) or left (for vertical x=... lines). In this example, the first fold instruction is fold along y=7, which designates the line formed by all of the positions where y is 7 (marked here with -):

...#..#..#.
....#......
...........
#..........
...#....#.#
...........
...........
-----------
...........
...........
.#....#.##.
....#......
......#...#
#..........
#.#........

Because this is a horizontal line, fold the bottom half up. Some of the dots might end up overlapping after the fold is complete, but dots will never appear exactly on a fold line. The result of doing this fold looks like this:

#.##..#..#.
#...#......
......#...#
#...#......
.#.#..#.###
...........
...........

Now, only 17 dots are visible.

Notice, for example, the two dots in the bottom left corner before the transparent paper is folded; after the fold is complete, those dots appear in the top left corner (at 0,0 and 0,1). Because the paper is transparent, the dot just below them in the result (at 0,3) remains visible, as it can be seen through the transparent paper.

Also notice that some dots can end up overlapping; in this case, the dots merge together and become a single dot.

The second fold instruction is fold along x=5, which indicates this line:

#.##.|#..#.
#...#|.....
.....|#...#
#...#|.....
.#.#.|#.###
.....|.....
.....|.....

Because this is a vertical line, fold left:

#####
#...#
#...#
#...#
#####
.....
.....

The instructions made a square!

The transparent paper is pretty big, so for now, focus on just completing the first fold. After the first fold in the example above, 17 dots are visible - dots that end up overlapping after the fold is completed count as a single dot.

How many dots are visible after completing just the first fold instruction on your transparent paper?

Your puzzle answer was 737.
--- Part Two ---

Finish folding the transparent paper according to the instructions. The manual says the code is always eight capital letters.

What code do you use to activate the infrared thermal imaging camera system?

 */
private class Day13(
) {
    private lateinit var initialDots: Set<Coords>
    private lateinit var foldInstructions: List<Fold>
    fun solve(folds: Int = 0): Int {
        val result = executeFolds(folds)
        result.print()
        return result.size
    }

    private fun executeFolds(folds: Int): Set<Coords> {
        return (if (folds == 0) foldInstructions else foldInstructions.take(folds))
            .fold(initialDots, this::foldDots)
    }

    private fun foldDots(dots: Set<Coords>, rule: Fold): Set<Coords> {
        return dots.map { (x, y) ->
            if (rule.axis == "x") {
                val newX = if (x <= rule.position) x else rule.position - (x - rule.position)
                newX to y
            } else {
                val newY = if (y <= rule.position) y else rule.position - (y - rule.position)
                x to newY
            }
        }.toSet()
    }

    constructor(testInput: String) : this() {
        initialDots = testInput.lines().takeWhile { it.isNotBlank() }
            .map { it.split(",").let { parts -> parts[0].toInt() to parts[1].toInt() } }.toSet()

        foldInstructions = testInput.lines().dropWhile { it.isNotBlank() }.dropWhile { it.isBlank() }
            .map { it.removePrefix("fold along ") }
            .filter { it.isNotBlank() }
            .map { it.split("=") }
            .map { Fold(it[0], it[1].toInt()) }
    }

    private data class Fold(val axis: String, val position: Int)

}

private fun Set<Coords>.print() {
    val maxX = this.maxOf { it.first }
    val maxY = this.maxOf { it.second }

    for (y in 0..maxY) {
        val buffer = StringBuffer()
        for (x in 0..maxX) if (this.contains(x to y)) buffer.append('#') else buffer.append(" ")
        println(buffer.toString())
    }
    println()
}

fun main() {
    listOf(
        { verifyResult(17, Day13(checkInput).solve(1)) },
        { println("Result is " + Day13(testInput).solve()) }
    ).onEachIndexed { i, test -> measure(test, i) }
}

private const val checkInput: String = "" +
        "6,10\n" +
        "0,14\n" +
        "9,10\n" +
        "0,3\n" +
        "10,4\n" +
        "4,11\n" +
        "6,0\n" +
        "6,12\n" +
        "4,1\n" +
        "0,13\n" +
        "10,12\n" +
        "3,4\n" +
        "3,0\n" +
        "8,4\n" +
        "1,10\n" +
        "2,14\n" +
        "8,10\n" +
        "9,0\n" +
        "\n" +
        "fold along y=7\n" +
        "fold along x=5\n"

private val testInput by lazy { readResourceFile("/advent2021/day13-task1.txt") }