package advent2022

import BaseTest
import org.junit.jupiter.api.Test

import readResourceFile


class Day12Test : BaseTest() {
    private val factory = { input: String -> Day12(input) }
    private val testInput = "Sabqponm\n" +
            "abcryxxl\n" +
            "accszExk\n" +
            "acctuvwj\n" +
            "abdefghi\n"
    private val taskInput by lazy { readResourceFile("/advent2022/day12-task1.txt") }

    @Test
    fun `test part1`() = measure {
        verifyResult(31, factory(testInput).solve1())
    }

    @Test
    fun solve() = measure {
        println("Result is ${factory(taskInput).solve2()}")
    }

    @Test
    fun `test part2`() = measure {
        verifyResult(29, factory(testInput).solve2())
    }
}