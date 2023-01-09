package advent2022

import BaseTest
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import readResourceFile


class Day17Test : BaseTest() {
    private val factory = { input: String -> Day17(input) }
    private val testInput = ">>><<><>><<<>><>>><<<>>><<<><<<>><>><<>>"
    private val taskInput by lazy { readResourceFile("/advent2022/day17-task1.txt") }

    @Test
    fun `test part1`() = measure {
        verifyResult(3068, factory(testInput).solve(2022))
    }

    @Test
    fun solve() = measure {
        println("Result is ${factory(taskInput).solve(4000)}")
    }

    @Test
    fun findDiscrepancy() {
        (1000..10000).forEach { rocks ->
            val calcResult = factory(taskInput).solve(rocks.toLong())
            val emulateResult = factory(taskInput).solveEmulate(rocks)
            if (calcResult != emulateResult) {
                throw RuntimeException("Discrepancy found for #rocks = $rocks. Calculated result = $calcResult, emulated = $emulateResult")
            }
        }
    }

    @Test
    fun `test part2`() = measure {
        verifyResult(1514285714288L, factory(testInput).solve(1000000000000))
    }
}