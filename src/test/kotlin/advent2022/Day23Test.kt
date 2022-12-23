package advent2022

import BaseTest
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import readResourceFile


class Day23Test : BaseTest() {
    private val factory = { input: String -> Day23(input) }
    private val testInput = "....#..\n" +
            "..###.#\n" +
            "#...#.#\n" +
            ".#...##\n" +
            "#.###..\n" +
            "##.#.##\n" +
            ".#..#..\n"
    private val taskInput by lazy { readResourceFile("/advent2022/day23.txt") }

    @Test
    fun `test part1`() = measure {
        verifyResult(110, factory(testInput).solve())
    }

    @Test
    fun solve() = measure {
        println("Result is ${factory(taskInput).solve2()}")
    }

    @Test
    fun `test part2`() = measure {
        verifyResult(20, factory(testInput).solve2())
    }
}