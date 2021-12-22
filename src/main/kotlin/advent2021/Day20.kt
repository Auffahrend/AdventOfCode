package advent2021

import measure
import neighbours9
import readResourceFile

/*

 */
private class Day20(
    private val algorithm: String,
    private val initialImage: List<List<Char>>
) {
    fun solve(enhanceTimes: Int): Int {
        var litPixels = initialImage.flatMapIndexed { y, row ->
            row.flatMapIndexed { x, c -> if (c == '#') listOf(x to y) else emptyList() }
        }.toSet()
        repeat(enhanceTimes) { step ->
            litPixels = litPixels.flatMap { litPixels.neighbours9(it) }.toSet()
                .filter { p ->
                    var address = 0
                    litPixels.neighbours9(p).onEach {
                        address = address.shl(1) + if (litPixels.isLit(it, step)) 1 else 0
                    }
                    algorithm[address] == '#'
                }.toSet()
        }
        return litPixels.size
    }

    private fun Set<Coords>.isLit(point: Coords, step: Int): Boolean =
        contains(point) || (algorithm[0] == '#' && step % 2 == 1 && isBorderPixel(point, step))

    private fun isBorderPixel(point: Coords, step: Int): Boolean {
        return point.first <= -step || point.first >= initialImage.first().size - 1 + step ||
                point.second <= -step || point.second >= initialImage.size - 1 + step
    }


    constructor(testInput: String) : this(testInput.lines().first(),
        testInput.lines().drop(2).filterNot { it.isBlank() }
            .map { it.toCharArray().toList() }
    )

}

fun main() {
    listOf(
//        { verifyResult(24, Day20(checkInput).solve(1)) },
//        { verifyResult(35, Day20(checkInput).solve(2)) },
        { println("Result is " + Day20(testInput).solve(2)) }
    ).onEachIndexed { i, test -> measure(test, i) }
}

private const val checkInput: String = "" +
        "..#.#..#####.#.#.#.###.##.....###.##.#..###.####..#####..#....#..#..##..##" +
        "#..######.###...####..#..#####..##..#.#####...##.#.#..#.##..#.#......#.###" +
        ".######.###.####...#.##.##..#..#..#####.....#.#....###..#.##......#.....#." +
        ".#..#..##..#...##.######.####.####.#.#...#.......#..#.#.#...####.##.#....." +
        ".#..#...##.#.##..#...##.#.##..###.#......#.#.......#.#.#.####.###.##...#.." +
        "...####.#..#..#.##.#....##..#.####....##...##..#...#......#.#.......#....." +
        "..##..####..#...#.#.#...##..#.#..###..#####........#..####......#..#\n" +
        "\n" +
        "#..#.\n" +
        "#....\n" +
        "##..#\n" +
        "..#..\n" +
        "..###\n"

private val testInput by lazy { readResourceFile("/advent2021/day20-task1.txt") }