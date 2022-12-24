package advent2022

import BaseTest
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import readResourceFile


class Day22Test : BaseTest() {
    private val factory = { input: String -> Day22(input) }
    private val testInput = "" +
            "        ...#\n" +
            "        .#..\n" +
            "        #...\n" +
            "        ....\n" +
            "...#.......#\n" +
            "........#...\n" +
            "..#....#....\n" +
            "..........#.\n" +
            "        ...#....\n" +
            "        .....#..\n" +
            "        .#......\n" +
            "        ......#.\n" +
            "\n" +
            "10R5L5R10L4R5L5\n"
    private val taskInput by lazy { readResourceFile("/advent2022/day22.txt") }

    @Test
    fun `test part1`() = measure {
        verifyResult(6032, factory(testInput).solve())
    }

    @Test
    fun solve() = measure {
        println("Result is ${factory(taskInput).solve2()}")
    }

    @Test
    fun `test part2`() = measure {
        verifyResult(5031, factory(testInput).solve2())
    }
}