package advent2022

import Coords
import neighbours8

/*

 */
class Day23(testInput: String) {
    private enum class Direction(val neighbors: (Coords) -> List<Coords>, val move: (Coords) -> Coords) {
        North({ (x, y) -> listOf(x - 1 to y - 1, x to y - 1, x + 1 to y - 1) }, { (x, y) -> x to y - 1 }),
        South({ (x, y) -> listOf(x - 1 to y + 1, x to y + 1, x + 1 to y + 1) }, { (x, y) -> x to y + 1 }),
        West({ (x, y) -> listOf(x - 1 to y - 1, x - 1 to y, x - 1 to y + 1) }, { (x, y) -> x - 1 to y }),
        East({ (x, y) -> listOf(x + 1 to y - 1, x + 1 to y, x + 1 to y + 1) }, { (x, y) -> x + 1 to y });
    }

    private val initialPositions: List<Coords>
    private val directionOrder = Direction.values().toMutableList()
    private fun rotateDirections() = directionOrder.removeFirst().also { directionOrder.add(it) }

    init {
        initialPositions = testInput.lines().filter { it.isNotEmpty() }
            .flatMapIndexed { y, it -> it.mapIndexedNotNull { x, c -> if (c == '#') x to y else null } }
    }

    private fun round(positions: Set<Coords>): Set<Coords> {
        val elves = positions.toSet()
        val proposedPositions = mutableMapOf<Coords, Coords>()
        elves.forEach { elf ->
            if (elf.neighbours8().any { elves.contains(it) }) {
                directionOrder
                    .firstOrNull { d -> d.neighbors(elf).none { elves.contains(it) } }
                    .let { proposedPositions[elf] = it?.move?.invoke(elf) ?: elf }
            } else proposedPositions[elf] = elf
        }

        val conflicts = proposedPositions.entries.groupBy({ it.value }, { it.key }).filterValues { it.size > 1 }
        conflicts.forEach { (_, positions) -> positions.forEach { proposedPositions[it] = it } }

        rotateDirections()
        return proposedPositions.values.toSet()
    }

    fun solve(rounds: Int = 10): Long {
        var elves = initialPositions.toSet()
        repeat(rounds) { elves = round(elves).also { it.debug()} }
        return emptySpaces(elves)
    }

    fun solve2(): Int {
        var i = 0
        var elves = initialPositions.toSet()
        do {
            val prev = elves
            elves = round(elves)
            i++
        } while (prev != elves)
        return i
    }

    private fun emptySpaces(elves: Set<Coords>): Long {
        val minX = elves.minOf { it.first }
        val maxX = elves.maxOf { it.first }
        val minY = elves.minOf { it.second }
        val maxY = elves.maxOf { it.second }

        return (maxX.toLong() - minX + 1) * (maxY - minY + 1) - elves.size
    }

    var i = 0
    private fun Set<Coords>.debug() {
        if (i++ > -1) return
        println("After round $i: ")
        val minX = minOf { it.first }
        val maxX = maxOf { it.first }
        val minY = minOf { it.second }
        val maxY = maxOf { it.second }

        (minY..maxY).forEach { y ->
            (minX..maxX).map { x -> if (contains(x to y)) '#' else '.' }
                .joinToString(separator = "")
                .also { println(it) }
        }
    }

}