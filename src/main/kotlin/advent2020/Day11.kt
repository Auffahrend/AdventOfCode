package advent2020

import readResourceFile
import verifyResult
import java.util.*

/*
--- Day 11: Seating System ---

Your plane lands with plenty of time to spare. The final leg of your journey is a ferry that goes directly to the tropical island where you can finally start your vacation. As you reach the waiting area to board the ferry, you realize you're so early, nobody else has even arrived yet!

By modeling the process people use to choose (or abandon) their seat in the waiting area, you're pretty sure you can predict the best place to sit. You make a quick map of the seat layout (your puzzle input).

The seat layout fits neatly on a grid. Each position is either floor (.), an empty seat (L), or an occupied seat (#). For example, the initial seat layout might look like this:

L.LL.LL.LL
LLLLLLL.LL
L.L.L..L..
LLLL.LL.LL
L.LL.LL.LL
L.LLLLL.LL
..L.L.....
LLLLLLLLLL
L.LLLLLL.L
L.LLLLL.LL

Now, you just need to model the people who will be arriving shortly. Fortunately, people are entirely predictable and always follow a simple set of rules. All decisions are based on the number of occupied seats adjacent to a given seat (one of the eight positions immediately up, down, left, right, or diagonal from the seat). The following rules are applied to every seat simultaneously:

    If a seat is empty (L) and there are no occupied seats adjacent to it, the seat becomes occupied.
    If a seat is occupied (#) and four or more seats adjacent to it are also occupied, the seat becomes empty.
    Otherwise, the seat's state does not change.

Floor (.) never changes; seats don't move, and nobody sits on the floor.

After one round of these rules, every seat in the example layout becomes occupied:

#.##.##.##
#######.##
#.#.#..#..
####.##.##
#.##.##.##
#.#####.##
..#.#.....
##########
#.######.#
#.#####.##

After a second round, the seats with four or more occupied adjacent seats become empty again:

#.LL.L#.##
#LLLLLL.L#
L.L.L..L..
#LLL.LL.L#
#.LL.LL.LL
#.LLLL#.##
..L.L.....
#LLLLLLLL#
#.LLLLLL.L
#.#LLLL.##

This process continues for three more rounds:

#.##.L#.##
#L###LL.L#
L.#.#..#..
#L##.##.L#
#.##.LL.LL
#.###L#.##
..#.#.....
#L######L#
#.LL###L.L
#.#L###.##

#.#L.L#.##
#LLL#LL.L#
L.L.L..#..
#LLL.##.L#
#.LL.LL.LL
#.LL#L#.##
..L.L.....
#L#LLLL#L#
#.LLLLLL.L
#.#L#L#.##

#.#L.L#.##
#LLL#LL.L#
L.#.L..#..
#L##.##.L#
#.#L.LL.LL
#.#L#L#.##
..L.L.....
#L#L##L#L#
#.LLLLLL.L
#.#L#L#.##

At this point, something interesting happens: the chaos stabilizes and further applications of these rules cause no seats to change state! Once people stop moving around, you count 37 occupied seats.

Simulate your seating area by applying the seating rules repeatedly until no seats change state. How many seats end up occupied?

--- Part Two ---

As soon as people start to arrive, you realize your mistake. People don't just care about adjacent seats - they care about the first seat they can see in each of those eight directions!

Now, instead of considering just the eight immediately adjacent seats, consider the first seat in each of those eight directions. For example, the empty seat below would see eight occupied seats:

.......#.
...#.....
.#.......
.........
..#L....#
....#....
.........
#........
...#.....

The leftmost empty seat below would only see one empty seat, but cannot see any of the occupied ones:

.............
.L.L.#.#.#.#.
.............

The empty seat below would see no occupied seats:

.##.##.
#.#.#.#
##...##
...L...
##...##
#.#.#.#
.##.##.

Also, people seem to be more tolerant than you expected: it now takes five or more visible occupied seats for an occupied seat to become empty (rather than four or more from the previous rules). The other rules still apply: empty seats that see no occupied seats become occupied, seats matching no rule don't change, and floor never changes.

Given the same starting layout as above, these new rules cause the seating area to shift around as follows:

L.LL.LL.LL
LLLLLLL.LL
L.L.L..L..
LLLL.LL.LL
L.LL.LL.LL
L.LLLLL.LL
..L.L.....
LLLLLLLLLL
L.LLLLLL.L
L.LLLLL.LL

#.##.##.##
#######.##
#.#.#..#..
####.##.##
#.##.##.##
#.#####.##
..#.#.....
##########
#.######.#
#.#####.##

#.LL.LL.L#
#LLLLLL.LL
L.L.L..L..
LLLL.LL.LL
L.LL.LL.LL
L.LLLLL.LL
..L.L.....
LLLLLLLLL#
#.LLLLLL.L
#.LLLLL.L#

#.L#.##.L#
#L#####.LL
L.#.#..#..
##L#.##.##
#.##.#L.##
#.#####.#L
..#.#.....
LLL####LL#
#.L#####.L
#.L####.L#

#.L#.L#.L#
#LLLLLL.LL
L.L.L..#..
##LL.LL.L#
L.LL.LL.L#
#.LLLLL.LL
..L.L.....
LLLLLLLLL#
#.LLLLL#.L
#.L#LL#.L#

#.L#.L#.L#
#LLLLLL.LL
L.L.L..#..
##L#.#L.L#
L.L#.#L.L#
#.L####.LL
..#.#.....
LLL###LLL#
#.LLLLL#.L
#.L#LL#.L#

#.L#.L#.L#
#LLLLLL.LL
L.L.L..#..
##L#.#L.L#
L.L#.LL.L#
#.LLLL#.LL
..#.L.....
LLL###LLL#
#.LLLLL#.L
#.L#LL#.L#

Again, at this point, people stop shifting around and the seating area reaches equilibrium. Once this occurs, you count 26 occupied seats.

Given the new visibility method and the rule change for occupied seats becoming empty, once equilibrium is reached, how many seats end up occupied?

 */
private class Day11 {
    private val initialSeats: Seats

    private constructor(initialSeats: Seats) {
        this.initialSeats = initialSeats
    }

    val taskPart = 2

    fun countOccupiedSeats(): Int {
        var previousState = initialSeats
        var newState = initialSeats.next()
        while (newState != previousState) {
            previousState = newState
            newState = newState.next()
        }
        return newState.seats.flatten().count { it == SeatState.Occupied }
    }

    constructor(seatMap: String) : this(
        seatMap.lines().filter { it.isNotBlank() }
            .map { line ->
                line.toCharArray()
                    .map { SeatState.of(it) }
            }
            .let { Seats(it) }
    )


    private data class Seats(val seats: List<List<SeatState>>) {
        fun next(): Seats =
            seats.mapIndexed { i, row ->
                row.mapIndexed { j, seat -> nextSeatState(seat, i, j) }
            }
                .let { Seats(it) }

        private fun nextSeatState(seat: SeatState, i: Int, j: Int) = when (seat) {
            SeatState.Floor -> SeatState.Floor
            SeatState.Empty -> if (hasNeighbors(i, j)) SeatState.Empty else SeatState.Occupied
            SeatState.Occupied -> if (isTooCrowded(i, j)) SeatState.Empty else SeatState.Occupied
        }

        private fun withinRange(i: Int, j: Int): Boolean = i in seats.indices && j in seats.first().indices

        private fun neighborsOf(i: Int, j: Int): List<SeatState> {
            if (part == 1) {
                return listOf(-1, 0, 1)
                    .flatMap { i1 -> listOf(-1, 0, 1).map { j1 -> i + i1 to j + j1 } }
                    .filter { (i1, j1) -> i != i1 || j != j1 }
                    .filter { (i1, j1) -> withinRange(i1, j1) }
                    .map { (i1, j1) -> seats[i1][j1] }
            } else {
                val directions = listOf(
                    -1 to -1, -1 to 0, -1 to 1,
                    0 to -1, 0 to 1,
                    1 to -1, 1 to 0, 1 to 1,
                )
                return directions.map { (dx, dy) ->
                    var x = i + dx
                    var y = j + dy
                    while (withinRange(x, y) && seats[x][y] == SeatState.Floor) {
                        x += dx; y += dy
                    }
                    if (withinRange(x, y)) seats[x][y] else null
                }.filterNotNull()
            }
        }

        private fun hasNeighbors(i: Int, j: Int): Boolean = neighborsOf(i, j).any { it == SeatState.Occupied }

        private fun isTooCrowded(i: Int, j: Int): Boolean = neighborsOf(i, j).count { it == SeatState.Occupied } >= 5
    }

    companion object {
        private const val part = 2
    }


    private enum class SeatState {
        Empty,
        Occupied,
        Floor
        ;

        companion object {
            fun of(serialized: Char): SeatState = when (serialized) {
                'L' -> Empty
                '#' -> Occupied
                '.' -> Floor
                else -> throw RuntimeException("Unknown seat state $serialized")
            }
        }
    }
}

fun main() {
    verifyResult(26, Day11(checkInput).countOccupiedSeats())
    println("Check succeeded")

    println(Day11(testInput).countOccupiedSeats())
}

private const val checkInput: String = "" +
        "L.LL.LL.LL\n" +
        "LLLLLLL.LL\n" +
        "L.L.L..L..\n" +
        "LLLL.LL.LL\n" +
        "L.LL.LL.LL\n" +
        "L.LLLLL.LL\n" +
        "..L.L.....\n" +
        "LLLLLLLLLL\n" +
        "L.LLLLLL.L\n" +
        "L.LLLLL.LL"

private val testInput by lazy { readResourceFile("/advent2020/day11-task1.txt") }