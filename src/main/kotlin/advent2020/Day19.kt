package advent2020

import measure
import readResourceFile
import verifyResult

/*
--- Day 19: Monster Messages ---

You land in an airport surrounded by dense forest. As you walk to your high-speed train, the Elves at the Mythical Information Bureau contact you again. They think their satellite has collected an image of a sea monster! Unfortunately, the connection to the satellite is having problems, and many of the messages sent back from the satellite have been corrupted.

They sent you a list of the rules valid messages should obey and a list of received messages they've collected so far (your puzzle input).

The rules for valid messages (the top part of your puzzle input) are numbered and build upon each other. For example:

0: 1 2
1: "a"
2: 1 3 | 3 1
3: "b"

Some rules, like 3: "b", simply match a single character (in this case, b).

The remaining rules list the sub-rules that must be followed; for example, the rule 0: 1 2 means that to match rule 0, the text being checked must match rule 1, and the text after the part that matched rule 1 must then match rule 2.

Some of the rules have multiple lists of sub-rules separated by a pipe (|). This means that at least one list of sub-rules must match. (The ones that match might be different each time the rule is encountered.) For example, the rule 2: 1 3 | 3 1 means that to match rule 2, the text being checked must match rule 1 followed by rule 3 or it must match rule 3 followed by rule 1.

Fortunately, there are no loops in the rules, so the list of possible matches will be finite. Since rule 1 matches a and rule 3 matches b, rule 2 matches either ab or ba. Therefore, rule 0 matches aab or aba.

Here's a more interesting example:

0: 4 1 5
1: 2 3 | 3 2
2: 4 4 | 5 5
3: 4 5 | 5 4
4: "a"
5: "b"

Here, because rule 4 matches a and rule 5 matches b, rule 2 matches two letters that are the same (aa or bb), and rule 3 matches two letters that are different (ab or ba).

Since rule 1 matches rules 2 and 3 once each in either order, it must match two pairs of letters, one pair with matching letters and one pair with different letters. This leaves eight possibilities: aaab, aaba, bbab, bbba, abaa, abbb, baaa, or babb.

Rule 0, therefore, matches a (rule 4), then any of the eight options from rule 1, then b (rule 5): aaaabb, aaabab, abbabb, abbbab, aabaab, aabbbb, abaaab, or ababbb.

The received messages (the bottom part of your puzzle input) need to be checked against the rules so you can determine which are valid and which are corrupted. Including the rules and the messages together, this might look like:

0: 4 1 5
1: 2 3 | 3 2
2: 4 4 | 5 5
3: 4 5 | 5 4
4: "a"
5: "b"

ababbb
bababa
abbbab
aaabbb
aaaabbb

Your goal is to determine the number of messages that completely match rule 0. In the above example, ababbb and abbbab match, but bababa, aaabbb, and aaaabbb do not, producing the answer 2. The whole message must match all of rule 0; there can't be extra unmatched characters in the message. (For example, aaaabbb might appear to match rule 0 above, but it has an extra unmatched b on the end.)

How many messages completely match rule 0?

 */
private class Day19(
) {
    private val parsedRules = mutableMapOf<Int, Rule>()
    private lateinit var examples: List<String>

    fun solve(): Int = examples
        .count { parsedRules[0]?.match(it) ?: false }

    constructor(testInput: String) : this() {
        val rules = testInput.lines().filterNot { it.isEmpty() }
            .filter { it.matches(Regex("\\d+:.+")) }
        examples = testInput.lines().filterNot { it.isEmpty() }
            .filterNot { it.matches(Regex("\\d+:.+")) }

        val orderedRules = rules.associate {
            it.split(":").let { (i, d) -> i.toInt() to d.trim() }
        }

        fun parseRule(name: String, description: String): Rule {
            return if (description.contains('|')) {
                Rule.OneOf(name, description.split("|").mapIndexed { i, it -> parseRule("$name.$i", it.trim()) })
            } else {
                val res = Rule.AllOf(name, description.split(" ")
                    .mapIndexed { i, part ->
                        if (part.matches(Regex("\\d+"))) {
                            val ruleIndex = part.toInt()
                            if (!parsedRules.containsKey(ruleIndex)) {
                                parsedRules[ruleIndex] = parseRule("$name.$i", orderedRules[ruleIndex]!!)
                            }
                            parsedRules[ruleIndex]!!
                        }
                        else Rule.Chars("$name.$i", part.filterNot { it == '"' })
                    }
                )
                if (res.rules.size == 1) res.rules.first() else res
            }
        }

        orderedRules.keys.sortedDescending()
            .filterNot { parsedRules.containsKey(it) }
            .map { it to orderedRules[it]!! }
            .map { (i, descr) -> parsedRules[i] = parseRule(i.toString(), descr) }

    }

    private sealed class Rule(open val name: String) {
        abstract val length: Int
        abstract fun match(s: String): Boolean

        data class Chars(override val name: String, val pattern: String) : Rule(name) {
            override fun match(s: String) = pattern == s
            override val length: Int = pattern.length
        }

        data class AllOf(override val name: String, val rules: List<Rule>) : Rule(name) {
            override fun match(s: String): Boolean = (s.length == length) &&
                    rules.firstAndRest().let { (r, rest) ->
                        r.match(s.substring(0 until r.length))
                                && (rest.isEmpty() || AllOf("partial from $name", rest).match(s.substring(r.length)))
                    }

            override val length: Int = rules.sumOf { it.length }
        }

        data class OneOf(override val name: String, val rules: List<Rule>) : Rule(name) {
            override fun match(s: String): Boolean =
                rules.any { it.match(s) }

            override val length: Int = rules.map { it.length }.distinct().let {
                if (it.size == 1) it.first()
                else throw RuntimeException("Rules of various lengths $it found for $this")
            }
        }
    }
}

fun <T> List<T>.firstAndRest(): Pair<T, List<T>> = first() to drop(1)

fun main() {
    listOf(
        { verifyResult(2, Day19(checkInput).solve()) },
        { println("Result is " + Day19(testInput).solve()) }
    ).onEachIndexed { i, test -> measure(test, i) }
}

private const val checkInput: String = "" +
        "0: 4 1 5\n" +
        "1: 2 3 | 3 2\n" +
        "2: 4 4 | 5 5\n" +
        "3: 4 5 | 5 4\n" +
        "4: \"a\"\n" +
        "5: \"b\"\n" +
        "\n" +
        "ababbb\n" +
        "bababa\n" +
        "abbbab\n" +
        "aaabbb\n" +
        "aaaabbb"

private val testInput by lazy { readResourceFile("/advent2020/day19-task1.txt") }