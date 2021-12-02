package advent2020

import measure
import verifyResult

/*
--- Day 23: Crab Cups ---

The small crab challenges you to a game! The crab is going to mix up some cups, and you have to predict where they'll end up.

The cups will be arranged in a circle and labeled clockwise (your puzzle input). For example, if your labeling were 32415, there would be five cups in the circle; going clockwise around the circle from the first cup, the cups would be labeled 3, 2, 4, 1, 5, and then back to 3 again.

Before the crab starts, it will designate the first cup in your list as the current cup. The crab is then going to do 100 moves.

Each move, the crab does the following actions:

    The crab picks up the three cups that are immediately clockwise of the current cup. They are removed from the circle; cup spacing is adjusted as necessary to maintain the circle.
    The crab selects a destination cup: the cup with a label equal to the current cup's label minus one. If this would select one of the cups that was just picked up, the crab will keep subtracting one until it finds a cup that wasn't just picked up. If at any point in this process the value goes below the lowest value on any cup's label, it wraps around to the highest value on any cup's label instead.
    The crab places the cups it just picked up so that they are immediately clockwise of the destination cup. They keep the same order as when they were picked up.
    The crab selects a new current cup: the cup which is immediately clockwise of the current cup.

For example, suppose your cup labeling were 389125467. If the crab were to do merely 10 moves, the following changes would occur:

-- move 1 --
cups: (3) 8  9  1  2  5  4  6  7
pick up: 8, 9, 1
destination: 2

-- move 2 --
cups:  3 (2) 8  9  1  5  4  6  7
pick up: 8, 9, 1
destination: 7

-- move 3 --
cups:  3  2 (5) 4  6  7  8  9  1
pick up: 4, 6, 7
destination: 3

-- move 4 --
cups:  7  2  5 (8) 9  1  3  4  6
pick up: 9, 1, 3
destination: 7

-- move 5 --
cups:  3  2  5  8 (4) 6  7  9  1
pick up: 6, 7, 9
destination: 3

-- move 6 --
cups:  9  2  5  8  4 (1) 3  6  7
pick up: 3, 6, 7
destination: 9

-- move 7 --
cups:  7  2  5  8  4  1 (9) 3  6
pick up: 3, 6, 7
destination: 8

-- move 8 --
cups:  8  3  6  7  4  1  9 (2) 5
pick up: 5, 8, 3
destination: 1

-- move 9 --
cups:  7  4  1  5  8  3  9  2 (6)
pick up: 7, 4, 1
destination: 5

-- move 10 --
cups: (5) 7  4  1  8  3  9  2  6
pick up: 7, 4, 1
destination: 3

-- final --
cups:  5 (8) 3  7  4  1  9  2  6

In the above example, the cups' values are the labels as they appear moving clockwise around the circle; the current cup is marked with ( ).

After the crab is done, what order will the cups be in? Starting after the cup labeled 1, collect the other cups' labels clockwise into a single string with no extra characters; each number except 1 should appear exactly once. In the above example, after 10 moves, the cups clockwise from 1 are labeled 9, 2, 6, 5, and so on, producing 92658374. If the crab were to complete all 100 moves, the order after cup 1 would be 67384529.

Using your labeling, simulate 100 moves. What are the labels on the cups after cup 1?

Your puzzle input is 716892543.
 */
private class Day23(
    val maxLabel: Int = 9
) {
    private lateinit var cups: CircularList<Int>


    fun solve1(rounds: Int = 100): String {
        repeat(rounds) { moveCups() }

        return cups.iterate(1).asSequence().joinToString(separator = "") { it.toString() }
            .substring(1..8)
    }

    fun solve2(rounds: Int = 10_000_000): Long {
        repeat(rounds) { moveCups() }

        return cups.find(1).let { it.next.label.toLong() * it.next.next.label }
    }

    private fun moveCups() {
        val removedCups = listOf(
            cups.removeAfter(cups.first().label),
            cups.removeAfter(cups.first().label),
            cups.removeAfter(cups.first().label),
        )

        var destinationLabel = cups.first().label - 1
        while (!cups.contains(destinationLabel)) destinationLabel =
            if (destinationLabel <= 1) maxLabel else destinationLabel - 1

        removedCups.reversed().forEach {
            cups.add(it, destinationLabel)
        }
        cups.rotate()
    }

    constructor(maxLabel: Int, testInput: String) : this(maxLabel) {
        cups = CircularList<Int>()
        testInput.toCharArray()
            .onEach { cups.add(it.digitToInt()) }

        var label = 10 // I know...
        while (cups.size < maxLabel) cups.add(label++)
    }

    private class CircularList<T> {
        var first: Element<T>? = null
            private set
        private var last: Element<T>? = null

        private val elementsLookup: MutableMap<T, Element<T>> = mutableMapOf()

        fun first(): Element<T> = first ?: throw NoSuchElementException("List is empty")
        val size get() = elementsLookup.size

        fun add(label: T, after: T? = null) {
            if (after != null && !elementsLookup.containsKey(after)) throw NoSuchElementException("No element with label $after")
            if (elementsLookup.containsKey(label)) throw RuntimeException("The list already contains label $label")

            val newElement = Element(label)
            elementsLookup[label] = newElement
            if (first == null) {
                newElement.next = newElement
                first = newElement
                last = newElement
            } else {
                val destination = if (after != null) elementsLookup[after]!! else last!!
                newElement.next = destination.next
                destination.next = newElement
                if (destination == last) last = newElement
            }
        }

        fun removeAfter(label: T): T {
            val destination = elementsLookup[label] ?: throw NoSuchElementException("No element with label $label")
            val toRemove = destination.next
            elementsLookup.remove(toRemove.label)
            destination.next = toRemove.next
            if (first == toRemove) first = first!!.next
            // TODO update last pointer
            return toRemove.label
        }

        fun rotate() {
            first = first?.next
            last = last?.next
        }

        fun iterate(start: T?): Iterator<T> {
            if (start != null && elementsLookup[start] == null) throw NoSuchElementException("No element with label $start")

            val started = start ?: first!!.label
            var current: Element<T>? = null

            return object : Iterator<T> {
                override fun hasNext(): Boolean = first != null && (current == null || current!!.next.label != started)

                override fun next(): T {
                    if (current == null) {
                        current = elementsLookup[started]!!
                    } else {
                        current = current!!.next
                        if (current!!.label == started) throw NoSuchElementException("Iterator has no more elements")
                    }
                    return current!!.label
                }
            }
        }

        fun contains(label: T): Boolean = elementsLookup.containsKey(label)

        fun find(label: T): Element<T> {
            return elementsLookup[label] ?: throw NoSuchElementException("Can't find element $label")
        }
    }

    private data class Element<T>(val label: T) {
        lateinit var next: Element<T>
    }
}

fun main() {
    listOf(
        { verifyResult("92658374", Day23(9, checkInput).solve1(rounds = 10)) },
        { verifyResult("67384529", Day23(9, checkInput).solve1()) },
        { verifyResult(149245887792L, Day23(1_000_000, checkInput).solve2()) },
        { println("Result is " + Day23(1_000_000, testInput).solve2()) }
    ).onEachIndexed { i, test -> measure(test, i) }
}

private const val checkInput: String = "" +
        "389125467"

private val testInput by lazy { "716892543" }