package advent2022

import BaseTest
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import readResourceFile


class Day20Test : BaseTest() {
    private val factory = { input: String -> Day20(input) }
    private val testInput = "1\n" +
            "2\n" +
            "-3\n" +
            "3\n" +
            "-2\n" +
            "0\n" +
            "4\n"
    private val taskInput by lazy { readResourceFile("/advent2022/day20-task1.txt") }

    @Test
    fun `test part1`() = measure {
        verifyResult(3, factory(testInput).solve1())
    }

    @Test
    fun solve() = measure {
        println("Result is ${factory(taskInput).solve2()}")
    }

    @Test
    fun `test part2`() = measure {
        verifyResult(1623178306, factory(testInput).solve2())
    }
}