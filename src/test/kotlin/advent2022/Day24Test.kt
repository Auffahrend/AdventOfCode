package advent2022

import BaseTest
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import readResourceFile


class Day24Test : BaseTest() {
    private val factory = { input: String -> Day24(input) }
    private val testInput = "" +
            "#.######\n" +
            "#>>.<^<#\n" +
            "#.<..<<#\n" +
            "#>v.><>#\n" +
            "#<^v^^>#\n" +
            "######.#\n"
    private val taskInput by lazy { readResourceFile("/advent2022/day24.txt") }

    @Test
    fun `1 test part1`() = measure {
        verifyResult(18, factory(testInput).solve())
    }

    @Test
    fun `3 solve`() = measure {
        println("Result is ${factory(taskInput).solve2()}")
    }

    @Test
    fun `2 test part2`() = measure {
        verifyResult(54, factory(testInput).solve2())
    }
}