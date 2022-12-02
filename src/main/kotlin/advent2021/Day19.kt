package advent2021

import Coords3
import measure
import readResourceFile
import sqr
import verifyResult

fun isBalanced(s: String): String {
    val stack = mutableListOf<Char>()
    s.toCharArray().forEach { c ->
        if (closingBrackets.contains(c)) {
            if (closingBrackets[c]!! != stack.removeAt(0)) return@isBalanced "NO"
        } else {
            stack.add(0, c)
        }
    }
    return if (stack.isEmpty()) "YES" else "NO"
}

private val closingBrackets = mapOf(
    ')' to '(',
    '}' to '{',
    ']' to '['
)

/*

 */
private class Day19 {
    private val scanners: List<Scanner>

    private constructor(scanners: List<Scanner>) {
        this.scanners = scanners
        this.allOrientations = Facing.values().flatMap { f -> Rotation.values().map { Orientation(f, it) } }
    }

    fun solve(): Long {
        val unplaced = scanners.toMutableList()
        val placed = listOf(unplaced.removeFirst())
            .map { PlacedScanner(Coords3(0, 0, 0), Orientation(Facing.X, Rotation.N), it) }
            .toMutableList()
        while (unplaced.isNotEmpty()) {
            val candidate = findCandidate(placed, unplaced)
            if (candidate != null) {
                println("Placing ${candidate.scanner.id} oriented ${candidate.orientation} with offset ${candidate.offset}")
                placed.add(candidate)
                unplaced.removeIf { s -> s.id == candidate.scanner.id }
            } else throw RuntimeException("Unable to place a new scanner. Placed scanners are: $placed")
        }

        val allBeacons = placed.flatMap { it.scanner.beacons }.toSet()
        allBeacons.sortedWith(Comparator.comparingInt<Coords3?> { it.x }
            .thenComparing(Comparator.comparingInt { it.y }))
            .onEach { println(it) }
        return allBeacons.size.toLong()
    }

    private fun findCandidate(placed: List<PlacedScanner>, unplaced: List<Scanner>): PlacedScanner? {
        return placed.firstNotNullOfOrNull { placedScanner ->
            unplaced
                .filter { candidate -> beaconsWithSameDistances(placedScanner.scanner, candidate) }
                .firstNotNullOfOrNull { candidate -> findPlacement(placedScanner, candidate) }
        }
    }

    private fun beaconsWithSameDistances(
        first: Scanner,
        second: Scanner,
        atLeast: Int = 12,
    ): Boolean {
        return first.distances.entries
            .flatMap { (_, firstBeaconDistances) ->
                second.distances.entries.filter { (_, secondBeaconDistances) ->
                    firstBeaconDistances.intersect(secondBeaconDistances).size >= atLeast
                }
            }.isNotEmpty()
    }


    private fun findPlacement(
        placedScanner: PlacedScanner,
        candidate: Scanner,
        atLeast: Int = 12,
    ): PlacedScanner? {
        return allOrientations.firstNotNullOfOrNull { orientation ->
            val oriented = candidate.orient(orientation)
            val entry = placedScanner.scanner.beacons.flatMap { b1 ->
                oriented.beacons.map { b2 ->
                    b1 to b2
                }
            }
                .groupBy { (b1, b2) -> b1 - b2 }
                .filter { (_, beacons) -> beacons.size >= atLeast }
                .entries.firstOrNull()
            if (entry != null) PlacedScanner(entry.key, orientation, oriented.offset(entry.key))
            else null
        }
    }

    constructor(testInput: String) : this(testInput.split("\n\n")
        .map { it.lines().filterNot { it.isEmpty() } }
        .map { lines ->
            Scanner(
                lines[0],
                lines.subList(1, lines.size)
                    .map { it.split(",").map { it.toInt() }.let { Coords3(it) } }
                    .toSet()
            )
        }
    )


    private enum class Facing {
        X, NX, Y, NY, Z, NZ
    }

    private enum class Rotation {
        N, W, S, E
    }

    private data class Orientation(val facing: Facing, val rotation: Rotation)

    private val allOrientations: List<Orientation>

    private data class Scanner(val id: String, val beacons: Set<Coords3>) {
        fun orient(orientation: Orientation): Scanner = Scanner(id,
            beacons.map { (x, y, z) ->
                when (orientation.facing) {
                    Facing.X -> Coords3(x, y, z)
                    Facing.NX -> Coords3(-x, y, z)
                    Facing.Y -> Coords3(y, -x, z)
                    Facing.NY -> Coords3(-y, x, z)
                    Facing.Z -> Coords3(z, y, -x)
                    Facing.NZ -> Coords3(-z, y, x)
                }
            }.map { (x, y, z) ->
                when (orientation.rotation) {
                    Rotation.N -> Coords3(x, y, z)
                    Rotation.W -> Coords3(x, z, -y)
                    Rotation.S -> Coords3(x, -y, -z)
                    Rotation.E -> Coords3(x, -z, y)
                }
            }.toSet()
        )

        fun offset(offset: Coords3) = Scanner(
            id,
            beacons.map { (x, y, z) -> Coords3(x + offset.x, y + offset.y, z + offset.z) }.toSet()
        )

        val distances: Map<Coords3, Set<Long>> = beacons.associateWith { first ->
            beacons.map { second ->
                sqr(first.x - second.x) + sqr(first.y - second.y) + sqr(first.z - second.z)
            }.toSet()
        }
    }

    private data class PlacedScanner(
        val offset: Coords3,
        val orientation: Orientation,
        val scanner: Scanner,
    )
}

fun main() {
    listOf(
//        { verifyResult(14, Day19(checkInput0).solve()) },
        { verifyResult(79, Day19(checkInput).solve()) },
        { println("Result is " + Day19(testInput).solve()) }
    ).onEachIndexed { i, test -> measure(test, i) }
}

private val checkInput0: String by lazy { readResourceFile("/advent2021/day19-verification0.txt") }
private val checkInput: String by lazy { readResourceFile("/advent2021/day19-verification.txt") }
private val checkBeacons: String by lazy { readResourceFile("/advent2021/day19-verificationAnswer.txt") }

private val testInput by lazy { readResourceFile("/advent2021/day19-task1.txt") }