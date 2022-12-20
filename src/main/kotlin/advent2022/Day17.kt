package advent2022

import Coords
import contains
import plus
import get
import set
import rotate90

/*

 */
class Day17(testInput: String) {
    private val airStreams: String = testInput.trim()
    private fun streamAt(step: Int): Char = airStreams[step % airStreams.length]
    private val cave = Cave()

    private class Cave {

        // cave is represented rotated 90º clockwise - floor is on left and rocks are falling from right (mutableLists` top indexes to left (0 indexes)
        val contents: List<MutableList<Rock?>> = buildList { repeat(7) { add(mutableListOf()) } }
        val allRocks = mutableListOf<Rock>()
        var highestPoint = 0
        private val moveLeft = (0 to -1)
        private val moveRight = (0 to 1)
        private val moveDown = (-1 to 0)

        fun increase(maxX: Int) {
            contents.forEach { row -> while (row.size <= maxX) row.add(null) }
        }

        fun tryMove(rock: Rock, streamDirection: Char) {
            val offset = if (streamDirection == '<') moveLeft else moveRight
            val newPoints = rock.shape.allPointsOffsets.map { it + rock.basePosition + offset }
            if (newPoints.all { contents.contains(it) && contents[it] == null }) rock.basePosition += offset
        }

        fun tryFall(rock: Rock) {
            val newPoints = rock.shape.allPointsOffsets.map { it + rock.basePosition + moveDown }
            if (newPoints.all { contents.contains(it) && contents[it] == null }) rock.basePosition += moveDown
            else {
                rock.felt = true
                allRocks.add(rock)
                rock.shape.allPointsOffsets.map { it + rock.basePosition }
                    .forEach { contents[it] = rock }
                highestPoint = Math.max(highestPoint, rock.basePosition.first + 1)
            }
        }

        fun spawn(): Rock =
            Shape.values()[allRocks.size % Shape.values().size]
                .let { nextShape -> Rock((highestPoint + 3 - nextShape.lowestLevel to 2), nextShape) }

        fun debug() {
            if (allRocks.size <= 0) {
                println("After ${allRocks.size} rocks: ")
                println("=====================================")
                (1..contents[0].size).forEach { y ->
                    contents.map { r -> (contents[0].size - y).let { r[it] } }
                        .map { if (it == null) '.' else '#' }
                        .joinToString(separator = "", prefix = "|", postfix = "|")
                        .also { println(it) }
                }
                println("+-------+")
            }
        }
    }

    private data class Rock(var basePosition: Coords, val shape: Shape, var felt: Boolean = false) {
    }

    private enum class Shape(text: String) {

        First("####"),
        Second(".#.\n###\n.#."),
        Third("..#\n..#\n###"),
        Fourth("#\n#\n#\n#"),
        Fifth("##\n##"),
        ;

        val allPointsOffsets = text.lines().flatMapIndexed { y, r ->
            r.mapIndexedNotNull { x, c -> if (c == '#') (x to y).rotate90() else null }
        }
        val lowestLevel = allPointsOffsets.minOf { it.first }
    }

    private data class FallSignature(val shape: Shape, val offset: Int, val stream: Int)
    private data class Repetition(
        val heightBefore: Int,
        val rocksBefore: Int,
        val totalSize: Int,
        val heightIncreases: List<Int>,
    ) {
        fun calculateTotalHeight(rocks: Long): Long {
            val repeatedRocks = rocks - rocksBefore
            val fullRepetitions = repeatedRocks / heightIncreases.size
            val rocksAfter = repeatedRocks - fullRepetitions * heightIncreases.size
            return heightBefore + fullRepetitions * totalSize +
                    if (rocksAfter > 0) heightIncreases[rocksAfter.toInt() - 1] else 0
        }
    }

    fun solveEmulate(rocks: Int): Long {
        var streamI = 0
        repeat(rocks) {
            val rock = cave.spawn()
            cave.increase(rock.basePosition.first)
            while (!rock.felt) {
                cave.tryMove(rock, streamAt(streamI++))
                cave.tryFall(rock)
            }
        }
        return cave.highestPoint.toLong()
    }

    fun solve(rocks: Long): Long {
        var streamI = 0
        var repetition: Repetition? = null
        val fallSignatureHistory: MutableList<Pair<FallSignature, Int>> = mutableListOf()

        while (repetition == null) {
            val rock = cave.spawn()
            cave.increase(rock.basePosition.first)
            while (!rock.felt) {
                cave.tryMove(rock, streamAt(streamI++))
                cave.tryFall(rock)
            }
            val signature = FallSignature(rock.shape, rock.basePosition.second, streamI % airStreams.length)
            fallSignatureHistory.add(signature to cave.highestPoint)
            val index = fallSignatureHistory.indexOfFirst { it.first == signature }
            if (index >= 0 && index < fallSignatureHistory.lastIndex && repetition == null) {
                val heightBefore = if (index > 0) fallSignatureHistory[index - 1].second else 0
                val heightIncreases = fallSignatureHistory.subList(index, fallSignatureHistory.lastIndex)
                    .map { it.second - heightBefore }
                val totalSize = cave.highestPoint - fallSignatureHistory[index].second

                repetition = Repetition(heightBefore, index, totalSize, heightIncreases)
            }
//            cave.debug()
        }

        println("Repetition found after ${cave.allRocks.size} rocks: $repetition")


        return repetition.calculateTotalHeight(rocks)
    }
}