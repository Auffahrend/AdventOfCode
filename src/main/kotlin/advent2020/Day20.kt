package advent2020

import measure
import readResourceFile
import verifyResult
import java.lang.StringBuilder

/*
--- Day 20: Jurassic Jigsaw ---

The high-speed train leaves the forest and quickly carries you south. You can even see a desert in the distance! Since you have some spare time, you might as well see if there was anything interesting in the image the Mythical Information Bureau satellite captured.

After decoding the satellite messages, you discover that the data actually contains many small images created by the satellite's camera array. The camera array consists of many cameras; rather than produce a single square image, they produce many smaller square image tiles that need to be reassembled back into a single image.

Each camera in the camera array returns a single monochrome image tile with a random unique ID number. The tiles (your puzzle input) arrived in a random order.

Worse yet, the camera array appears to be malfunctioning: each image tile has been rotated and flipped to a random orientation. Your first task is to reassemble the original image by orienting the tiles so they fit together.

To show how the tiles should be reassembled, each tile's image data includes a border that should line up exactly with its adjacent tiles. All tiles have this border, and the border lines up exactly when the tiles are both oriented correctly. Tiles at the edge of the image also have this border, but the outermost edges won't line up with any other tiles.

For example, suppose you have the following nine tiles:

Tile 2311:
..##.#..#.
##..#.....
#...##..#.
####.#...#
##.##.###.
##...#.###
.#.#.#..##
..#....#..
###...#.#.
..###..###

Tile 1951:
#.##...##.
#.####...#
.....#..##
#...######
.##.#....#
.###.#####
###.##.##.
.###....#.
..#.#..#.#
#...##.#..

Tile 1171:
####...##.
#..##.#..#
##.#..#.#.
.###.####.
..###.####
.##....##.
.#...####.
#.##.####.
####..#...
.....##...

Tile 1427:
###.##.#..
.#..#.##..
.#.##.#..#
#.#.#.##.#
....#...##
...##..##.
...#.#####
.#.####.#.
..#..###.#
..##.#..#.

Tile 1489:
##.#.#....
..##...#..
.##..##...
..#...#...
#####...#.
#..#.#.#.#
...#.#.#..
##.#...##.
..##.##.##
###.##.#..

Tile 2473:
#....####.
#..#.##...
#.##..#...
######.#.#
.#...#.#.#
.#########
.###.#..#.
########.#
##...##.#.
..###.#.#.

Tile 2971:
..#.#....#
#...###...
#.#.###...
##.##..#..
.#####..##
.#..####.#
#..#.#..#.
..####.###
..#.#.###.
...#.#.#.#

Tile 2729:
...#.#.#.#
####.#....
..#.#.....
....#..#.#
.##..##.#.
.#.####...
####.#.#..
##.####...
##..#.##..
#.##...##.

Tile 3079:
#.#.#####.
.#..######
..#.......
######....
####.#..#.
.#...#.##.
#.#####.##
..#.###...
..#.......
..#.###...

By rotating, flipping, and rearranging them, you can find a square arrangement that causes all adjacent borders to line up:

#...##.#.. ..###..### #.#.#####.
..#.#..#.# ###...#.#. .#..######
.###....#. ..#....#.. ..#.......
###.##.##. .#.#.#..## ######....
.###.##### ##...#.### ####.#..#.
.##.#....# ##.##.###. .#...#.##.
#...###### ####.#...# #.#####.##
.....#..## #...##..#. ..#.###...
#.####...# ##..#..... ..#.......
#.##...##. ..##.#..#. ..#.###...

#.##...##. ..##.#..#. ..#.###...
##..#.##.. ..#..###.# ##.##....#
##.####... .#.####.#. ..#.###..#
####.#.#.. ...#.##### ###.#..###
.#.####... ...##..##. .######.##
.##..##.#. ....#...## #.#.#.#...
....#..#.# #.#.#.##.# #.###.###.
..#.#..... .#.##.#..# #.###.##..
####.#.... .#..#.##.. .######...
...#.#.#.# ###.##.#.. .##...####

...#.#.#.# ###.##.#.. .##...####
..#.#.###. ..##.##.## #..#.##..#
..####.### ##.#...##. .#.#..#.##
#..#.#..#. ...#.#.#.. .####.###.
.#..####.# #..#.#.#.# ####.###..
.#####..## #####...#. .##....##.
##.##..#.. ..#...#... .####...#.
#.#.###... .##..##... .####.##.#
#...###... ..##...#.. ...#..####
..#.#....# ##.#.#.... ...##.....

For reference, the IDs of the above tiles are:

1951    2311    3079
2729    1427    2473
2971    1489    1171

To check that you've assembled the image correctly, multiply the IDs of the four corner tiles together. If you do this with the assembled tiles from the example above, you get 1951 * 3079 * 2971 * 1171 = 20899048083289.

Assemble the tiles into an image. What do you get if you multiply together the IDs of the four corner tiles?


--- Part Two ---

Now, you're ready to check the image for sea monsters.

The borders of each tile are not part of the actual image; start by removing them.

In the example above, the tiles become:

.#.#..#. ##...#.# #..#####
###....# .#....#. .#......
##.##.## #.#.#..# #####...
###.#### #...#.## ###.#..#
##.#.... #.##.### #...#.##
...##### ###.#... .#####.#
....#..# ...##..# .#.###..
.####... #..#.... .#......

#..#.##. .#..###. #.##....
#.####.. #.####.# .#.###..
###.#.#. ..#.#### ##.#..##
#.####.. ..##..## ######.#
##..##.# ...#...# .#.#.#..
...#..#. .#.#.##. .###.###
.#.#.... #.##.#.. .###.##.
###.#... #..#.##. ######..

.#.#.### .##.##.# ..#.##..
.####.## #.#...## #.#..#.#
..#.#..# ..#.#.#. ####.###
#..####. ..#.#.#. ###.###.
#####..# ####...# ##....##
#.##..#. .#...#.. ####...#
.#.###.. ##..##.. ####.##.
...###.. .##...#. ..#..###

Remove the gaps to form the actual image:

.#.#..#.##...#.##..#####
###....#.#....#..#......
##.##.###.#.#..######...
###.#####...#.#####.#..#
##.#....#.##.####...#.##
...########.#....#####.#
....#..#...##..#.#.###..
.####...#..#.....#......
#..#.##..#..###.#.##....
#.####..#.####.#.#.###..
###.#.#...#.######.#..##
#.####....##..########.#
##..##.#...#...#.#.#.#..
...#..#..#.#.##..###.###
.#.#....#.##.#...###.##.
###.#...#..#.##.######..
.#.#.###.##.##.#..#.##..
.####.###.#...###.#..#.#
..#.#..#..#.#.#.####.###
#..####...#.#.#.###.###.
#####..#####...###....##
#.##..#..#...#..####...#
.#.###..##..##..####.##.
...###...##...#...#..###

Now, you're ready to search for sea monsters! Because your image is monochrome, a sea monster will look like this:

                  #
#    ##    ##    ###
 #  #  #  #  #  #

When looking for this pattern in the image, the spaces can be anything; only the # need to match. Also, you might need to rotate or flip your image before it's oriented correctly to find sea monsters. In the above image, after flipping and rotating it to the appropriate orientation, there are two sea monsters (marked with O):

.####...#####..#...###..
#####..#..#.#.####..#.#.
.#.#...#.###...#.##.O#..
#.O.##.OO#.#.OO.##.OOO##
..#O.#O#.O##O..O.#O##.##
...#.#..##.##...#..#..##
#.##.#..#.#..#..##.#.#..
.###.##.....#...###.#...
#.####.#.#....##.#..#.#.
##...#..#....#..#...####
..#.##...###..#.#####..#
....#.##.#.#####....#...
..##.##.###.....#.##..#.
#...#...###..####....##.
.#.##...#.##.#.#.###...#
#.###.#..####...##..#...
#.###...#.##...#.##O###.
.O##.#OO.###OO##..OOO##.
..O#.O..O..O.#O##O##.###
#.#..##.########..#..##.
#.#####..#.#...##..#....
#....##..#.#########..##
#...#.....#..##...###.##
#..###....##.#...##.##.#

Determine how rough the waters are in the sea monsters' habitat by counting the number of # that are not part of a sea monster. In the above example, the habitat's water roughness is 273.

How many # are not part of a sea monster?

 */
private class Day20(
) {
    private val tiles = mutableSetOf<Tile>()
    private val allBorders = mutableMapOf<Pair<Side, Border>, MutableSet<TileOrientation>>()
    private val monster: List<String> = ("" +
            "                  # \n" +
            "#    ##    ##    ###\n" +
            " #  #  #  #  #  #   ").lines()

    fun solve1(): Long {
        return findArrangement()
//            .also { print("\n\n${produceImage(it)}\n\n") }
            .let {
                it.first().first().tile.id * it.first().last().tile.id *
                        it.last().first().tile.id * it.last().last().tile.id
            }
    }

    fun solve2(): Int {
        return produceImage(findArrangement()).lines().filterNot { it.isBlank() }
            .let { estimateRoughness(it) }
    }

    private fun estimateRoughness(rotatedImage: List<String>): Int {
        return Tile(-1, rotatedImage)
            .let { Orientation.all.map { o -> TileOrientation(it, o) } }
            .map { it.tile.orient(it.orientation) }
            .map { removeSeaMonsters(it) }
            // recognizing monsters will reduce the number of '#'
            .minOf { image -> image.sumOf { line -> line.count { it == '#' } } }
    }

    private fun removeSeaMonsters(
        image: Image,
    ): Image {
        val processedImage = Array(image.size) { y -> Array(image.first().length) { x -> image[y][x] } }

        (0 until (image.size - monster.size))
            .onEach { y ->
                (0 until (image.first().length - monster.first().length))
                    .onEach { x -> if (isMonster(x, y, processedImage)) removeMonster(x, y, processedImage) }
            }
        return processedImage.map { String(it.toCharArray()) }
    }

    private fun isMonster(x: Int, y: Int, processedImage: Array<Array<Char>>): Boolean =
        monster.mapIndexed { i, line -> i to line }.stream()
            .allMatch { (i, line) -> monsterMatch(processedImage[y + i].drop(x), line) }

    private fun monsterMatch(imageSubstring: List<Char>, monsterString: String): Boolean =
        monsterString.mapIndexed { i, c -> c != '#' || c == imageSubstring[i] }.stream().allMatch { it }

    private fun removeMonster(x: Int, y: Int, processedImage: Array<Array<Char>>) {
        monster.onEachIndexed { yOffset, line ->
            line.onEachIndexed { xOffset, c ->
                if (c == '#') processedImage[y + yOffset][x + xOffset] = 'O'
            }
        }
    }

    private fun produceImage(
        solution: List<List<TileOrientation>>,
        skipMatchingBorder: Boolean = true,
        skipAllBorder: Boolean = true
    ): String {
        val buffer = StringBuilder()
        solution.onEach { row ->
            // skip horizontal border on one side
            val height = row.first().tile.content.size - if (skipMatchingBorder || skipAllBorder) 1 else 0
            // skip vertical border on one side
            val width = row.first().tile.content.first().length - if (skipMatchingBorder || skipAllBorder) 1 else 0
            val from = if (skipMatchingBorder || skipAllBorder) 1 else 0
            (from until height)
                .onEach { y ->
                    row.onEach { to ->
                        buffer.append(to.tile.orient(to.orientation)[y].substring(from until width))
                    }
                    buffer.append("\n")
                }
        }
        return buffer.toString()
    }

    private fun findArrangement(): List<List<TileOrientation>> {
        val minRows = 2
        val maxRows = tiles.size / minRows

        (minRows..maxRows)
            .filter { rows ->
                // can we make a rectangle of this size?
                tiles.size % rows == 0
            }
            .map { it to tiles.size / it }
            .filter { (x, y) -> x <= y } // other sizes will be a rotation of previous ones
            .mapNotNull { (rows, columns) -> findArrangement(rows, columns) }
            .map { return it }

        throw RuntimeException("Could not find any possible arrangements")
    }

    private fun findArrangement(rows: Int, columns: Int): List<List<TileOrientation>>? {
        val tilesLeft = tiles.toMutableSet()
        val tilesGrid = Array<Array<TileOrientation?>>(rows) { Array(columns) { null } }

        val allCandidates = tilesLeft.flatMap { t -> Orientation.all.map { TileOrientation(t, it) } }
        return allCandidates.stream()
            .map { firstTile ->
                findArrangement(tilesGrid.deepCopy().also { it[0][0] = firstTile }, tilesLeft - firstTile.tile)
            }
            .filter { it != null }
            .findFirst().orElse(null)
    }

    private fun findArrangement(
        tilesGrid: Array<Array<TileOrientation?>>,
        tilesLeft: Set<Tile>
    ): List<List<TileOrientation>>? {
        val freeSpot = tilesGrid
            .flatMapIndexed { y, row -> row.mapIndexedNotNull { x, spot -> if (spot == null) x to y else null } }
            .firstOrNull()
        return if (freeSpot == null) {
            // solved!
            tilesGrid
                .map { row -> row.map { it!! }.toList() }
                .toList()
        } else {
            // find a tile that can be put on this spot
            val (x, y) = freeSpot
            val definingBordersForTheSpot = definingBordersForTheSpot(tilesGrid, freeSpot)

            val candidates: Set<TileOrientation> =
                definingBordersForTheSpot
                    .map { (side, border) ->
                        allBorders[side to border]
                            ?.filter { candidate -> tilesLeft.contains(candidate.tile) }
                            ?.toSet() ?: emptySet()
                    }.let {
                        // the candidate has to satisfy all defining borders simultaneously
                        return@let if (it.isNotEmpty()) {
                            it.reduce(Set<TileOrientation>::intersect)
                        } else emptySet()
                    }

            candidates.stream()
                .map { t -> findArrangement(tilesGrid.deepCopy().also { it[y][x] = t }, tilesLeft - t.tile) }
                .filter { it != null }
                .findFirst().orElse(null)
        }
    }

    private fun definingBordersForTheSpot(
        grid: Array<Array<TileOrientation?>>,
        spot: Pair<Int, Int>
    ): Map<Side, Border> = Side.values()
        .mapNotNull { side ->
            when (side) {
                Side.L -> grid.getTile(spot.first - 1, spot.second)?.border(side.opposite)
                Side.R -> grid.getTile(spot.first + 1, spot.second)?.border(side.opposite)
                Side.T -> grid.getTile(spot.first, spot.second - 1)?.border(side.opposite)
                Side.B -> grid.getTile(spot.first, spot.second + 1)?.border(side.opposite)
            }?.let { side to it }
        }.associateBy({ it.first }, { it.second })

    private fun Array<Array<TileOrientation?>>.getTile(x: Int, y: Int): TileOrientation? =
        if (y in 0 until size && x in 0 until first().size) this[y][x] else null

    private inline fun <reified T> Array<Array<T?>>.deepCopy(): Array<Array<T?>> =
        Array(size) { y -> Array(first().size) { x -> this[y][x] } }

    constructor(testInput: String) : this() {
        var imageId = 0L
        var image = mutableListOf<String>()
        testInput.lines().iterator().forEachRemaining { line ->
            if (line.isBlank()) {
                if (image.isNotEmpty()) {
                    tiles.add(Tile(imageId, image))
                }
                image = mutableListOf(); imageId = -1
            } else if (line.startsWith("Tile ")) {
                imageId = line.substringAfter("Tile ").removeSuffix(":").toLong()
            } else {
                image.add(line)
            }
        }
        if (image.isNotEmpty()) tiles.add(Tile(imageId, image))

        tiles.forEach { tile ->
            Orientation.all.map { o -> TileOrientation(tile, o) }
                .forEach { tileOrient ->
                    Side.values().onEach { side ->
                        allBorders.computeIfAbsent(side to tileOrient.border(side)) { _ -> mutableSetOf() }
                            .add(tileOrient)
                    }
                }
        }
    }

    data class Tile(val id: Long, val content: Image) {
        private val orientations = mutableMapOf<Orientation, Image>()
        fun orient(orientation: Orientation): Image = orientations.computeIfAbsent(orientation) {
            content.flip(orientation.flip).rotate(orientation.rotation)
        }

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as Tile

            if (id != other.id) return false

            return true
        }

        override fun hashCode(): Int {
            return id.hashCode()
        }
    }

    data class TileOrientation(val tile: Tile, val orientation: Orientation) {
        private val borders = mutableMapOf<Side, Border>()
        fun border(side: Side) = borders.computeIfAbsent(side) { s ->
            tile.orient(orientation).let { data ->
                when (s) {
                    Side.L -> String(data.map { it.first() }.toTypedArray().toCharArray())
                    Side.R -> String(data.map { it.last() }.toTypedArray().toCharArray())
                    Side.T -> data.first()
                    Side.B -> data.last()
                }
            }
        }
    }

    enum class Side {
        L, R, T, B;

        val opposite: Side by lazy {
            when (this) {
                L -> R
                R -> L
                T -> B
                B -> T
            }
        }
    }

    enum class Flip {
        N, H//, V
    }

    enum class Rotation {
        _0, _90, _180, _270
    }

    data class Orientation(val flip: Flip, val rotation: Rotation) {
        companion object {
            val all: List<Orientation> =
                Flip.values()
                    .flatMap { f -> Rotation.values().map { r -> Orientation(f, r) } }
        }
    }
}

private fun Image.flip(flip: Day20.Flip): Image =
    when (flip) {
        Day20.Flip.N -> this
        Day20.Flip.H -> this.map { it.reversed() }
//        Day20.Flip.V -> this.reversed()
    }

private fun Image.rotate(rotation: Day20.Rotation): Image =
    when (rotation) {
        Day20.Rotation._0 -> this
        Day20.Rotation._90 -> this.mapIndexed { y, row ->
            row.mapIndexed { x, _ -> this[x][size - y - 1] }
                .let { String(it.toCharArray()) }
        }
        Day20.Rotation._180 -> this.rotate(Day20.Rotation._90).rotate(Day20.Rotation._90)
        Day20.Rotation._270 -> this.rotate(Day20.Rotation._180).rotate(Day20.Rotation._90)
    }


typealias Image = List<String>
typealias Border = String

fun main() {
    listOf(
        { verifyResult(20899048083289, Day20(checkInput).solve1()) },
        { verifyResult(273, Day20(checkInput).solve2()) },
        { println("Result is " + Day20(testInput).solve2()) }
    ).onEachIndexed { i, test -> measure(test, i) }
}

private const val checkInput: String = "" +
        "Tile 2311:\n" +
        "..##.#..#.\n" +
        "##..#.....\n" +
        "#...##..#.\n" +
        "####.#...#\n" +
        "##.##.###.\n" +
        "##...#.###\n" +
        ".#.#.#..##\n" +
        "..#....#..\n" +
        "###...#.#.\n" +
        "..###..###\n" +
        "\n" +
        "Tile 1951:\n" +
        "#.##...##.\n" +
        "#.####...#\n" +
        ".....#..##\n" +
        "#...######\n" +
        ".##.#....#\n" +
        ".###.#####\n" +
        "###.##.##.\n" +
        ".###....#.\n" +
        "..#.#..#.#\n" +
        "#...##.#..\n" +
        "\n" +
        "Tile 1171:\n" +
        "####...##.\n" +
        "#..##.#..#\n" +
        "##.#..#.#.\n" +
        ".###.####.\n" +
        "..###.####\n" +
        ".##....##.\n" +
        ".#...####.\n" +
        "#.##.####.\n" +
        "####..#...\n" +
        ".....##...\n" +
        "\n" +
        "Tile 1427:\n" +
        "###.##.#..\n" +
        ".#..#.##..\n" +
        ".#.##.#..#\n" +
        "#.#.#.##.#\n" +
        "....#...##\n" +
        "...##..##.\n" +
        "...#.#####\n" +
        ".#.####.#.\n" +
        "..#..###.#\n" +
        "..##.#..#.\n" +
        "\n" +
        "Tile 1489:\n" +
        "##.#.#....\n" +
        "..##...#..\n" +
        ".##..##...\n" +
        "..#...#...\n" +
        "#####...#.\n" +
        "#..#.#.#.#\n" +
        "...#.#.#..\n" +
        "##.#...##.\n" +
        "..##.##.##\n" +
        "###.##.#..\n" +
        "\n" +
        "Tile 2473:\n" +
        "#....####.\n" +
        "#..#.##...\n" +
        "#.##..#...\n" +
        "######.#.#\n" +
        ".#...#.#.#\n" +
        ".#########\n" +
        ".###.#..#.\n" +
        "########.#\n" +
        "##...##.#.\n" +
        "..###.#.#.\n" +
        "\n" +
        "Tile 2971:\n" +
        "..#.#....#\n" +
        "#...###...\n" +
        "#.#.###...\n" +
        "##.##..#..\n" +
        ".#####..##\n" +
        ".#..####.#\n" +
        "#..#.#..#.\n" +
        "..####.###\n" +
        "..#.#.###.\n" +
        "...#.#.#.#\n" +
        "\n" +
        "Tile 2729:\n" +
        "...#.#.#.#\n" +
        "####.#....\n" +
        "..#.#.....\n" +
        "....#..#.#\n" +
        ".##..##.#.\n" +
        ".#.####...\n" +
        "####.#.#..\n" +
        "##.####...\n" +
        "##..#.##..\n" +
        "#.##...##.\n" +
        "\n" +
        "Tile 3079:\n" +
        "#.#.#####.\n" +
        ".#..######\n" +
        "..#.......\n" +
        "######....\n" +
        "####.#..#.\n" +
        ".#...#.##.\n" +
        "#.#####.##\n" +
        "..#.###...\n" +
        "..#.......\n" +
        "..#.###...\n"


private val testInput by lazy { readResourceFile("/advent2020/day20-task1.txt") }