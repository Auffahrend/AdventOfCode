import BaseTest
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.MethodOrderer
import org.junit.jupiter.api.TestMethodOrder
import readResourceFile


@TestMethodOrder(MethodOrderer.Alphanumeric::class)
class DayXXTest : BaseTest() {
    private val factory = { input: String -> DayXX(input) }
    private val testInput = ""
    private val taskInput by lazy { readResourceFile("/advent2023/dayXX.txt") }

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