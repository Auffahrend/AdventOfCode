package advent2022

import BaseTest
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import readResourceFile


class Day25Test : BaseTest() {
    private val factory = { input: String -> Day25(input) }
    private val testInput = "1=-0-2\n" +
            "12111\n" +
            "2=0=\n" +
            "21\n" +
            "2=01\n" +
            "111\n" +
            "20012\n" +
            "112\n" +
            "1=-1=\n" +
            "1-12\n" +
            "12\n" +
            "1=\n" +
            "122\n"
    private val taskInput by lazy { readResourceFile("/advent2022/day25.txt") }

    @Test
    fun `1 test part1`() = measure {
        verifyResult("2=-1=0", factory(testInput).solve())
    }

    @Test
    fun `3 solve`() = measure {
        println("Result is ${factory(taskInput).solve()}")
    }

    @Test
    fun `2 test part2`() = measure {
        verifyResult(0, factory(testInput).solve())
    }
}