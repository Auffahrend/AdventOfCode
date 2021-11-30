package advent2020

import readResourceFile
import verifyResult

/*
--- Day 3: Toboggan Trajectory ---

With the toboggan login problems resolved, you set off toward the airport. While travel by toboggan might be easy, it's certainly not safe: there's very minimal steering and the area is covered in trees. You'll need to see which angles will take you near the fewest trees.

Due to the local geology, trees in this area only grow on exact integer coordinates in a grid. You make a map (your puzzle input) of the open squares (.) and trees (#) you can see. For example:

..##.......
#...#...#..
.#....#..#.
..#.#...#.#
.#...##..#.
..#.##.....
.#.#.#....#
.#........#
#.##...#...
#...##....#
.#..#...#.#

These aren't the only trees, though; due to something you read about once involving arboreal genetics and biome stability, the same pattern repeats to the right many times:

..##.........##.........##.........##.........##.........##.......  --->
#...#...#..#...#...#..#...#...#..#...#...#..#...#...#..#...#...#..
.#....#..#..#....#..#..#....#..#..#....#..#..#....#..#..#....#..#.
..#.#...#.#..#.#...#.#..#.#...#.#..#.#...#.#..#.#...#.#..#.#...#.#
.#...##..#..#...##..#..#...##..#..#...##..#..#...##..#..#...##..#.
..#.##.......#.##.......#.##.......#.##.......#.##.......#.##.....  --->
.#.#.#....#.#.#.#....#.#.#.#....#.#.#.#....#.#.#.#....#.#.#.#....#
.#........#.#........#.#........#.#........#.#........#.#........#
#.##...#...#.##...#...#.##...#...#.##...#...#.##...#...#.##...#...
#...##....##...##....##...##....##...##....##...##....##...##....#
.#..#...#.#.#..#...#.#.#..#...#.#.#..#...#.#.#..#...#.#.#..#...#.#  --->

You start on the open square (.) in the top-left corner and need to reach the bottom (below the bottom-most row on your map).

The toboggan can only follow a few specific slopes (you opted for a cheaper model that prefers rational numbers); start by counting all the trees you would encounter for the slope right 3, down 1:

From your starting position at the top-left, check the position that is right 3 and down 1. Then, check the position that is right 3 and down 1 from there, and so on until you go past the bottom of the map.

The locations you'd check in the above example are marked here with O where there was an open square and X where there was a tree:

..##.........##.........##.........##.........##.........##.......  --->
#..O#...#..#...#...#..#...#...#..#...#...#..#...#...#..#...#...#..
.#....X..#..#....#..#..#....#..#..#....#..#..#....#..#..#....#..#.
..#.#...#O#..#.#...#.#..#.#...#.#..#.#...#.#..#.#...#.#..#.#...#.#
.#...##..#..X...##..#..#...##..#..#...##..#..#...##..#..#...##..#.
..#.##.......#.X#.......#.##.......#.##.......#.##.......#.##.....  --->
.#.#.#....#.#.#.#.O..#.#.#.#....#.#.#.#....#.#.#.#....#.#.#.#....#
.#........#.#........X.#........#.#........#.#........#.#........#
#.##...#...#.##...#...#.X#...#...#.##...#...#.##...#...#.##...#...
#...##....##...##....##...#X....##...##....##...##....##...##....#
.#..#...#.#.#..#...#.#.#..#...X.#.#..#...#.#.#..#...#.#.#..#...#.#  --->

In this example, traversing the map using this slope would cause you to encounter 7 trees.

Starting at the top-left corner of your map and following a slope of right 3 and down 1, how many trees would you encounter?

 */
class Day03(input: String) {
    private val map: List<List<Obstacle>>

    init {
        map = input.lines()
            .filter { it.isNotBlank() }
            .map { line ->
                line.toCharArray()
                    .map { char -> Obstacle.from(char) }
            }
    }

    /**
     * @param direction - next step offset on X and Y axis
     */
    fun solve(direction: Pair<Int, Int>, obstacleToCount: Obstacle = Obstacle.Tree): Int {
        val mapWidth = map.first().size
        var currentPosition = 0 to 0
        var counter = 0
        while (currentPosition.second < map.size) {
            if (map[currentPosition.second][currentPosition.first % mapWidth] == obstacleToCount) counter++
            currentPosition = currentPosition.first + direction.first to currentPosition.second + direction.second
        }
        return counter
    }

}

enum class Obstacle(val char: Char) {
    None('.'), Tree('#');

    companion object {
        fun from(char: Char): Obstacle = values().first { it.char == char }
    }
}

fun main() {
    verifyResult(7, Day03(checkInput).solve(3 to 1))

    Day03(testInput)
        .let { slope ->
            listOf(1 to 1, 3 to 1, 5 to 1, 7 to 1, 1 to 2)
                .map { slope.solve(it) }
                .reduce { i, j -> i * j }
        }
        .let { println(it) }
}

private const val checkInput: String = "" +
        "..##.......\n" +
        "#...#...#..\n" +
        ".#....#..#.\n" +
        "..#.#...#.#\n" +
        ".#...##..#.\n" +
        "..#.##.....\n" +
        ".#.#.#....#\n" +
        ".#........#\n" +
        "#.##...#...\n" +
        "#...##....#\n" +
        ".#..#...#.#"

private val testInput by lazy { readResourceFile("/advent2020/day03-task1.txt") }