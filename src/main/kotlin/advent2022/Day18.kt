package advent2022

import Coords3

/*

 */
class Day18(testInput: String) {

    private val cubes = mutableSetOf<Coords3>()
    private val outsidesCache = mutableMapOf<Coords3, Boolean>()
    private var enclosureMin = Coords3(Int.MAX_VALUE, Int.MAX_VALUE, Int.MAX_VALUE)
    private var enclosureMax = Coords3(Int.MIN_VALUE, Int.MIN_VALUE, Int.MIN_VALUE)

    init {
        testInput.lines().filterNot { it.isEmpty() }
            .map { it.split(",") }
            .map { (x, y, z) -> Coords3(x.toInt(), y.toInt(), z.toInt()) }
            .forEach {
                cubes.add(it)
                outsidesCache[it] = false
                addToEnclosure(it)
            }
    }

    private fun addToEnclosure(point: Coords3) {
        if (enclosureMin.x > point.x) enclosureMin = Coords3(point.x, enclosureMin.y, enclosureMin.z)
        if (enclosureMin.y > point.y) enclosureMin = Coords3(enclosureMin.x, point.y, enclosureMin.z)
        if (enclosureMin.z > point.z) enclosureMin = Coords3(enclosureMin.x, enclosureMin.y, point.z)

        if (enclosureMax.x < point.x) enclosureMax = Coords3(point.x, enclosureMax.y, enclosureMax.z)
        if (enclosureMax.y < point.y) enclosureMax = Coords3(enclosureMax.x, point.y, enclosureMax.z)
        if (enclosureMax.z < point.z) enclosureMax = Coords3(enclosureMax.x, enclosureMax.y, point.z)
    }

    fun solve1(): Long {
        return calculateSides().toLong()
    }

    fun solve2(): Long {
        return calculateExteriorSides().toLong()
    }

    private fun calculateSides() =
        cubes.flatMap { c -> c.neighbours6().filter { !cubes.contains(it) } }
            .size

    private fun calculateExteriorSides() =
        cubes.flatMap { c ->
            c.neighbours6()
                .filter { !cubes.contains(it) }
                .filter { connectsToWater(it) }
        }
            .size


    private fun connectsToWater(initial: Coords3): Boolean {
        if (outsidesCache.contains(initial)) return outsidesCache[initial]!!

        val consideredPoints = mutableSetOf<Coords3>() // all points are connected to initial and between themselves
        val pointsToConsider = mutableSetOf(initial)
        while (pointsToConsider.isNotEmpty()) {
            val point = pointsToConsider.iterator().next(); pointsToConsider.remove(point)
            if (!consideredPoints.contains(point)) {
                if (isOutside(point)) {
                    outsidesCache[point] = true
                    consideredPoints.forEach { outsidesCache[it] = true }
                    pointsToConsider.forEach { outsidesCache[it] = true }
                    return true
                } else {
                    consideredPoints.add(point)
                    pointsToConsider.addAll(point.neighbours6().filter { !cubes.contains(it) && !consideredPoints.contains(it)})
                }
            }
        }

        // it's a point in an internal void
        consideredPoints.forEach { outsidesCache[it] = false }
        return false
    }

    private fun isOutside(point: Coords3): Boolean =
        enclosureMin.x > point.x || enclosureMin.y > point.y || enclosureMin.z > point.z ||
        enclosureMax.x < point.x || enclosureMax.y < point.y || enclosureMax.z < point.z
}