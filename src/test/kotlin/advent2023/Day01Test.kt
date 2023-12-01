package advent2023

import BaseTest
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.MethodOrderer
import org.junit.jupiter.api.TestMethodOrder
import readResourceFile

@TestMethodOrder(MethodOrderer.Alphanumeric::class)
class Day01Test : BaseTest() {
    private val factory = { input: String -> Day01(input) }
    private val testInput = "1abc2\n" +
            "pqr3stu8vwx\n" +
            "a1b2c3d4e5f\n" +
            "treb7uchet\n"
    private val testInput2 = "two1nine\n" +
            "eightwothree\n" +
            "abcone2threexyz\n" +
            "xtwone3four\n" +
            "4nineeightseven2\n" +
            "zoneight234\n" +
            "7pqrstsixteen\n"
    private val taskInput by lazy { readResourceFile("/advent2023/day01.txt") }

    @Test
    fun `1 test part1`() = measure {
        verifyResult(142, factory(testInput).solve())
    }

    @Test
    fun `3 solve`() = measure {
        println("Result is ${factory(taskInput).solve(mode = 2)}")
    }

    @Test
    fun `2 test part2`() = measure {
        verifyResult(281, factory(testInput2).solve(mode = 2))
    }
}