package advent2022

import BaseTest
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import readResourceFile


class Day21Test : BaseTest() {
    private val factory = { input: String -> Day21(input) }
    private val testInput = "root: pppw + sjmn\n" +
            "dbpl: 5\n" +
            "cczh: sllz + lgvd\n" +
            "zczc: 2\n" +
            "ptdq: humn - dvpt\n" +
            "dvpt: 3\n" +
            "lfqf: 4\n" +
            "humn: 5\n" +
            "ljgn: 2\n" +
            "sjmn: drzm * dbpl\n" +
            "sllz: 4\n" +
            "pppw: cczh / lfqf\n" +
            "lgvd: ljgn * ptdq\n" +
            "drzm: hmdt - zczc\n" +
            "hmdt: 32\n"
    private val taskInput by lazy { readResourceFile("/advent2022/day21.txt") }

    @Test
    fun `test part1`() = measure {
        verifyResult(152, factory(testInput).solve())
    }

    @Test
    fun solve() = measure {
        println("Result is ${factory(taskInput).solve2()}")
    }

    @Test
    fun `test part2`() = measure {
        verifyResult(301, factory(testInput).solve2())
    }
}