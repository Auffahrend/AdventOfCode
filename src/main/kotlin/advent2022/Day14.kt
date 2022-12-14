package advent2022

import Coords
import contains
import plus
import minus
import get
import set
import kotlin.math.max
import kotlin.math.min

/*
https://adventofcode.com/2022/day/14
 */

private const val AIR = '.'
private const val ROCK = '#'
private const val SAND = 'o'

class Day14(testInput: String, val intoVoid: Boolean = true) {
    private val grid = mutableListOf<MutableList<Char>>()
    private val source: Coords

    init {
        val paths = testInput.lines().filter { it.isNotEmpty() }
            .map { it.split(" -> ") }
            .map { pairs -> pairs.map { it.split(",").let { (f, s) -> f.toInt() to s.toInt() } } }

        val allPoints = paths.flatMap { it } + (500 to 0)
        var topLeft = allPoints.minBy { (x, _) -> x }.first to allPoints.minBy { (_, y) -> y }.second
        var bottomRight = allPoints.maxBy { (x, _) -> x }.first to allPoints.maxBy { (_, y) -> y }.second
        // starting from 0..0
        val floorLevel = bottomRight.second + 2
        if (!intoVoid) {
            topLeft = min(topLeft.first, 500 - floorLevel - 2) to topLeft.second
            bottomRight = max(bottomRight.first, 500 + floorLevel + 2) to (bottomRight.second + 2)
        }

        val offset = topLeft
        // all coordinates below must be relative
        source = (500 to 0) - offset
        val size = bottomRight - offset

        // empty grid
        (0..size.second).forEach {
            (0..size.first).map { AIR }
                .also { grid.add(it.toMutableList()) }
        }

        paths.forEach {
            it.windowed(2).forEach { (from, to) -> grid.addRocks(from - offset, to - offset) }
        }
        if (!intoVoid) grid.addRocks((0 to grid.size-1), (grid[0].size-1 to grid.size-1))
    }

    private fun fillWithSand() {
        do {
            val settled = grid.fallSand(source)
        } while (settled != null && settled != source)
    }

    fun solve(): Int {
        fillWithSand()
        return grid.flatten().count { it == SAND }
    }
}


private fun MutableList<MutableList<Char>>.fallSand(source: Pair<Int, Int>): Coords? {
    var sand = source
    while (this.contains(sand)) {
        val down = sand + (0 to 1)
        val left = sand + (-1 to 1)
        val right = sand + (1 to 1)
        if (!this.contains(down) || this[down] == AIR) sand = down
        else if (!this.contains(left) || this[left] == AIR) sand = left
        else if (!this.contains(right) || this[right] == AIR) sand = right
        else {
            this[sand] = SAND
            return sand
        }
    }
    return null
}

private fun MutableList<MutableList<Char>>.addRocks(from: Coords, to: Coords) {
    if (from.first == to.first) {
        (min(from.second, to.second)..max(from.second, to.second)).forEach { y -> this[from.first to y] = ROCK }
    } else if (from.second == to.second) {
        (min(from.first, to.first)..max(from.first, to.first)).forEach { x -> this[x to from.second] = ROCK }
    } else throw IllegalArgumentException("A diagonal line from $from to $to")
}