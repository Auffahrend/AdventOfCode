package advent2021

import measure
import readResourceFile
import verifyResult
import java.util.*

/*
--- Day 8: Seven Segment Search ---

You barely reach the safety of the cave when the whale smashes into the cave mouth, collapsing it. Sensors indicate another exit to this cave at a much greater depth, so you have no choice but to press on.

As your submarine slowly makes its way through the cave system, you notice that the four-digit seven-segment displays in your submarine are malfunctioning; they must have been damaged during the escape. You'll be in a lot of trouble without them, so you'd better figure out what's wrong.

Each digit of a seven-segment display is rendered by turning on or off any of seven segments named a through g:

  0:      1:      2:      3:      4:
 aaaa    ....    aaaa    aaaa    ....
b    c  .    c  .    c  .    c  b    c
b    c  .    c  .    c  .    c  b    c
 ....    ....    dddd    dddd    dddd
e    f  .    f  e    .  .    f  .    f
e    f  .    f  e    .  .    f  .    f
 gggg    ....    gggg    gggg    ....

  5:      6:      7:      8:      9:
 aaaa    aaaa    aaaa    aaaa    aaaa
b    .  b    .  .    c  b    c  b    c
b    .  b    .  .    c  b    c  b    c
 dddd    dddd    ....    dddd    dddd
.    f  e    f  .    f  e    f  .    f
.    f  e    f  .    f  e    f  .    f
 gggg    gggg    ....    gggg    gggg

So, to render a 1, only segments c and f would be turned on; the rest would be off. To render a 7, only segments a, c, and f would be turned on.

The problem is that the signals which control the segments have been mixed up on each display. The submarine is still trying to display numbers by producing output on signal wires a through g, but those wires are connected to segments randomly. Worse, the wire/segment connections are mixed up separately for each four-digit display! (All of the digits within a display use the same connections, though.)

So, you might know that only signal wires b and g are turned on, but that doesn't mean segments b and g are turned on: the only digit that uses two segments is 1, so it must mean segments c and f are meant to be on. With just that information, you still can't tell which wire (b/g) goes to which segment (c/f). For that, you'll need to collect more information.

For each display, you watch the changing signals for a while, make a note of all ten unique signal patterns you see, and then write down a single four digit output value (your puzzle input). Using the signal patterns, you should be able to work out which pattern corresponds to which digit.

For example, here is what you might see in a single entry in your notes:

acedgfb cdfbe gcdfa fbcad dab cefabd cdfgeb eafb cagedb ab |
cdfeb fcadb cdfeb cdbaf

(The entry is wrapped here to two lines so it fits; in your notes, it will all be on a single line.)

Each entry consists of ten unique signal patterns, a | delimiter, and finally the four digit output value. Within an entry, the same wire/segment connections are used (but you don't know what the connections actually are). The unique signal patterns correspond to the ten different ways the submarine tries to render a digit using the current wire/segment connections. Because 7 is the only digit that uses three segments, dab in the above example means that to render a 7, signal lines d, a, and b are on. Because 4 is the only digit that uses four segments, eafb means that to render a 4, signal lines e, a, f, and b are on.

Using this information, you should be able to work out which combination of signal wires corresponds to each of the ten digits. Then, you can decode the four digit output value. Unfortunately, in the above example, all of the digits in the output value (cdfeb fcadb cdfeb cdbaf) use five segments and are more difficult to deduce.

For now, focus on the easy digits. Consider this larger example:

be cfbegad cbdgef fgaecd cgeb fdcge agebfd fecdb fabcd edb |
fdgacbe cefdb cefbgd gcbe
edbfga begcd cbg gc gcadebf fbgde acbgfd abcde gfcbed gfec |
fcgedb cgb dgebacf gc
fgaebd cg bdaec gdafb agbcfd gdcbef bgcad gfac gcb cdgabef |
cg cg fdcagb cbg
fbegcd cbd adcefb dageb afcb bc aefdc ecdab fgdeca fcdbega |
efabcd cedba gadfec cb
aecbfdg fbg gf bafeg dbefa fcge gcbea fcaegb dgceab fcbdga |
gecf egdcabf bgf bfgea
fgeab ca afcebg bdacfeg cfaedg gcfdb baec bfadeg bafgc acf |
gebdcfa ecba ca fadegcb
dbcfg fgd bdegcaf fgec aegbdf ecdfab fbedc dacgb gdcebf gf |
cefg dcbef fcge gbcadfe
bdfegc cbegaf gecbf dfcage bdacg ed bedf ced adcbefg gebcd |
ed bcgafe cdgba cbgef
egadfb cdbfeg cegd fecab cgb gbdefca cg fgcdab egfdb bfceg |
gbdfcae bgc cg cgb
gcafb gcf dcaebfg ecagb gf abcdeg gaef cafbge fdbac fegbdc |
fgae cfgab fg bagce

Because the digits 1, 4, 7, and 8 each use a unique number of segments, you should be able to tell which combinations of signals correspond to those digits. Counting only digits in the output values (the part after | on each line), in the above example, there are 26 instances of digits that use a unique number of segments (highlighted above).

In the output values, how many times do digits 1, 4, 7, or 8 appear?

--- Part Two ---

Through a little deduction, you should now be able to determine the remaining digits. Consider again the first example above:

acedgfb cdfbe gcdfa fbcad dab cefabd cdfgeb eafb cagedb ab |
cdfeb fcadb cdfeb cdbaf

After some careful analysis, the mapping between signal wires and segments only make sense in the following configuration:

 dddd
e    a
e    a
 ffff
g    b
g    b
 cccc

So, the unique signal patterns would correspond to the following digits:

    acedgfb: 8
    cdfbe: 5
    gcdfa: 2
    fbcad: 3
    dab: 7
    cefabd: 9
    cdfgeb: 6
    eafb: 4
    cagedb: 0
    ab: 1

Then, the four digits of the output value can be decoded:

    cdfeb: 5
    fcadb: 3
    cdfeb: 5
    cdbaf: 3

Therefore, the output value for this entry is 5353.

Following this same process for each entry in the second, larger example above, the output value of each entry can be determined:

    fdgacbe cefdb cefbgd gcbe: 8394
    fcgedb cgb dgebacf gc: 9781
    cg cg fdcagb cbg: 1197
    efabcd cedba gadfec cb: 9361
    gecf egdcabf bgf bfgea: 4873
    gebdcfa ecba ca fadegcb: 8418
    cefg dcbef fcge gbcadfe: 4548
    ed bcgafe cdgba cbgef: 1625
    gbdfcae bgc cg cgb: 8717
    fgae cfgab fg bagce: 4315

Adding all of the output values in this larger example produces 61229.

For each entry, determine all of the wire/segment connections and decode the four-digit output values. What do you get if you add up all of the output values?

 */
private class Day08(

) {
    private val input: MutableMap<String, String> = mutableMapOf()

    private val digitWires = mapOf(
        0 to setOf(Wire.A, Wire.B, Wire.C, Wire.E, Wire.F, Wire.G),
        1 to setOf(Wire.C, Wire.F),
        2 to setOf(Wire.A, Wire.C, Wire.D, Wire.E, Wire.G),
        3 to setOf(Wire.A, Wire.C, Wire.D, Wire.F, Wire.G),
        4 to setOf(Wire.B, Wire.C, Wire.D, Wire.F),
        5 to setOf(Wire.A, Wire.B, Wire.D, Wire.F, Wire.G),
        6 to setOf(Wire.A, Wire.B, Wire.D, Wire.E, Wire.F, Wire.G),
        7 to setOf(Wire.A, Wire.C, Wire.F),
        8 to setOf(Wire.A, Wire.B, Wire.C, Wire.D, Wire.E, Wire.F, Wire.G),
        9 to setOf(Wire.A, Wire.B, Wire.C, Wire.D, Wire.F, Wire.G),
    )

    fun solve1(): Int {
        val uniqueDigitSizes = setOf(1, 4, 7, 8).map { digitWires[it]!!.size }
        return input.values.flatMap { it.split(" ") }
            .count { it.length in uniqueDigitSizes }
    }

    fun solve2(): Int {
        return input.map { (pattern, encodedDigits) ->
            cipherFrom(pattern).let { cipher -> decode(cipher, encodedDigits) }
        }
            .sum()
    }

    private fun cipherFrom(pattern: String): Map<Int, Signal> {
        val wireToSegment = mutableMapOf<Wire, Wire>()
        val digitPatterns: List<Signal> = pattern.split(" ").map { it.toCharArray().map(Wire::find).toSortedSet() }

        val decipher = mutableMapOf<Int, Signal>()
        // uniquely sized digits
        decipher[1] = digitPatterns.first { it.size == digitWires[1]!!.size }
        decipher[7] = digitPatterns.first { it.size == digitWires[7]!!.size }
        decipher[4] = digitPatterns.first { it.size == digitWires[4]!!.size }
        decipher[8] = digitPatterns.first { it.size == digitWires[8]!!.size }
        //  wire A is the diff between 1 and 7 segments
        (decipher[7]!! - decipher[1]!!).let { wireToSegment[Wire.A] = it.first() }
        // intersection of 1 and 7 signals are wires C and F
        // wire C appears in all patterns 8 times and wire F appears in all patterns 9 times
        decipher[1]!!.intersect(decipher[7]!!).onEach { eitherC_Or_F ->
            val appears = digitPatterns.count { it.contains(eitherC_Or_F) }
            if (appears == 8) wireToSegment[Wire.C] = eitherC_Or_F
            if (appears == 9) wireToSegment[Wire.F] = eitherC_Or_F
        }

        // digits 0, 6 and 9 all have 6 signals, but only 6 doesn't have wire C
        digitPatterns.first { it.size == digitWires[6]!!.size && !it.contains(wireToSegment[Wire.C]!!) }
            .let { decipher[6] = it }
        // out of (2, 3, 5) only 5 doesn't have wire C
        digitPatterns.first { it.size == digitWires[5]!!.size && !it.contains(wireToSegment[Wire.C]!!) }
            .let { decipher[5] = it }
        // out of (2, 3, 5) only 5 does have wire B
        (decipher[5]!! -
                (digitPatterns.filter { it.size == digitWires[5]!!.size && it != decipher[5]!! }
                    .flatten()).toSortedSet())
            .first().let { wireToSegment[Wire.B] = it }

        // 4 consists of wires B, C, D, F
        (decipher[4]!! - wireToSegment[Wire.B] - wireToSegment[Wire.C] - wireToSegment[Wire.F])
            .first().let { wireToSegment[Wire.D] = it!! }
        // 0 and 9 differ by segment D
        (digitPatterns.filter { it.size == digitWires[0]!!.size && it != decipher[6]!! }).onEach { either0_Or9 ->
            if (either0_Or9.contains(wireToSegment[Wire.D])) decipher[9] = either0_Or9
            else decipher[0] = either0_Or9
        }
        // 2 and 3 differ by segment F (and E, but whatever)
        (digitPatterns.filter { it.size == digitWires[2]!!.size && it != decipher[5]!! }).onEach { either2_Or3 ->
            if (either2_Or3.contains(wireToSegment[Wire.F])) decipher[3] = either2_Or3
            else decipher[2] = either2_Or3
        }
        return decipher
    }

    private fun decode(cipher: Map<Int, Signal>, encoded: String): Int {
        val decipher = cipher.map { (k, v) -> k to v }.associate { (k, v) -> v to k }
        val digits: List<Signal> = encoded.split(" ").map { it.toCharArray().map(Wire::find).toSortedSet() }
        return digits.map { decipher[it] }.joinToString(separator = "").toInt()
    }

    constructor(testInput: String) : this() {
        testInput.lines().filter { it.isNotBlank() }
            .associate { it.split("|").let { it[0].trim() to it[1].trim() } }
            .let { input.putAll(it) }
    }

    enum class Wire {
        A, B, C, D, E, F, G;

        private val serial: Char = name.lowercase().first()

        companion object {
            fun find(c: Char) = values().first { it.serial == c }
        }
    }
}

private typealias Signal = SortedSet<Day08.Wire>

fun main() {
    listOf(
        { verifyResult(26, Day08(checkInput).solve1()) },
        { verifyResult(61229, Day08(checkInput).solve2()) },
        { println("Result is " + Day08(testInput).solve2()) }
    ).onEachIndexed { i, test -> measure(test, i) }
}

private const val checkInput: String = "" +
        "be cfbegad cbdgef fgaecd cgeb fdcge agebfd fecdb fabcd edb |" +
        "fdgacbe cefdb cefbgd gcbe\n" +
        "edbfga begcd cbg gc gcadebf fbgde acbgfd abcde gfcbed gfec |" +
        "fcgedb cgb dgebacf gc\n" +
        "fgaebd cg bdaec gdafb agbcfd gdcbef bgcad gfac gcb cdgabef |" +
        "cg cg fdcagb cbg\n" +
        "fbegcd cbd adcefb dageb afcb bc aefdc ecdab fgdeca fcdbega |" +
        "efabcd cedba gadfec cb\n" +
        "aecbfdg fbg gf bafeg dbefa fcge gcbea fcaegb dgceab fcbdga |" +
        "gecf egdcabf bgf bfgea\n" +
        "fgeab ca afcebg bdacfeg cfaedg gcfdb baec bfadeg bafgc acf |" +
        "gebdcfa ecba ca fadegcb\n" +
        "dbcfg fgd bdegcaf fgec aegbdf ecdfab fbedc dacgb gdcebf gf |" +
        "cefg dcbef fcge gbcadfe\n" +
        "bdfegc cbegaf gecbf dfcage bdacg ed bedf ced adcbefg gebcd |" +
        "ed bcgafe cdgba cbgef\n" +
        "egadfb cdbfeg cegd fecab cgb gbdefca cg fgcdab egfdb bfceg |" +
        "gbdfcae bgc cg cgb\n" +
        "gcafb gcf dcaebfg ecagb gf abcdeg gaef cafbge fdbac fegbdc |" +
        "fgae cfgab fg bagce\n"

private val testInput by lazy { readResourceFile("/advent2021/day08-task1.txt") }