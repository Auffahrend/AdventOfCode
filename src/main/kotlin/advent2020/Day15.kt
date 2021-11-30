package advent2020

import measure
import readResourceFile
import verifyResult

/*
--- Day 15: Rambunctious Recitation ---

You catch the airport shuttle and try to book a new flight to your vacation island. Due to the storm, all direct flights have been cancelled, but a route is available to get around the storm. You take it.

While you wait for your flight, you decide to check in with the Elves back at the North Pole. They're playing a memory game and are ever so excited to explain the rules!

In this game, the players take turns saying numbers. They begin by taking turns reading from a list of starting numbers (your puzzle input). Then, each turn consists of considering the most recently spoken number:

    If that was the first time the number has been spoken, the current player says 0.
    Otherwise, the number had been spoken before; the current player announces how many turns apart the number is from when it was previously spoken.

So, after the starting numbers, each turn results in that player speaking aloud either 0 (if the last number is new) or an age (if the last number is a repeat).

For example, suppose the starting numbers are 0,3,6:

    Turn 1: The 1st number spoken is a starting number, 0.
    Turn 2: The 2nd number spoken is a starting number, 3.
    Turn 3: The 3rd number spoken is a starting number, 6.
    Turn 4: Now, consider the last number spoken, 6. Since that was the first time the number had been spoken, the 4th number spoken is 0.
    Turn 5: Next, again consider the last number spoken, 0. Since it had been spoken before, the next number to speak is the difference between the turn number when it was last spoken (the previous turn, 4) and the turn number of the time it was most recently spoken before then (turn 1). Thus, the 5th number spoken is 4 - 1, 3.
    Turn 6: The last number spoken, 3 had also been spoken before, most recently on turns 5 and 2. So, the 6th number spoken is 5 - 2, 3.
    Turn 7: Since 3 was just spoken twice in a row, and the last two turns are 1 turn apart, the 7th number spoken is 1.
    Turn 8: Since 1 is new, the 8th number spoken is 0.
    Turn 9: 0 was last spoken on turns 8 and 4, so the 9th number spoken is the difference between them, 4.
    Turn 10: 4 is new, so the 10th number spoken is 0.

(The game ends when the Elves get sick of playing or dinner is ready, whichever comes first.)

Their question for you is: what will be the 2020th number spoken? In the example above, the 2020th number spoken will be 436.

Here are a few more examples:

    Given the starting numbers 1,3,2, the 2020th number spoken is 1.
    Given the starting numbers 2,1,3, the 2020th number spoken is 10.
    Given the starting numbers 1,2,3, the 2020th number spoken is 27.
    Given the starting numbers 2,3,1, the 2020th number spoken is 78.
    Given the starting numbers 3,2,1, the 2020th number spoken is 438.
    Given the starting numbers 3,1,2, the 2020th number spoken is 1836.

Given your starting numbers, what will be the 2020th number spoken?

--- Part Two ---

Impressed, the Elves issue you a challenge: determine the 30000000th number spoken. For example, given the same starting numbers as above:

    Given 0,3,6, the 30000000th number spoken is 175594.
    Given 1,3,2, the 30000000th number spoken is 2578.
    Given 2,1,3, the 30000000th number spoken is 3544142.
    Given 1,2,3, the 30000000th number spoken is 261214.
    Given 2,3,1, the 30000000th number spoken is 6895259.
    Given 3,2,1, the 30000000th number spoken is 18.
    Given 3,1,2, the 30000000th number spoken is 362.

Given your starting numbers, what will be the 30000000th number spoken?

 */
private class Day15(
    private var lastNumber: Int = -1,
    private var lastIndex: Int = -1,
    private val numberIndexes: MutableMap<Int, Pair<Int?, Int?>> = mutableMapOf()
) {
    fun solve(position: Int = 30_000_000): Int {
        while (lastIndex < position - 1) calculateAndAddNextNumber()
        return lastNumber
    }

    private fun calculateAndAddNextNumber() {
        val nextNumber = numberIndexes[lastNumber]!!.let { (previousIndex, lastIndex) ->
            if (previousIndex == null || lastIndex == null) 0
            else lastIndex - previousIndex
        }
        addNextNumber(nextNumber)
    }

    constructor(testInput: String) : this() {
        testInput.split(",")
            .map { it.toInt() }
            .onEach { addNextNumber(it) }
    }

    private fun addNextNumber(number: Int) {
        lastNumber = number
        lastIndex++
        val last2Indexes = (numberIndexes[number] ?: (null to null))
            .let { (_, last) -> last to lastIndex }
        numberIndexes[number] = last2Indexes
    }

}

fun main() {
    listOf(
        { verifyResult(175594, Day15(checkInput1).solve()) },
        { verifyResult(2578, Day15(checkInput2).solve()) },
        { verifyResult(3544142, Day15(checkInput3).solve()) },
        { verifyResult(261214, Day15(checkInput4).solve()) },
        { verifyResult(6895259, Day15(checkInput5).solve()) },
        { verifyResult(18, Day15(checkInput6).solve()) },
        { verifyResult(362, Day15(checkInput7).solve()) },
        { println("Result is " + Day15(testInput).solve(30_000_000)) },
    ).onEachIndexed { i, test ->
        measure(test, i)
    }
}

private const val checkInput1: String = "0,3,6"
private const val checkInput2: String = "1,3,2"
private const val checkInput3: String = "2,1,3"
private const val checkInput4: String = "1,2,3"
private const val checkInput5: String = "2,3,1"
private const val checkInput6: String = "3,2,1"
private const val checkInput7: String = "3,1,2"

private val testInput by lazy { readResourceFile("/advent2020/day15-task1.txt") }