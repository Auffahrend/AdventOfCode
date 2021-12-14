package advent2021

import measure
import readResourceFile
import verifyResult

/*
--- Day 14: Extended Polymerization ---

The incredible pressures at this depth are starting to put a strain on your submarine. The submarine has polymerization equipment that would produce suitable materials to reinforce the submarine, and the nearby volcanically-active caves should even have the necessary input elements in sufficient quantities.

The submarine manual contains instructions for finding the optimal polymer formula; specifically, it offers a polymer template and a list of pair insertion rules (your puzzle input). You just need to work out what polymer would result after repeating the pair insertion process a few times.

For example:

NNCB

CH -> B
HH -> N
CB -> H
NH -> C
HB -> C
HC -> B
HN -> C
NN -> C
BH -> H
NC -> B
NB -> B
BN -> B
BB -> N
BC -> B
CC -> N
CN -> C

The first line is the polymer template - this is the starting point of the process.

The following section defines the pair insertion rules. A rule like AB -> C means that when elements A and B are immediately adjacent, element C should be inserted between them. These insertions all happen simultaneously.

So, starting with the polymer template NNCB, the first step simultaneously considers all three pairs:

    The first pair (NN) matches the rule NN -> C, so element C is inserted between the first N and the second N.
    The second pair (NC) matches the rule NC -> B, so element B is inserted between the N and the C.
    The third pair (CB) matches the rule CB -> H, so element H is inserted between the C and the B.

Note that these pairs overlap: the second element of one pair is the first element of the next pair. Also, because all pairs are considered simultaneously, inserted elements are not considered to be part of a pair until the next step.

After the first step of this process, the polymer becomes NCNBCHB.

Here are the results of a few steps using the above rules:

Template:     NNCB
After step 1: NCNBCHB
After step 2: NBCCNBBBCBHCB
After step 3: NBBBCNCCNBBNBNBBCHBHHBCHB
After step 4: NBBNBNBBCCNBCNCCNBBNBBNBBBNBBNBBCBHCBHHNHCBBCBHCB

This polymer grows quickly. After step 5, it has length 97; After step 10, it has length 3073. After step 10, B occurs 1749 times, C occurs 298 times, H occurs 161 times, and N occurs 865 times; taking the quantity of the most common element (B, 1749) and subtracting the quantity of the least common element (H, 161) produces 1749 - 161 = 1588.

Apply 10 steps of pair insertion to the polymer template and find the most and least common elements in the result. What do you get if you take the quantity of the most common element and subtract the quantity of the least common element?

Your puzzle answer was 2112.
--- Part Two ---

The resulting polymer isn't nearly strong enough to reinforce the submarine. You'll need to run more steps of the pair insertion process; a total of 40 steps should do it.

In the above example, the most common element is B (occurring 2192039569602 times) and the least common element is H (occurring 3849876073 times); subtracting these produces 2188189693529.

Apply 40 steps of pair insertion to the polymer template and find the most and least common elements in the result. What do you get if you take the quantity of the most common element and subtract the quantity of the least common element?

 */
private class Day14(
) {
    private lateinit var start: String
    private lateinit var rules: Map<String, Pair<String, String>>

    fun solve(steps: Int = 10): Long {
        var pairFrequencies: Map<String, Long> =
            start.windowed(2).groupingBy { it }.eachCount().mapValues { (_, v) -> v.toLong() }
        repeat(steps) {
            pairFrequencies = pairFrequencies.map { (pair, count) ->
                rules[pair]?.let { newPairs ->
                    listOf(newPairs.first to count, newPairs.second to count)
                } ?: emptyList()
            }.flatten()
                .groupingBy { it.first }.aggregate { _, a: Long?, e, _ -> (a ?: 0L) + e.second }
        }
        val elementFrequencies = pairFrequencies.flatMap { (pair, count) ->
            listOf(pair[0] to count, pair[1] to count)
        }.groupingBy { it.first }.aggregate { _, a: Long?, e, _ -> (a ?: 0L) + e.second }
                // each pair doubles element frequency, except the very first and last in the chain (they lack a pair each)
            .mapValues { (e, count) -> (count + (if (start.first() == e || start.last() == e) 1 else 0)) / 2  }

        return (elementFrequencies.values.maxOfOrNull { it } ?: 0) - (elementFrequencies.values.minOrNull() ?: 0)
    }

    constructor(testInput: String) : this() {
        start = testInput.lines().first()
        rules = testInput.lines().drop(1).filter { it.isNotBlank() }
            .map { it.split(" -> ") }
            .groupBy { it[0] }.mapValues { (_, v) -> v[0][1] }// produces map of <CH -> B>
            .mapValues { (k, v) -> k[0] + v to v + k[1] } // produces map of <CH -> CB, BH>
    }
}

fun main() {
    listOf(
        { verifyResult(1588, Day14(checkInput).solve(10)) },
        { verifyResult(2188189693529, Day14(checkInput).solve(40)) },
        { println("Result is " + Day14(testInput).solve(40)) }
    ).onEachIndexed { i, test -> measure(test, i) }
}

private const val checkInput: String = "" +
        "NNCB\n" +
        "\n" +
        "CH -> B\n" +
        "HH -> N\n" +
        "CB -> H\n" +
        "NH -> C\n" +
        "HB -> C\n" +
        "HC -> B\n" +
        "HN -> C\n" +
        "NN -> C\n" +
        "BH -> H\n" +
        "NC -> B\n" +
        "NB -> B\n" +
        "BN -> B\n" +
        "BB -> N\n" +
        "BC -> B\n" +
        "CC -> N\n" +
        "CN -> C\n"

private val testInput by lazy { readResourceFile("/advent2021/day14-task1.txt") }