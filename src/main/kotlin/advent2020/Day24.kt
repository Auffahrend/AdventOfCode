package advent2020

import measure
import readResourceFile
import verifyResult
import kotlin.math.round

/*
--- Day 24: Lobby Layout ---

Your raft makes it to the tropical island; it turns out that the small crab was an excellent navigator. You make your way to the resort.

As you enter the lobby, you discover a small problem: the floor is being renovated. You can't even reach the check-in desk until they've finished installing the new tile floor.

The tiles are all hexagonal; they need to be arranged in a hex grid with a very specific color pattern. Not in the mood to wait, you offer to help figure out the pattern.

The tiles are all white on one side and black on the other. They start with the white side facing up. The lobby is large enough to fit whatever pattern might need to appear there.

A member of the renovation crew gives you a list of the tiles that need to be flipped over (your puzzle input). Each line in the list identifies a single tile that needs to be flipped by giving a series of steps starting from a reference tile in the very center of the room. (Every line starts from the same reference tile.)

Because the tiles are hexagonal, every tile has six neighbors: east, southeast, southwest, west, northwest, and northeast. These directions are given in your list, respectively, as e, se, sw, w, nw, and ne. A tile is identified by a series of these directions with no delimiters; for example, esenee identifies the tile you land on if you start at the reference tile and then move one tile east, one tile southeast, one tile northeast, and one tile east.

Each time a tile is identified, it flips from white to black or from black to white. Tiles might be flipped more than once. For example, a line like esew flips a tile immediately adjacent to the reference tile, and a line like nwwswee flips the reference tile itself.

Here is a larger example:

sesenwnenenewseeswwswswwnenewsewsw
neeenesenwnwwswnenewnwwsewnenwseswesw
seswneswswsenwwnwse
nwnwneseeswswnenewneswwnewseswneseene
swweswneswnenwsewnwneneseenw
eesenwseswswnenwswnwnwsewwnwsene
sewnenenenesenwsewnenwwwse
wenwwweseeeweswwwnwwe
wsweesenenewnwwnwsenewsenwwsesesenwne
neeswseenwwswnwswswnw
nenwswwsewswnenenewsenwsenwnesesenew
enewnwewneswsewnwswenweswnenwsenwsw
sweneswneswneneenwnewenewwneswswnese
swwesenesewenwneswnwwneseswwne
enesenwswwswneneswsenwnewswseenwsese
wnwnesenesenenwwnenwsewesewsesesew
nenewswnwewswnenesenwnesewesw
eneswnwswnwsenenwnwnwwseeswneewsenese
neswnwewnwnwseenwseesewsenwsweewe
wseweeenwnesenwwwswnew

In the above example, 10 tiles are flipped once (to black), and 5 more are flipped twice (to black, then back to white). After all of these instructions have been followed, a total of 10 tiles are black.

Go through the renovation crew's list and determine which tiles they need to flip. After all of the instructions have been followed, how many tiles are left with the black side up?

--- Part Two ---

The tile floor in the lobby is meant to be a living art exhibit. Every day, the tiles are all flipped according to the following rules:

    Any black tile with zero or more than 2 black tiles immediately adjacent to it is flipped to white.
    Any white tile with exactly 2 black tiles immediately adjacent to it is flipped to black.

Here, tiles immediately adjacent means the six tiles directly touching the tile in question.

The rules are applied simultaneously to every tile; put another way, it is first determined which tiles need to be flipped, then they are all flipped at the same time.

In the above example, the number of black tiles that are facing up after the given number of days has passed is as follows:

Day 1: 15
Day 2: 12
Day 3: 25
Day 4: 14
Day 5: 23
Day 6: 28
Day 7: 41
Day 8: 37
Day 9: 49
Day 10: 37

Day 20: 132
Day 30: 259
Day 40: 406
Day 50: 566
Day 60: 788
Day 70: 1106
Day 80: 1373
Day 90: 1844
Day 100: 2208

After executing this process a total of 100 times, there would be 2208 black tiles facing up.

How many tiles will be black after 100 days?

 */
private class Day24(
) {
    private lateinit var tilePaths: List<List<Direction>>

    fun solve(n: Int = 0): Int {
        var state = getFlippedTiles()
        repeat(n) { state = round(state) }
        return state.size
    }

    private fun round(blacks: Set<Point>): Set<Point> =
        blacks.flatMap { it.neighbours() + it }.toSet() // all points to consider
            .filter { current ->
                if (blacks.contains(current))
                    current.neighbours().filter { blacks.contains(it) }.size in 1..2
                else current.neighbours().filter { blacks.contains(it) }.size == 2
            }.toSet()


    private fun getFlippedTiles(): Set<Point> {
        val flipped = mutableSetOf<Point>()
        tilePaths
            .map { it.fold(Point(0, 0), Point::move) }
            .onEach { if (flipped.contains(it)) flipped.remove(it) else flipped.add(it) }
        return flipped.toSet()//.onEach { it.isBlack = true }
    }

    constructor(testInput: String) : this() {
        tilePaths = testInput.lines().filterNot { it.isBlank() }
            .map { it.uppercase().toCharArray() }
            .map { chars ->
                var i = 0
                val acc = mutableListOf<String>()
                while (i < chars.size) {
                    var d = chars[i].toString()
                    if (d == "N" || d == "S") {
                        d = chars.concatToString(i, i + 2)
                        i++
                    }
                    acc.add(d)
                    i++
                }
                return@map acc.map { Direction.valueOf(it) }
            }
    }

    data class Point(val x: Int, val xy: Int) {
        //        var isBlack: Boolean = true
        fun move(direction: Direction): Point = Point(x + direction.dx, xy + direction.dxy)
        fun neighbours() = Direction.values().map { move(it) }.toSet()
    }

    private fun Set<Point>.neighbors(p: Point) {

    }

    enum class Direction(val dx: Int, val dxy: Int) {
        E(1, 0), W(-1, 0),
        NE(0, 1), NW(-1, 1),
        SE(1, -1), SW(0, -1)
    }
}

fun main() {
    listOf(
        { verifyResult(10, Day24(checkInput).solve()) },
        { verifyResult(15, Day24(checkInput).solve(1)) },
        { verifyResult(37, Day24(checkInput).solve(10)) },
        { verifyResult(2208, Day24(checkInput).solve(100)) },
        { println("Result is " + Day24(testInput).solve(100)) }
    ).onEachIndexed { i, test -> measure(test, i) }
}

private const val checkInput: String = "" +
        "sesenwnenenewseeswwswswwnenewsewsw\n" +
        "neeenesenwnwwswnenewnwwsewnenwseswesw\n" +
        "seswneswswsenwwnwse\n" +
        "nwnwneseeswswnenewneswwnewseswneseene\n" +
        "swweswneswnenwsewnwneneseenw\n" +
        "eesenwseswswnenwswnwnwsewwnwsene\n" +
        "sewnenenenesenwsewnenwwwse\n" +
        "wenwwweseeeweswwwnwwe\n" +
        "wsweesenenewnwwnwsenewsenwwsesesenwne\n" +
        "neeswseenwwswnwswswnw\n" +
        "nenwswwsewswnenenewsenwsenwnesesenew\n" +
        "enewnwewneswsewnwswenweswnenwsenwsw\n" +
        "sweneswneswneneenwnewenewwneswswnese\n" +
        "swwesenesewenwneswnwwneseswwne\n" +
        "enesenwswwswneneswsenwnewswseenwsese\n" +
        "wnwnesenesenenwwnenwsewesewsesesew\n" +
        "nenewswnwewswnenesenwnesewesw\n" +
        "eneswnwswnwsenenwnwnwwseeswneewsenese\n" +
        "neswnwewnwnwseenwseesewsenwsweewe\n" +
        "wseweeenwnesenwwwswnew"

private val testInput by lazy { readResourceFile("/advent2020/day24-task1.txt") }