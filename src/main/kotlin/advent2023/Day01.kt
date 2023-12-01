package advent2023

/*

 */
class Day01(val testInput: String) {
    fun solve(mode: Int = 1): Long =
        testInput.trim().lines()
            .map { extractDigits(it, mode) }
            .sumOf { (l, r) -> l * 10 + r }
            .toLong()

    private val digitNames = listOf("one", "two", "three", "four", "five", "six", "seven", "eight", "nine")
    private fun extractDigits(line: String, mode: Int): Pair<Int, Int> {
        val pattern = if (mode == 1) "\\d" else (digitNames.joinToString("|") { it } + "|\\d")
        val reversePattern = if (mode == 1) "\\d" else (digitNames.joinToString("|") { it }.reversed() + "|\\d")
        val first = Regex(pattern).find(line)!!
        val last = Regex(reversePattern).find(line.reversed())!!
        return listOf(first.value, last.value.reversed())
            .map {
                if (it[0].isDigit()) it[0] - '0'
                else digitNames.indexOf(it) + 1
            }
            .let { it[0] to it[1] }
    }
}