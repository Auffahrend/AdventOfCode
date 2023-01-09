package advent2020

import readResourceFile

/*
Their password database seems to be a little corrupted: some of the passwords wouldn't have been allowed by the Official Toboggan Corporate Policy that was in effect when they were chosen.

To try to debug the problem, they have created a list (your puzzle input) of passwords (according to the corrupted database) and the corporate policy when that password was set.

For example, suppose you have the following list:

1-3 a: abcde
1-3 b: cdefg
2-9 c: ccccccccc

Each line gives the password policy and then the password. The password policy indicates the lowest and highest number of times a given letter must appear for the password to be valid. For example, 1-3 a means that the password must contain a at least 1 time and at most 3 times.

In the above example, 2 passwords are valid. The middle password, cdefg, is not; it contains no instances of b, but needs at least 1. The first and third passwords are valid: they contain one a or nine c, both within the limits of their respective policies.

How many passwords are valid according to their policies?
 */

fun main() {
    parse(testInput)
        .count { it.isValid(interpretation = 2) }
        .let { println(it)}
}

private fun parse(input: String) : List<PasswordCheck> = input.lines().filter { it.isNotBlank()}
    .map { it.split(":") }
    .map { (rule, password) -> PasswordCheck(PasswordRule(rule), password) }

private class PasswordCheck(val rule: PasswordRule, val password: String) {
    fun isValid(interpretation: Int): Boolean = rule.covers(password, interpretation)
}

private class PasswordRule(val rule: String) {
    private val char: Char
    private val times: IntRange
    private val positions: Pair<Int, Int>
    init {
        rule.split(" ").also { (timesToken, charToken) ->
            if (charToken.length != 1) throw RuntimeException("Can't parse rule $rule - the checked string is not a single character")
            char = charToken[0]
            times = timesToken.split("-").let { (low, high) -> IntRange(low.toInt(), high.toInt()) }
            positions = times.first-1 to times.last-1
        }
    }

    fun covers(password: String, interpretation: Int): Boolean = when (interpretation) {
        1 -> password.toCharArray().count { it == char } in times
        2 -> password.toCharArray().let { chars -> (chars[positions.first] == char) xor (chars[positions.second] == char) }
        else -> throw RuntimeException("unknown password rule interpretation $interpretation")
    }
}

private val checkInput = "1-3 a: abcde\n" +
        "1-3 b: cdefg\n" +
        "2-9 c: ccccccccc"

private val testInput by lazy { readResourceFile("/advent2020/day02-task1.txt") }