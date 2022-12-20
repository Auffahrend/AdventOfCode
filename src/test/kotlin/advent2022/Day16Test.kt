package advent2022

import BaseTest
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import readResourceFile


class Day16Test : BaseTest() {
    private val factory = { input: String -> Day16(input) }
    private val testInput = "" +
            "Valve AA has flow rate=0; tunnels lead to valves DD, II, BB\n" +
            "Valve BB has flow rate=13; tunnels lead to valves CC, AA\n" +
            "Valve CC has flow rate=2; tunnels lead to valves DD, BB\n" +
            "Valve DD has flow rate=20; tunnels lead to valves CC, AA, EE\n" +
            "Valve EE has flow rate=3; tunnels lead to valves FF, DD\n" +
            "Valve FF has flow rate=0; tunnels lead to valves EE, GG\n" +
            "Valve GG has flow rate=0; tunnels lead to valves FF, HH\n" +
            "Valve HH has flow rate=22; tunnel leads to valve GG\n" +
            "Valve II has flow rate=0; tunnels lead to valves AA, JJ\n" +
            "Valve JJ has flow rate=21; tunnel leads to valve II\n"
    private val taskInput by lazy { readResourceFile("/advent2022/day16-task1.txt") }

    @Test
    fun `test part1`() = measure {
        verifyResult(1651, factory(testInput).solve())
    }

    @Test
    fun solve() = measure {
        println("Result is ${factory(taskInput).solve(true)}")
    }

    @Test
    fun `test part2`() = measure {
        verifyResult(1707, factory(testInput).solve(true))
    }
}