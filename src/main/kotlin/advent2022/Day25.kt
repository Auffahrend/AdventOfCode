package advent2022

import kotlin.math.max
import kotlin.math.pow

/*

 */
class Day25(testInput: String) {

    private val numbers = testInput.lines().filterNot { it.isEmpty() }
        .map { SnafuNumber(it) }
    companion object {
        val snafuDigitToInt = mapOf(
            '=' to -2,
            '-' to -1,
            '0' to 0,
            '1' to 1,
            '2' to 2,
        )
        val intToSnafyDigit = snafuDigitToInt.entries.associate { (k, v) -> v to k }
        fun power5(exp: Int): Int = 5.0.pow(exp).toInt()
    }
    private data class SnafuNumber(val digits: List<Int>) {
        constructor(text: String): this(text.reversed().map { snafuDigitToInt[it]!! } )
        val asDecimal: Int by lazy { digits.mapIndexed { i, v -> v * power5(i)}.sum() }
        val asText: String by lazy { digits.map { intToSnafyDigit[it]!! }.reversed().joinToString(separator = "") }
        private fun digit(index: Int) = if (index in digits.indices) digits[index] else 0

        operator fun plus(other: SnafuNumber): SnafuNumber {
            val res = mutableListOf<Int>()
            var carryOver = 0
            (0 until  max(digits.size, other.digits.size)).forEach { i ->
                var sum = this.digit(i) + other.digit(i) + carryOver
                carryOver = 0
                if (sum < -2) {
                    sum +=5
                    carryOver = -1
                } else if (sum > 2) {
                    sum -= 5
                    carryOver = 1
                }
                res.add(sum)
            }
            if (carryOver != 0) res.add(carryOver)
            return SnafuNumber(res)
        }
    }
    fun solve(): String {
        return numbers.reduce(SnafuNumber::plus).asText
    }
}