package advent2022

import BaseTest
import org.junit.jupiter.api.Test

import readResourceFile


class Day13Test : BaseTest() {
    private val factory = { input: String -> Day13(input) }
    private val testInput = "[1,1,3,1,1]\n" +
            "[1,1,5,1,1]\n" +
            "\n" +
            "[[1],[2,3,4]]\n" +
            "[[1],4]\n" +
            "\n" +
            "[9]\n" +
            "[[8,7,6]]\n" +
            "\n" +
            "[[4,4],4,4]\n" +
            "[[4,4],4,4,4]\n" +
            "\n" +
            "[7,7,7,7]\n" +
            "[7,7,7]\n" +
            "\n" +
            "[]\n" +
            "[3]\n" +
            "\n" +
            "[[[]]]\n" +
            "[[]]\n" +
            "\n" +
            "[1,[2,[3,[4,[5,6,7]]]],8,9]\n" +
            "[1,[2,[3,[4,[5,6,0]]]],8,9]\n"
    private val taskInput by lazy { readResourceFile("/advent2022/day13-task1.txt") }

    @Test
    fun `test part1`() = measure {
        verifyResult(13, factory(testInput).solve())
    }

    @Test
    fun solve() = measure {
        println("Result is ${factory(taskInput).solve2()}")
    }

    @Test
    fun `test part2`() = measure {
        verifyResult(140, factory(testInput).solve2())
    }
}