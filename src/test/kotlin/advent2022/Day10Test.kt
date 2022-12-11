package advent2022

import BaseTest
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import readResourceFile


class Day10Test : BaseTest() {
    private val factory = { input: String -> Day10(input) }
    private val testInput = "addx 15\n" +
            "addx -11\n" +
            "addx 6\n" +
            "addx -3\n" +
            "addx 5\n" +
            "addx -1\n" +
            "addx -8\n" +
            "addx 13\n" +
            "addx 4\n" +
            "noop\n" +
            "addx -1\n" +
            "addx 5\n" +
            "addx -1\n" +
            "addx 5\n" +
            "addx -1\n" +
            "addx 5\n" +
            "addx -1\n" +
            "addx 5\n" +
            "addx -1\n" +
            "addx -35\n" +
            "addx 1\n" +
            "addx 24\n" +
            "addx -19\n" +
            "addx 1\n" +
            "addx 16\n" +
            "addx -11\n" +
            "noop\n" +
            "noop\n" +
            "addx 21\n" +
            "addx -15\n" +
            "noop\n" +
            "noop\n" +
            "addx -3\n" +
            "addx 9\n" +
            "addx 1\n" +
            "addx -3\n" +
            "addx 8\n" +
            "addx 1\n" +
            "addx 5\n" +
            "noop\n" +
            "noop\n" +
            "noop\n" +
            "noop\n" +
            "noop\n" +
            "addx -36\n" +
            "noop\n" +
            "addx 1\n" +
            "addx 7\n" +
            "noop\n" +
            "noop\n" +
            "noop\n" +
            "addx 2\n" +
            "addx 6\n" +
            "noop\n" +
            "noop\n" +
            "noop\n" +
            "noop\n" +
            "noop\n" +
            "addx 1\n" +
            "noop\n" +
            "noop\n" +
            "addx 7\n" +
            "addx 1\n" +
            "noop\n" +
            "addx -13\n" +
            "addx 13\n" +
            "addx 7\n" +
            "noop\n" +
            "addx 1\n" +
            "addx -33\n" +
            "noop\n" +
            "noop\n" +
            "noop\n" +
            "addx 2\n" +
            "noop\n" +
            "noop\n" +
            "noop\n" +
            "addx 8\n" +
            "noop\n" +
            "addx -1\n" +
            "addx 2\n" +
            "addx 1\n" +
            "noop\n" +
            "addx 17\n" +
            "addx -9\n" +
            "addx 1\n" +
            "addx 1\n" +
            "addx -3\n" +
            "addx 11\n" +
            "noop\n" +
            "noop\n" +
            "addx 1\n" +
            "noop\n" +
            "addx 1\n" +
            "noop\n" +
            "noop\n" +
            "addx -13\n" +
            "addx -19\n" +
            "addx 1\n" +
            "addx 3\n" +
            "addx 26\n" +
            "addx -30\n" +
            "addx 12\n" +
            "addx -1\n" +
            "addx 3\n" +
            "addx 1\n" +
            "noop\n" +
            "noop\n" +
            "noop\n" +
            "addx -9\n" +
            "addx 18\n" +
            "addx 1\n" +
            "addx 2\n" +
            "noop\n" +
            "noop\n" +
            "addx 9\n" +
            "noop\n" +
            "noop\n" +
            "noop\n" +
            "addx -1\n" +
            "addx 2\n" +
            "addx -37\n" +
            "addx 1\n" +
            "addx 3\n" +
            "noop\n" +
            "addx 15\n" +
            "addx -21\n" +
            "addx 22\n" +
            "addx -6\n" +
            "addx 1\n" +
            "noop\n" +
            "addx 2\n" +
            "addx 1\n" +
            "noop\n" +
            "addx -10\n" +
            "noop\n" +
            "noop\n" +
            "addx 20\n" +
            "addx 1\n" +
            "addx 2\n" +
            "addx 2\n" +
            "addx -6\n" +
            "addx -11\n" +
            "noop\n" +
            "noop\n" +
            "noop\n"
    private val taskInput by lazy { readResourceFile("/advent2022/day10-task1.txt") }

    @Test
    fun `test part1`() = measure {
        verifyResult(13140, factory(testInput).solve1())
    }

    @Test
    fun solve() = measure {
        println("Result is \n${factory(taskInput).solve2()}")
    }

    @Test
    fun `test part2`() = measure {
        verifyResult(
            "##..##..##..##..##..##..##..##..##..##..\n" +
                    "###...###...###...###...###...###...###.\n" +
                    "####....####....####....####....####....\n" +
                    "#####.....#####.....#####.....#####.....\n" +
                    "######......######......######......####\n" +
                    "#######.......#######.......#######.....",
            factory(testInput).solve2()
        )
    }
}