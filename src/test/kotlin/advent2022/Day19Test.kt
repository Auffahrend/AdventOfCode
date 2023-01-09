package advent2022

import BaseTest
import log
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import readResourceFile


class Day19Test : BaseTest() {
    private val factory = { input: String -> Day19(input) }
    private val testInput = "Blueprint 1:\n" +
            "  Each ore robot costs 4 ore.\n" +
            "  Each clay robot costs 2 ore.\n" +
            "  Each obsidian robot costs 3 ore and 14 clay.\n" +
            "  Each geode robot costs 2 ore and 7 obsidian.\n" +
            "\n" +
            "Blueprint 2:\n" +
            "  Each ore robot costs 2 ore.\n" +
            "  Each clay robot costs 3 ore.\n" +
            "  Each obsidian robot costs 3 ore and 8 clay.\n" +
            "  Each geode robot costs 3 ore and 12 obsidian.\n"
    private val taskInput by lazy { readResourceFile("/advent2022/day19.txt") }

    @Test
    fun `1 test part1`() = measure {
        verifyResult(33, factory(testInput).solve())
    }

    @Test
    fun `3 solve`() = measure {
        javaClass.log("Result is ${factory(taskInput).solve(32, 3)}")
    }

    @Test
    fun `2 test part2`() = measure {
        verifyResult(56 * 62, factory(testInput).solve(32, 3))
    }
}