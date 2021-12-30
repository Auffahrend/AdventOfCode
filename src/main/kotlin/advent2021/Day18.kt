package advent2021

import measure
import readResourceFile
import verifyResult

/*
--- Day 18: Snailfish ---

You descend into the ocean trench and encounter some snailfish. They say they saw the sleigh keys! They'll even tell you which direction the keys went if you help one of the smaller snailfish with his math homework.

Snailfish numbers aren't like regular numbers. Instead, every snailfish number is a pair - an ordered list of two elements. Each element of the pair can be either a regular number or another pair.

Pairs are written as [x,y], where x and y are the elements within the pair. Here are some example snailfish numbers, one snailfish number per line:

[1,2]
[[1,2],3]
[9,[8,7]]
[[1,9],[8,5]]
[[[[1,2],[3,4]],[[5,6],[7,8]]],9]
[[[9,[3,8]],[[0,9],6]],[[[3,7],[4,9]],3]]
[[[[1,3],[5,3]],[[1,3],[8,7]]],[[[4,9],[6,9]],[[8,2],[7,3]]]]

This snailfish homework is about addition. To add two snailfish numbers, form a pair from the left and right parameters of the addition operator. For example, [1,2] + [[3,4],5] becomes [[1,2],[[3,4],5]].

There's only one problem: snailfish numbers must always be reduced, and the process of adding two snailfish numbers can result in snailfish numbers that need to be reduced.

To reduce a snailfish number, you must repeatedly do the first action in this list that applies to the snailfish number:

    If any pair is nested inside four pairs, the leftmost such pair explodes.
    If any regular number is 10 or greater, the leftmost such regular number splits.

Once no action in the above list applies, the snailfish number is reduced.

During reduction, at most one action applies, after which the process returns to the top of the list of actions. For example, if split produces a pair that meets the explode criteria, that pair explodes before other splits occur.

To explode a pair, the pair's left value is added to the first regular number to the left of the exploding pair (if any), and the pair's right value is added to the first regular number to the right of the exploding pair (if any). Exploding pairs will always consist of two regular numbers. Then, the entire exploding pair is replaced with the regular number 0.

Here are some examples of a single explode action:

    [[[[[9,8],1],2],3],4] becomes [[[[0,9],2],3],4] (the 9 has no regular number to its left, so it is not added to any regular number).
    [7,[6,[5,[4,[3,2]]]]] becomes [7,[6,[5,[7,0]]]] (the 2 has no regular number to its right, and so it is not added to any regular number).
    [[6,[5,[4,[3,2]]]],1] becomes [[6,[5,[7,0]]],3].
    [[3,[2,[1,[7,3]]]],[6,[5,[4,[3,2]]]]] becomes [[3,[2,[8,0]]],[9,[5,[4,[3,2]]]]] (the pair [3,2] is unaffected because the pair [7,3] is further to the left; [3,2] would explode on the next action).
    [[3,[2,[8,0]]],[9,[5,[4,[3,2]]]]] becomes [[3,[2,[8,0]]],[9,[5,[7,0]]]].

To split a regular number, replace it with a pair; the left element of the pair should be the regular number divided by two and rounded down, while the right element of the pair should be the regular number divided by two and rounded up. For example, 10 becomes [5,5], 11 becomes [5,6], 12 becomes [6,6], and so on.

Here is the process of finding the reduced result of [[[[4,3],4],4],[7,[[8,4],9]]] + [1,1]:

after addition: [[[[[4,3],4],4],[7,[[8,4],9]]],[1,1]]
after explode:  [[[[0,7],4],[7,[[8,4],9]]],[1,1]]
after explode:  [[[[0,7],4],[15,[0,13]]],[1,1]]
after split:    [[[[0,7],4],[[7,8],[0,13]]],[1,1]]
after split:    [[[[0,7],4],[[7,8],[0,[6,7]]]],[1,1]]
after explode:  [[[[0,7],4],[[7,8],[6,0]]],[8,1]]

Once no reduce actions apply, the snailfish number that remains is the actual result of the addition operation: [[[[0,7],4],[[7,8],[6,0]]],[8,1]].

The homework assignment involves adding up a list of snailfish numbers (your puzzle input). The snailfish numbers are each listed on a separate line. Add the first snailfish number and the second, then add that result and the third, then add that result and the fourth, and so on until all numbers in the list have been used once.

For example, the final sum of this list is [[[[1,1],[2,2]],[3,3]],[4,4]]:

[1,1]
[2,2]
[3,3]
[4,4]

The final sum of this list is [[[[3,0],[5,3]],[4,4]],[5,5]]:

[1,1]
[2,2]
[3,3]
[4,4]
[5,5]

The final sum of this list is [[[[5,0],[7,4]],[5,5]],[6,6]]:

[1,1]
[2,2]
[3,3]
[4,4]
[5,5]
[6,6]

Here's a slightly larger example:

[[[0,[4,5]],[0,0]],[[[4,5],[2,6]],[9,5]]]
[7,[[[3,7],[4,3]],[[6,3],[8,8]]]]
[[2,[[0,8],[3,4]]],[[[6,7],1],[7,[1,6]]]]
[[[[2,4],7],[6,[0,5]]],[[[6,8],[2,8]],[[2,1],[4,5]]]]
[7,[5,[[3,8],[1,4]]]]
[[2,[2,2]],[8,[8,1]]]
[2,9]
[1,[[[9,3],9],[[9,0],[0,7]]]]
[[[5,[7,4]],7],1]
[[[[4,2],2],6],[8,7]]

The final sum [[[[8,7],[7,7]],[[8,6],[7,7]]],[[[0,7],[6,6]],[8,7]]] is found after adding up the above snailfish numbers:

  [[[0,[4,5]],[0,0]],[[[4,5],[2,6]],[9,5]]]
+ [7,[[[3,7],[4,3]],[[6,3],[8,8]]]]
= [[[[4,0],[5,4]],[[7,7],[6,0]]],[[8,[7,7]],[[7,9],[5,0]]]]

  [[[[4,0],[5,4]],[[7,7],[6,0]]],[[8,[7,7]],[[7,9],[5,0]]]]
+ [[2,[[0,8],[3,4]]],[[[6,7],1],[7,[1,6]]]]
= [[[[6,7],[6,7]],[[7,7],[0,7]]],[[[8,7],[7,7]],[[8,8],[8,0]]]]

  [[[[6,7],[6,7]],[[7,7],[0,7]]],[[[8,7],[7,7]],[[8,8],[8,0]]]]
+ [[[[2,4],7],[6,[0,5]]],[[[6,8],[2,8]],[[2,1],[4,5]]]]
= [[[[7,0],[7,7]],[[7,7],[7,8]]],[[[7,7],[8,8]],[[7,7],[8,7]]]]

  [[[[7,0],[7,7]],[[7,7],[7,8]]],[[[7,7],[8,8]],[[7,7],[8,7]]]]
+ [7,[5,[[3,8],[1,4]]]]
= [[[[7,7],[7,8]],[[9,5],[8,7]]],[[[6,8],[0,8]],[[9,9],[9,0]]]]

  [[[[7,7],[7,8]],[[9,5],[8,7]]],[[[6,8],[0,8]],[[9,9],[9,0]]]]
+ [[2,[2,2]],[8,[8,1]]]
= [[[[6,6],[6,6]],[[6,0],[6,7]]],[[[7,7],[8,9]],[8,[8,1]]]]

  [[[[6,6],[6,6]],[[6,0],[6,7]]],[[[7,7],[8,9]],[8,[8,1]]]]
+ [2,9]
= [[[[6,6],[7,7]],[[0,7],[7,7]]],[[[5,5],[5,6]],9]]

  [[[[6,6],[7,7]],[[0,7],[7,7]]],[[[5,5],[5,6]],9]]
+ [1,[[[9,3],9],[[9,0],[0,7]]]]
= [[[[7,8],[6,7]],[[6,8],[0,8]]],[[[7,7],[5,0]],[[5,5],[5,6]]]]

  [[[[7,8],[6,7]],[[6,8],[0,8]]],[[[7,7],[5,0]],[[5,5],[5,6]]]]
+ [[[5,[7,4]],7],1]
= [[[[7,7],[7,7]],[[8,7],[8,7]]],[[[7,0],[7,7]],9]]

  [[[[7,7],[7,7]],[[8,7],[8,7]]],[[[7,0],[7,7]],9]]
+ [[[[4,2],2],6],[8,7]]
= [[[[8,7],[7,7]],[[8,6],[7,7]]],[[[0,7],[6,6]],[8,7]]]

To check whether it's the right answer, the snailfish teacher only checks the magnitude of the final sum. The magnitude of a pair is 3 times the magnitude of its left element plus 2 times the magnitude of its right element. The magnitude of a regular number is just that number.

For example, the magnitude of [9,1] is 3*9 + 2*1 = 29; the magnitude of [1,9] is 3*1 + 2*9 = 21. Magnitude calculations are recursive: the magnitude of [[9,1],[1,9]] is 3*29 + 2*21 = 129.

Here are a few more magnitude examples:

    [[1,2],[[3,4],5]] becomes 143.
    [[[[0,7],4],[[7,8],[6,0]]],[8,1]] becomes 1384.
    [[[[1,1],[2,2]],[3,3]],[4,4]] becomes 445.
    [[[[3,0],[5,3]],[4,4]],[5,5]] becomes 791.
    [[[[5,0],[7,4]],[5,5]],[6,6]] becomes 1137.
    [[[[8,7],[7,7]],[[8,6],[7,7]]],[[[0,7],[6,6]],[8,7]]] becomes 3488.

So, given this example homework assignment:

[[[0,[5,8]],[[1,7],[9,6]]],[[4,[1,2]],[[1,4],2]]]
[[[5,[2,8]],4],[5,[[9,9],0]]]
[6,[[[6,2],[5,6]],[[7,6],[4,7]]]]
[[[6,[0,7]],[0,9]],[4,[9,[9,0]]]]
[[[7,[6,4]],[3,[1,3]]],[[[5,5],1],9]]
[[6,[[7,3],[3,2]]],[[[3,8],[5,7]],4]]
[[[[5,4],[7,7]],8],[[8,3],8]]
[[9,3],[[9,9],[6,[4,9]]]]
[[2,[[7,7],7]],[[5,8],[[9,3],[0,2]]]]
[[[[5,2],5],[8,[3,7]]],[[5,[7,5]],[4,4]]]

The final sum is:

[[[[6,6],[7,6]],[[7,7],[7,0]]],[[[7,7],[7,7]],[[7,8],[9,9]]]]

The magnitude of this final sum is 4140.

Add up all of the snailfish numbers from the homework assignment in the order they appear. What is the magnitude of the final sum?

 */
private class Day18(
    private val numbers: List<SNumber>
) {
    fun solve(): Long {
        val result = numbers.reduce(SNumber::plus)
        println("Result is $result")
        return result.magnitude().toLong()
    }

    fun maxMagnitude(): Int = numbers
        .flatMap { x ->
            numbers
                .filter { y -> x !== y }
                .map { y -> x.copy() + y.copy() }
        }
        .maxOfOrNull { it.magnitude() } ?: 0

    constructor(testInput: String) : this(testInput.lines()
        .filterNot { it.isEmpty() }
        .map { SNumber.of(it) }
    )

    sealed class SNumber {
        var parent: SPair? = null
        abstract fun magnitude(): Int
        abstract fun copy(): SNumber
        fun level(): Int = if (parent == null) 1 else 1 + parent!!.level()

        operator fun plus(other: SNumber): SNumber {
            val result = SPair(this, other)
            do {
                var reduced = false
                if (result.exploded()) reduced = true
                if (!reduced && result.splitted()) reduced = true

            } while (reduced)
            return result
        }

        fun splitted(): Boolean {
            return when {
                this is SPair -> left.splitted() || right.splitted()
                this is SValue && this.value >= 10 -> {
                    if (parent!!.left === this) {
                        parent!!.left = SPair(SValue(value / 2), SValue(value / 2 + value % 2))
                            .also { it.parent = parent }
                    } else {
                        parent!!.right = SPair(SValue(value / 2), SValue(value / 2 + value % 2))
                            .also { it.parent = parent }
                    }
                    parent = null
                    true
                }
                else -> false
            }
        }

        data class SPair(var left: SNumber, var right: SNumber) : SNumber() {
            init {
                left.parent = this
                right.parent = this
            }

            override fun copy(): SNumber = SPair(left.copy(), right.copy())

            override fun magnitude(): Int = 3 * left.magnitude() + 2 * right.magnitude()
            override fun toString(): String = "[$left,$right]"
            fun exploded(): Boolean {
                if (left is SPair && (left as SPair).exploded()) return true
                else if (right is SPair && (right as SPair).exploded()) return true
                else if (left is SValue && right is SValue && level() > 4) {
                    explode()
                    return true
                }
                return false
            }

            private fun explode() {
                findNeighborValue(true)?.let { it.value += (left as SValue).value }
                findNeighborValue(false)?.let { it.value += (right as SValue).value }
                if (parent!!.left === this) parent!!.left = SValue(0).also { it.parent = parent }
                else parent!!.right = SValue(0).also { it.parent = parent }

                this.parent = null
            }

            private fun findNeighborValue(goLeft: Boolean): SValue? {
                var point: SNumber? = this
                val goUpLens: (SPair?) -> SNumber?
                val goDownLens: (SPair?) -> SNumber?
                if (goLeft) {
                    goUpLens = { it?.left }
                    goDownLens = { it?.right }
                } else {
                    goUpLens = { it?.right }
                    goDownLens = { it?.left }
                }
                while (point is SPair && goUpLens(point.parent) === point) point = point.parent
                // move to left sibling - we came from right node now
                point = goUpLens(point?.parent)

                if (point != null) {
                    while (point is SPair) point = goDownLens(point)
                    return point as SValue
                }
                return null
            }
        }

        data class SValue(var value: Int) : SNumber() {
            override fun magnitude(): Int = value
            override fun toString(): String = "$value"
            override fun copy(): SNumber = SValue(value)
        }

        companion object {
            fun of(s: String): SNumber {
                return if (s.startsWith("[") && s.endsWith("]")) {
                    val inner = s.substring(1..s.length - 2)
                    val rootComma = findRootComma(inner)
                    SPair(of(inner.substring(0 until rootComma)), of(inner.substring(rootComma + 1 until inner.length)))
                } else SValue(s.toInt())
            }

            private fun findRootComma(s: String): Int {
                var bracketCounter = 0
                return s.indexOfFirst { c ->
                    if (c == '[') bracketCounter++
                    if (c == ']') bracketCounter--
                    return@indexOfFirst c == ',' && bracketCounter == 0
                }
            }
        }
    }
}

fun main() {
    listOf(
        { verifyResult(3488, Day18(checkInput1).solve()) },
        { verifyResult(4140, Day18(checkInput2).solve()) },
        { verifyResult(3993, Day18(checkInput2).maxMagnitude()) },
        { println("Result is " + Day18(testInput).maxMagnitude()) }
    ).onEachIndexed { i, test -> measure(test, i) }
}

private const val checkInput1: String = "" +
        "[[[0,[4,5]],[0,0]],[[[4,5],[2,6]],[9,5]]]\n" +
        "[7,[[[3,7],[4,3]],[[6,3],[8,8]]]]\n" +
        "[[2,[[0,8],[3,4]]],[[[6,7],1],[7,[1,6]]]]\n" +
        "[[[[2,4],7],[6,[0,5]]],[[[6,8],[2,8]],[[2,1],[4,5]]]]\n" +
        "[7,[5,[[3,8],[1,4]]]]\n" +
        "[[2,[2,2]],[8,[8,1]]]\n" +
        "[2,9]\n" +
        "[1,[[[9,3],9],[[9,0],[0,7]]]]\n" +
        "[[[5,[7,4]],7],1]\n" +
        "[[[[4,2],2],6],[8,7]]\n"
private const val checkInput2: String = "" +
        "[[[0,[5,8]],[[1,7],[9,6]]],[[4,[1,2]],[[1,4],2]]]\n" +
        "[[[5,[2,8]],4],[5,[[9,9],0]]]\n" +
        "[6,[[[6,2],[5,6]],[[7,6],[4,7]]]]\n" +
        "[[[6,[0,7]],[0,9]],[4,[9,[9,0]]]]\n" +
        "[[[7,[6,4]],[3,[1,3]]],[[[5,5],1],9]]\n" +
        "[[6,[[7,3],[3,2]]],[[[3,8],[5,7]],4]]\n" +
        "[[[[5,4],[7,7]],8],[[8,3],8]]\n" +
        "[[9,3],[[9,9],[6,[4,9]]]]\n" +
        "[[2,[[7,7],7]],[[5,8],[[9,3],[0,2]]]]\n" +
        "[[[[5,2],5],[8,[3,7]]],[[5,[7,5]],[4,4]]]\n"

private val testInput by lazy { readResourceFile("/advent2021/day18-task1.txt") }