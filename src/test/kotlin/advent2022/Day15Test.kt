package advent2022

import BaseTest
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import readResourceFile


class Day15Test : BaseTest() {
    private val factory = { input: String -> Day15(input) }
    private val testInput = "Sensor at x=2, y=18: closest beacon is at x=-2, y=15\n" +
            "Sensor at x=9, y=16: closest beacon is at x=10, y=16\n" +
            "Sensor at x=13, y=2: closest beacon is at x=15, y=3\n" +
            "Sensor at x=12, y=14: closest beacon is at x=10, y=16\n" +
            "Sensor at x=10, y=20: closest beacon is at x=10, y=16\n" +
            "Sensor at x=14, y=17: closest beacon is at x=10, y=16\n" +
            "Sensor at x=8, y=7: closest beacon is at x=2, y=10\n" +
            "Sensor at x=2, y=0: closest beacon is at x=2, y=10\n" +
            "Sensor at x=0, y=11: closest beacon is at x=2, y=10\n" +
            "Sensor at x=20, y=14: closest beacon is at x=25, y=17\n" +
            "Sensor at x=17, y=20: closest beacon is at x=21, y=22\n" +
            "Sensor at x=16, y=7: closest beacon is at x=15, y=3\n" +
            "Sensor at x=14, y=3: closest beacon is at x=15, y=3\n" +
            "Sensor at x=20, y=1: closest beacon is at x=15, y=3\n"
    private val taskInput by lazy { readResourceFile("/advent2022/day15-task1.txt") }

    @Test
    fun `test part1`() = measure {
        verifyResult(26, factory(testInput).solve1(10))
    }

    @Test
    fun solve1() = measure {
        println("Result 1 is ${factory(taskInput).solve1(2000000)}")
    }

    @Test
    fun `test part2`() = measure {
        verifyResult(56000011, factory(testInput).solve2(0..20))
    }

    @Test
    fun solve2() = measure {
        println("Result 2 is ${factory(taskInput).solve2(0..4000000)}")
    }
}