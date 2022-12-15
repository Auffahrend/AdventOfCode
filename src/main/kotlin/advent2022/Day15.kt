package advent2022

import Coords
import advent2021.intersection
import minus
import kotlin.math.abs
import kotlin.math.max

/*

 */
private fun distance(p1: Coords, p2: Coords): Int = (p1 - p2).let { abs(it.first) + abs(it.second) }
class Day15(testInput: String) {
    private data class SensorReading(val sensor: Coords, val beacon: Coords) {
        val distance = distance(sensor, beacon)

        fun coverageAt(y: Int): IntRange {
            val overlapHeight = distance - abs(y - sensor.second)
            return if (overlapHeight < 0) IntRange.EMPTY
            else sensor.first - overlapHeight..sensor.first + overlapHeight
        }
    }

    private val sensorReadings: List<SensorReading>

    init {
        val matcher = "Sensor at x=(-?\\d+), y=(-?\\d+): closest beacon is at x=(-?\\d+), y=(-?\\d+)".toRegex()
        sensorReadings = testInput.lines().filter { it.isNotEmpty() }
            .map {
                matcher.matchEntire(it)!!.destructured
                    .let { (sx, sy, bx, by) ->
                        SensorReading(sx.toInt() to sy.toInt(), bx.toInt() to by.toInt())
                    }
            }
    }

    fun solve1(y: Int): Int {
        val allCoveredRanges = sensorReadings.map { it.coverageAt(y) }.filterNot { it.isEmpty() }
            .sortedBy { it.first }
            .toMutableList()
            .unionIntersections()

        val allItemsOnRow = sensorReadings.flatMap { setOf(it.sensor, it.beacon) }.toSet().filter { it.second == y }
        return allCoveredRanges.sumOf(IntRange::size) - allItemsOnRow.size
    }

    fun solve2(range: IntRange): Long {
        range.forEach { y ->
            val allCoveredRanges = sensorReadings.map { it.coverageAt(y) }.filterNot { it.isEmpty() }
                .sortedBy { it.first }
                .toMutableList()
                .unionIntersections()
                .mapNotNull { it.intersection(range) }
            if (range.size() - allCoveredRanges.sumOf(IntRange::size) == 1) {
                assert(allCoveredRanges.size == 2) { "A point in the middle is expected (although it's possible it's on an edge" }
                return (allCoveredRanges[0].last + 1).let { x -> x.toLong() * 4000000 + y }
            }

        }
        throw IllegalArgumentException("A single possible point not found")
    }
}

private fun IntRange.size(): Int = if (isEmpty()) 0 else last - first + 1
private fun MutableList<IntRange>.unionIntersections(): MutableList<IntRange> {
    var i = 0
    while (i < size - 1) {
        if (this[i].contains(this[i + 1].first)) {
            val union = this[i].first..max(this[i].last, this[i + 1].last)
            this[i] = union
            removeAt(i + 1)
        } else i++
    }
    return this
}
