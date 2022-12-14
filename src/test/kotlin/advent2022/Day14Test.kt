package advent2022

import BaseTest
import org.junit.jupiter.api.Test

import readResourceFile


class Day14Test : BaseTest() {
    private val testInput = "498,4 -> 498,6 -> 496,6\n" +
            "503,4 -> 502,4 -> 502,9 -> 494,9\n"
    private val taskInput by lazy { readResourceFile("/advent2022/day14-task1.txt") }

    @Test
    fun `test part1`() = measure {
        verifyResult(24, Day14(testInput).solve())
    }

    @Test
    fun solve() = measure {
        println("Result is ${Day14(taskInput, false).solve()}")
    }

    @Test
    fun `test part2`() = measure {
        verifyResult(93, Day14(testInput, false).solve())
    }
}