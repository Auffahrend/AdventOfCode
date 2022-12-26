import BaseTest
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import readResourceFile


class DayXXTest : BaseTest() {
    private val factory = { input: String -> DayXX(input) }
    private val testInput = ""
    private val taskInput by lazy { readResourceFile("/advent2022/dayXX.txt") }

    @Test
    fun `1 test part1`() = measure {
        verifyResult(0, factory(testInput).solve())
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