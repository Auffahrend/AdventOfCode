package advent2022

import BaseTest
import org.junit.jupiter.api.Test

import readResourceFile

class Day08Test : BaseTest() {
    private val factory = { input: String -> Day08(input) }
    private val testInput = "30373\n" +
            "25512\n" +
            "65332\n" +
            "33549\n" +
            "35390\n"
    private val taskInput by lazy { readResourceFile("/advent2022/day08-task1.txt") }

    @Test
    fun `test part1`() = measure {
        verifyResult(21, factory(testInput).solve1())
    }

    @Test
    fun solve1() = measure {
        println("Result is ${factory(taskInput).solve1()}")
    }

    @Test
    fun `test part2`() = measure {
        verifyResult(8, factory(testInput).solve2())
    }

    @Test
    fun solve2() = measure {
        println("Result is ${factory(taskInput).solve2()}")
    }
}