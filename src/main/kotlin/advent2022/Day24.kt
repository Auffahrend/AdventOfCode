package advent2022

import Coords
import get
import minus
import neighbours4
import java.util.TreeSet
import kotlin.Comparator
import kotlin.math.abs

/*

 */
class Day24(testInput: String) {
    private val start = 0 to -1
    private val end: Coords
    private val externalWall: Coords
    private val blizzardMaps = mutableMapOf<Int, BlizzardMap>()

    init {
        val map = testInput.lines().filterNot { it.isEmpty() }
            .drop(1).dropLast(1)
            .map { it.drop(1).dropLast(1) }
        end = map[0].length - 1 to map.size
        externalWall = map[0].length to map.size
        map.flatMapIndexed { y, r -> r.mapIndexed { x, c -> x to y to Direction.of(c) } }
            .mapNotNull { (c, d) -> if (d == null) null else c to listOf(d) }
            .toMap()
            .let { BlizzardMap(it) }
            .also { blizzardMaps[0] = it }
    }

    private enum class Direction(val move: (Coords) -> Coords, val repr: Char) {
        North({ (x, y) -> x to y - 1 }, '^'),
        South({ (x, y) -> x to y + 1 }, 'v'),
        West({ (x, y) -> x - 1 to y }, '<'),
        East({ (x, y) -> x + 1 to y }, '>');

        companion object {
            fun of(char: Char): Direction? = Direction.values().firstOrNull { it.repr == char }
        }
    }

    private data class BlizzardMap(val blizzards: Map<Coords, List<Direction>>) {
        fun isEmpty(point: Coords) = blizzards[point]?.isEmpty() ?: true
    }

    private fun BlizzardMap.debug(): String =
        (0 until externalWall.second).map { y ->
            (0 until externalWall.first).joinToString(separator = "") { x ->
                blizzards[x to y]?.let { if (it.size == 1) it.first().repr.toString() else it.size.toString() } ?: "."
            }
        }.joinToString(separator = "\n")

    private fun Coords.isPassable(step: Int) = this == start || this == end ||
            (first in 0 until externalWall.first && second in 0 until externalWall.second
                    && blizzardMap(step).isEmpty(this))

    private fun BlizzardMap.next(): BlizzardMap = blizzards.entries
        .flatMap { (c, ds) -> ds.map { it to it.move(c) } }
        .map { (d, c) ->
            d to c.let { (x, y) ->
                (if (x < 0) externalWall.first - 1 else x % externalWall.first) to
                        if (y < 0) externalWall.second - 1 else y % externalWall.second
            }
        }
        .groupBy({ (_, c) -> c }, { (d, _) -> d })
        .let { BlizzardMap(it) }

    private fun blizzardMap(step: Int): BlizzardMap = blizzardMaps.getOrPut(step) {
        val max = blizzardMaps.keys.max()
        var last = blizzardMaps[max]!!
        (max + 1..step).forEach { i ->
            last = last.next().also { blizzardMaps[i] = it }
        }
        blizzardMaps[step]!!
    }

    fun solve(): Int {
        return shortestPath(start, end, 0, comparatorToGoForward)
    }

    fun solve2(): Int {
        val r1 = shortestPath(start, end, 0, comparatorToGoForward)
        val r2 = shortestPath(end, start, r1, comparatorToGoBackward)
        return shortestPath(start, end, r2, comparatorToGoForward)
    }

    private data class Position(val coords: Coords, val step: Int)

    // BFS limited by bestPath, prioritizing going to the end (priority = x + y)
    private val comparatorToGoForward = Comparator
        .comparingInt<Position?> { it.step }
        .thenComparing(Comparator
            .comparingInt<Position?> { it.coords.first }
            .thenComparingInt { it.coords.second }
            .reversed()
        )

    // BFS limited by bestPath, prioritizing going to the start (priority = -(x + y))
    private val comparatorToGoBackward = Comparator
        .comparingInt<Position?> { it.step }
        .thenComparing(Comparator
            .comparingInt<Position?> { it.coords.first }
            .thenComparingInt { it.coords.second }
        )

    private fun shortestPath(from: Coords, to: Coords, startAtStep: Int, comparator: Comparator<Position>): Int {
        var bestPath = Int.MAX_VALUE
        val pathOptions = TreeSet(comparator)
        pathOptions.add(Position(from, startAtStep))
        while (pathOptions.isNotEmpty()) {
            val (coords, step) = pathOptions.pollFirst()!!
            if (coords == to && bestPath > step) {
                bestPath = step
                println("Found path with $bestPath steps")
            }

            val minStepsLeft = (to - coords).let { (dx, dy) -> abs(dx) + abs(dy) }
            if (step + minStepsLeft < bestPath) {
                (coords.neighbours4() + coords)
                    .filter { it.isPassable(step + 1) }
                    .forEach { pathOptions.add(Position(it, step + 1)) }
            }
        }
        return bestPath
    }
}