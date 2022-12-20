package advent2022

import BaseTest
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import readResourceFile


class Day18Test : BaseTest() {
    private val factory = { input: String -> Day18(input) }
    private val testInput = "2,2,2\n" +
            "1,2,2\n" +
            "3,2,2\n" +
            "2,1,2\n" +
            "2,3,2\n" +
            "2,2,1\n" +
            "2,2,3\n" +
            "2,2,4\n" +
            "2,2,6\n" +
            "1,2,5\n" +
            "3,2,5\n" +
            "2,1,5\n" +
            "2,3,5\n"
    private val taskInput by lazy { readResourceFile("/advent2022/day18-task1.txt") }

    @Test
    fun `test part1`() = measure {
        verifyResult(64, factory(testInput).solve1())
    }

    @Test
    fun solve() = measure {
        println("Result is ${factory(taskInput).solve2()}")
    }

    @Test
    fun `test part2`() = measure {
        verifyResult(58, factory(testInput).solve2())
    }
}