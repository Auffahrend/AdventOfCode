package advent2022

import BaseTest
import org.junit.jupiter.api.Test

import readResourceFile


class Day09Test : BaseTest() {
    private val factory = { input: String -> Day09(input) }
    private val testInput1 = "R 4\n" +
            "U 4\n" +
            "L 3\n" +
            "D 1\n" +
            "R 4\n" +
            "D 1\n" +
            "L 5\n" +
            "R 2\n"
    private val testInput2 = "R 5\n" +
            "U 8\n" +
            "L 8\n" +
            "D 3\n" +
            "R 17\n" +
            "D 10\n" +
            "L 25\n" +
            "U 20\n"
    private val taskInput by lazy { readResourceFile("/advent2022/day09-task1.txt") }

    @Test
    fun `test part1`() = measure {
        verifyResult(13, factory(testInput1).solve1())
    }

    @Test
    fun solve() = measure {
        println("Result is ${factory(taskInput).solve2()}")
    }

    @Test
    fun `test part2`() = measure {
        verifyResult(36, factory(testInput2).solve2())
    }
}