package advent2021

import measure
import readResourceFile
import verifyResult
import java.util.*

/*

 */
private class Day10(
    private val lines: List<CharArray>
) {
    private val bracketPairs = mapOf(
        '(' to ')',
        '[' to ']',
        '{' to '}',
        '<' to '>',
    )
    private val illegalCharScores = mapOf(
        ')' to 3,
        ']' to 57,
        '}' to 1197,
        '>' to 25137,
    )
    private val completionCharScores = mapOf(
        ')' to 1,
        ']' to 2,
        '}' to 3,
        '>' to 4,
    )

    fun solve1(): Int =
        lines.mapNotNull { firstCorrupt(it) }
            .sumOf { illegalCharScores[it] ?: 0 }

    fun solve2(): Long =
        lines.filter { firstCorrupt(it) == null }
            .map { completionLine(it) }
            .map { completionScore(it) }
            .let { scores -> scores.sortedBy { it }[scores.size / 2] }

    private fun completionLine(line: CharArray): CharArray {
        val stack = LinkedList<Char>()
        line.onEach { c ->
            if (bracketPairs.containsKey(c)) stack.addFirst(c) else stack.removeFirst()
        }
        return stack.map { bracketPairs[it]!! }.toCharArray()
    }

    private fun completionScore(line: CharArray): Long =
        line.fold(0) { a, c -> a * 5 + completionCharScores[c]!! }

    private fun firstCorrupt(line: CharArray): Char? {
        val stack = LinkedList<Char>()
        line.onEach { c ->
            if (illegalCharScores.containsKey(c)) {
                val opening = stack.removeFirst()
                if (bracketPairs[opening] != c) return c
            } else stack.addFirst(c)
        }
        return null
    }

    constructor(testInput: String) : this(testInput.lines()
        .filter { it.isNotBlank() }
        .map { it.toCharArray() }
    )
}

fun main() {
    listOf(
        { verifyResult(26397, Day10(checkInput).solve1()) },
        { verifyResult(288957, Day10(checkInput).solve2()) },
        { println("Result is " + Day10(testInput).solve2()) }
    ).onEachIndexed { i, test -> measure(test, i) }
}

private const val checkInput: String = "" +
        "[({(<(())[]>[[{[]{<()<>>\n" +
        "[(()[<>])]({[<{<<[]>>(\n" +
        "{([(<{}[<>[]}>{[]{[(<()>\n" +
        "(((({<>}<{<{<>}{[]{[]{}\n" +
        "[[<[([]))<([[{}[[()]]]\n" +
        "[{[{({}]{}}([{[{{{}}([]\n" +
        "{<[[]]>}<{[{[{[]{()[[[]\n" +
        "[<(<(<(<{}))><([]([]()\n" +
        "<{([([[(<>()){}]>(<<{{\n" +
        "<{([{{}}[<[[[<>{}]]]>[]]\n"

private val testInput by lazy { readResourceFile("/advent2021/day10-task1.txt") }