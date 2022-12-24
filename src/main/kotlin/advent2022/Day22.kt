package advent2022

import Coords
import contains
import get
import Matrix

/*

 */
class Day22(testInput: String) {
    private val grid: Matrix<Char>
    private val commands: String

    init {
        testInput.split("\n\n").let {(map, comm) ->
            grid = map.lines()
                .map { it.toCharArray().toList() }
            commands = comm.trim()
        }
    }

    private abstract class TopologyMap(val grid: Matrix<Char>) {
        var currentPosition: Coords = grid.indexOfFirst { row -> row.contains(EMPTY_TILE) }
            .let { y -> grid[y].indexOfFirst { it == EMPTY_TILE } to y }
        var currentDirection = Direction.East
        var stepI = 0
        var path = mutableMapOf(currentPosition to stepI)

        fun execute(commands: String) {
            val allSteps = commands.split("[RL]".toRegex()).map { it.toInt() }
            val allTurns = commands.split("\\d+".toRegex()).filter { it.isNotEmpty() }.map { it[0] }

            allSteps.zip(allTurns).forEach { (steps, direction) ->
                move(steps)
                rotate(direction)
            }
            move(allSteps.last())
        }

        fun rotate(dir: Char) {
            currentDirection = currentDirection.rotate(dir)
        }

        abstract fun wrapAround(nextPosition: Coords): Pair<Coords, Direction>

        fun move(steps: Int) {
            repeat(steps) {
                var nextPosition = currentDirection.move(currentPosition)
                val previousDirection = currentDirection
                if (!grid.contains(nextPosition) || grid[nextPosition] == NO_TILE) {
                    wrapAround(nextPosition).let { (pos, dir) ->
                        nextPosition = pos
                        currentDirection = dir
                    }
                }
                if (grid[nextPosition] == EMPTY_TILE) currentPosition = nextPosition.also {
                    path[currentPosition] = ++stepI
                } else {
                    // can't proceed into other cube face
                    currentDirection = previousDirection
                    return
                }
            }
        }

        fun password() = currentPosition.let { (x, y) -> (1+y) * 1000 + (1+x) * 4 + currentDirection.ordinal}
    }

    private class PlainTopologyMap(grid: Matrix<Char>) : TopologyMap(grid) {
        override fun wrapAround(nextPosition: Coords): Pair<Coords, Direction> {
            val (x, y) = nextPosition
            return when (currentDirection) {
                Direction.North -> x to grid.indexOfLast { row -> row.size > x && row[x] != NO_TILE }
                Direction.South -> x to grid.indexOfFirst { row -> row.size > x && row[x] != NO_TILE }
                Direction.West -> grid[y].indexOfLast { it != NO_TILE } to y
                Direction.East -> grid[y].indexOfFirst { it != NO_TILE } to y
            } to currentDirection
        }
    }

    private class CubeTopologyMap(grid: Matrix<Char>) : TopologyMap(grid) {
        private enum class Face {Front, Right, Back, Left, Top, Bottom }

        private val faces: Map<Face, Set<Coords>> = Face.values().associateWith {
            when(it) {
                Face.Front -> (50 until 100).flatMap { x -> (0 until 50).map { y -> x to y } }.toSet()
                Face.Right -> (100 until 150).flatMap { x -> (0 until 50).map { y -> x to y } }.toSet()
                Face.Bottom -> (50 until 100).flatMap { x -> (50 until 100).map { y -> x to y } }.toSet()
                Face.Left -> (0 until 50).flatMap { x -> (100 until 150).map { y -> x to y } }.toSet()
                Face.Back -> (50 until 100).flatMap { x -> (100 until 150).map { y -> x to y } }.toSet()
                Face.Top -> (0 until 50).flatMap { x -> (150 until 200).map { y -> x to y } }.toSet()
            }
        }
        override fun wrapAround(nextPosition: Coords): Pair<Coords, Direction> {
            when (val currentFace = faces.entries.first { (_, coords) -> coords.contains(currentPosition) }.key) {
                Face.Front -> return when (currentDirection) {
                    Direction.West ->
                        // wrap around to Left: x = 0, y = -y
                        0 to 149 - nextPosition.second to Direction.East
                    Direction.North ->
                        // wrap around to Top: x = 0, y = +x
                        0 to 150 + (nextPosition.first - 50) to Direction.East
                    else -> throw IllegalStateException("No wrap around from $currentFace to $currentFace direction")
                }
                Face.Right -> return when (currentDirection) {
                    Direction.North ->
                        // wrap around to Top: x=+x, y = 199
                        nextPosition.first - 100 to 199 to Direction.North
                    Direction.East ->
                        // wrap around to Back: x = 99, y = -y
                        99 to 149 - nextPosition.second to Direction.West
                    Direction.South ->
                        // wrap around to Bottom: x = 99, y = +x
                        99 to 50 + nextPosition.first-100 to Direction.West
                    else -> throw IllegalStateException("No wrap around from $currentFace to $currentFace direction")
                }
                Face.Back -> return when (currentDirection) {
                    Direction.East ->
                        // wrap around to Right: x = 149, y = -y
                        149 to 49 - (nextPosition.second - 100) to Direction.West
                    Direction.South ->
                        // wrap around to Top: x = 49, y = +x
                        49 to 150 + (nextPosition.first - 50) to Direction.West
                    else -> throw IllegalStateException("No wrap around from $currentFace to $currentFace direction")
                }
                Face.Left -> return when (currentDirection) {
                    Direction.North ->
                        // wrap around to Bottom: x = 50, y = +x
                        50 to 50 + nextPosition.first to Direction.East
                    Direction.West ->
                        // wrap around to Front: x = 50, y = -y
                        50 to 49 - (nextPosition.second - 100) to Direction.East
                    else -> throw IllegalStateException("No wrap around from $currentFace to $currentFace direction")
                }
                Face.Top -> return when (currentDirection) {
                    Direction.East ->
                        // wrap around to Back: x=+y, y = 149
                        nextPosition.second - 150 + 50 to 149 to Direction.North
                    Direction.South ->
                        // wrap around to Right: x = +x, y = 0
                        nextPosition.first + 100 to 0 to Direction.South
                    Direction.West ->
                        // wrap around to Front: x = +y, y = 0
                        nextPosition.second-150+50 to 0 to Direction.South
                    else -> throw IllegalStateException("No wrap around from $currentFace to $currentFace direction")
                }
                Face.Bottom -> return when (currentDirection) {
                    Direction.East ->
                        // wrap around to Right: x=+y, y = 49
                        nextPosition.second - 50 + 100 to 49 to Direction.North
                    Direction.West ->
                        // wrap around to Left: x = +y, y = 100
                        nextPosition.second-50 to 100 to Direction.South
                    else -> throw IllegalStateException("No wrap around from $currentFace to $currentFace direction")
                }
            }
        }
    }

    fun solve(): Long {
        val map = PlainTopologyMap(grid)
        map.execute(commands)
        return map.password().toLong()
    }

    fun solve2(): Long {
        val map = CubeTopologyMap(grid)
        map.execute(commands)
        return map.password().toLong()
    }
}

private const val NO_TILE = ' '
private const val EMPTY_TILE = '.'
private const val WALL_TILE = '#'
private enum class Direction(val move: (Coords) -> Coords) {
    East({ (x, y) -> x + 1 to y }),
    South({ (x, y) -> x to y + 1 }),
    West({ (x, y) -> x - 1 to y }),
    North({ (x, y) -> x to y - 1 })
    ;

    fun rotate(dir: Char): Direction = when (this) {
        North -> if (dir == 'R') East else West
        South -> if (dir == 'R') West else East
        West -> if (dir == 'R') North else South
        East ->  if (dir == 'R') South else North
    }
}