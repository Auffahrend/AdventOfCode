package advent2021

import measure
import readResourceFile
import verifyResult

/*
--- Day 4: Giant Squid ---

You're already almost 1.5km (almost a mile) below the surface of the ocean, already so deep that you can't see any sunlight. What you can see, however, is a giant squid that has attached itself to the outside of your submarine.

Maybe it wants to play bingo?

Bingo is played on a set of boards each consisting of a 5x5 grid of numbers. Numbers are chosen at random, and the chosen number is marked on all boards on which it appears. (Numbers may not appear on all boards.) If all numbers in any row or any column of a board are marked, that board wins. (Diagonals don't count.)

The submarine has a bingo subsystem to help passengers (currently, you and the giant squid) pass the time. It automatically generates a random order in which to draw numbers and a random set of boards (your puzzle input). For example:

7,4,9,5,11,17,23,2,0,14,21,24,10,16,13,6,15,25,12,22,18,20,8,19,3,26,1

22 13 17 11  0
 8  2 23  4 24
21  9 14 16  7
 6 10  3 18  5
 1 12 20 15 19

 3 15  0  2 22
 9 18 13 17  5
19  8  7 25 23
20 11 10 24  4
14 21 16 12  6

14 21 17 24  4
10 16 15  9 19
18  8 23 26 20
22 11 13  6  5
 2  0 12  3  7

After the first five numbers are drawn (7, 4, 9, 5, and 11), there are no winners, but the boards are marked as follows (shown here adjacent to each other to save space):

22 13 17 11  0         3 15  0  2 22        14 21 17 24  4
 8  2 23  4 24         9 18 13 17  5        10 16 15  9 19
21  9 14 16  7        19  8  7 25 23        18  8 23 26 20
 6 10  3 18  5        20 11 10 24  4        22 11 13  6  5
 1 12 20 15 19        14 21 16 12  6         2  0 12  3  7

After the next six numbers are drawn (17, 23, 2, 0, 14, and 21), there are still no winners:

22 13 17 11  0         3 15  0  2 22        14 21 17 24  4
 8  2 23  4 24         9 18 13 17  5        10 16 15  9 19
21  9 14 16  7        19  8  7 25 23        18  8 23 26 20
 6 10  3 18  5        20 11 10 24  4        22 11 13  6  5
 1 12 20 15 19        14 21 16 12  6         2  0 12  3  7

Finally, 24 is drawn:

22 13 17 11  0         3 15  0  2 22        14 21 17 24  4
 8  2 23  4 24         9 18 13 17  5        10 16 15  9 19
21  9 14 16  7        19  8  7 25 23        18  8 23 26 20
 6 10  3 18  5        20 11 10 24  4        22 11 13  6  5
 1 12 20 15 19        14 21 16 12  6         2  0 12  3  7

At this point, the third board wins because it has at least one complete row or column of marked numbers (in this case, the entire top row is marked: 14 21 17 24 4).

The score of the winning board can now be calculated. Start by finding the sum of all unmarked numbers on that board; in this case, the sum is 188. Then, multiply that sum by the number that was just called when the board won, 24, to get the final score, 188 * 24 = 4512.

To guarantee victory against the giant squid, figure out which board will win first. What will your final score be if you choose that board?

--- Part Two ---

On the other hand, it might be wise to try a different strategy: let the giant squid win.

You aren't sure how many bingo boards a giant squid could play at once, so rather than waste time counting its arms, the safe thing to do is to figure out which board will win last and choose that one. That way, no matter which boards it picks, it will win for sure.

In the above example, the second board is the last to win, which happens after 13 is eventually called and its middle column is completely marked. If you were to keep playing until this point, the second board would have a sum of unmarked numbers equal to 148 for a final score of 148 * 13 = 1924.

Figure out which board will win last. Once it wins, what would its final score be?

 */
private class Day04(
) {
    private lateinit var numbers: List<Int>
    private lateinit var boards: List<BingoBoard>

    fun solve(): Int {
        val winningNumber = numbers.first { n -> boards.onEach { it.draw(n) }.any { it.isWinner() } }
        val winner = boards.first { it.isWinner() }
        return winner.columns.flatMap { it.left }.sum() * winningNumber
    }

    fun solve2(): Int {
        val allWinners: MutableSet<BingoBoard> = mutableSetOf()
        val lastWinningNumber = numbers.first { n ->
            boards.onEach {
                it.draw(n)
                if (it.isWinner() && !allWinners.contains(it)) allWinners.add(it)
            }.all { it.isWinner() }
        }
        return allWinners.last().columns.flatMap { it.left }.sum() * lastWinningNumber
    }

    constructor(testInput: String) : this() {
        numbers = testInput.lines().first().split(",").map { it.toInt() }
        boards = testInput.lines().drop(1).filterNot { it.isEmpty() }
            .map { it.trim().split(Regex("\\s+")).map { it.toInt() } }
            .chunked(5)
            .map { BingoBoard(it) }
    }

    private class BingoBoard(numbers: List<List<Int>>) {
        val ranks: List<Line> = numbers.map { Line((it)) }
        val columns: List<Line> = numbers.first().mapIndexed { i, _ -> numbers.map { it[i] } }.map { Line(it) }

        fun draw(number: Int) {
            ranks.onEach { it.draw(number) }
            columns.onEach { it.draw(number) }
        }

        fun isWinner() = (ranks + columns).any { it.left.isEmpty() }
    }

    private class Line(numbers: List<Int>) {
        val drawn: MutableSet<Int> = mutableSetOf()
        val left: MutableSet<Int> = numbers.toMutableSet()
        fun draw(number: Int) {
            if (left.remove(number)) drawn.add(number)
        }
    }

}

fun main() {
    listOf(
        { verifyResult(4512, Day04(checkInput).solve()) },
        { verifyResult(1924, Day04(checkInput).solve2()) },
        { println("Result is " + Day04(testInput).solve2()) }
    ).onEachIndexed { i, test -> measure(test, i) }
}

private const val checkInput: String = "" +
        "7,4,9,5,11,17,23,2,0,14,21,24,10,16,13,6,15,25,12,22,18,20,8,19,3,26,1\n" +
        "\n" +
        "22 13 17 11  0\n" +
        " 8  2 23  4 24\n" +
        "21  9 14 16  7\n" +
        " 6 10  3 18  5\n" +
        " 1 12 20 15 19\n" +
        "\n" +
        " 3 15  0  2 22\n" +
        " 9 18 13 17  5\n" +
        "19  8  7 25 23\n" +
        "20 11 10 24  4\n" +
        "14 21 16 12  6\n" +
        "\n" +
        "14 21 17 24  4\n" +
        "10 16 15  9 19\n" +
        "18  8 23 26 20\n" +
        "22 11 13  6  5\n" +
        " 2  0 12  3  7"

private val testInput by lazy { readResourceFile("/advent2021/day04-task1.txt") }