package advent2020

import measure
import readResourceFile
import verifyResult
import java.lang.NumberFormatException
import java.util.*
import java.util.regex.MatchResult
import java.util.regex.Pattern
import java.util.stream.Collectors.toList
import kotlin.collections.ArrayDeque

/*
--- Day 18: Operation Order ---

As you look out the window and notice a heavily-forested continent slowly appear over the horizon, you are interrupted by the child sitting next to you. They're curious if you could help them with their math homework.

Unfortunately, it seems like this "math" follows different rules than you remember.

The homework (your puzzle input) consists of a series of expressions that consist of addition (+), multiplication (*), and parentheses ((...)). Just like normal math, parentheses indicate that the expression inside must be evaluated before it can be used by the surrounding expression. Addition still finds the sum of the numbers on both sides of the operator, and multiplication still finds the product.

However, the rules of operator precedence have changed. Rather than evaluating multiplication before addition, the operators have the same precedence, and are evaluated left-to-right regardless of the order in which they appear.

For example, the steps to evaluate the expression 1 + 2 * 3 + 4 * 5 + 6 are as follows:

1 + 2 * 3 + 4 * 5 + 6
  3   * 3 + 4 * 5 + 6
      9   + 4 * 5 + 6
         13   * 5 + 6
             65   + 6
                 71

Parentheses can override this order; for example, here is what happens if parentheses are added to form 1 + (2 * 3) + (4 * (5 + 6)):

1 + (2 * 3) + (4 * (5 + 6))
1 +    6    + (4 * (5 + 6))
     7      + (4 * (5 + 6))
     7      + (4 *   11   )
     7      +     44
            51

Here are a few more examples:

    2 * 3 + (4 * 5) becomes 26.
    5 + (8 * 3 + 9 + 3 * 4 * 3) becomes 437.
    5 * 9 * (7 * 3 * 3 + 9 * 3 + (8 + 6 * 4)) becomes 12240.
    ((2 + 4 * 9) * (6 + 9 * 8 + 6) + 6) + 2 + 4 * 2 becomes 13632.

Before you can help with the homework, you need to understand it yourself. Evaluate the expression on each line of the homework; what is the sum of the resulting values?

 */
private class Day18 {
    val pattern = Pattern.compile("([\\d*+()] ?)")

    fun evaluate(input: String): Int = input.lines().filterNot { it.isBlank() }
        .map { line ->
            val matcher = pattern.matcher(line)
            val tokens = ArrayDeque<String>()
            while (matcher.find()) {
                tokens.add(matcher.group(1).trim())
            }
            val resultFromRPN = reversePolish(shuntingYard(tokens.toMutableList()))
            val result = nextNode(tokens).evaluate()
            if (resultFromRPN != result) throw RuntimeException("Result from RNP $resultFromRPN, result from AST $result")
            result
        }
        .sum()

    private fun reversePolish(tokens: List<String>): Int {
        val stack = ArrayDeque<Int>()
        tokens.onEach {
            when (it) {
                "+", "*" -> {
                    val right = stack.removeFirst()
                    val left = stack.removeFirst()
                    stack.addFirst(if (it == "+") left + right else left * right)
                }
                else -> stack.addFirst(it.toInt())
            }
        }
        return stack.removeFirst().also {
            if (stack.isNotEmpty()) throw RuntimeException("Stack is not empty after evaluation")
        }
    }

    private fun shuntingYard(tokens: List<String>): List<String> {
        val result = mutableListOf<String>()
        val stack = ArrayDeque<String>()

        tokens.onEach { token ->
            when (token) {
                "+", "*" -> {
                    while (stack.isNotEmpty() && (stack.first() == "+" || stack.first() == "*")) {
                        result.add(stack.removeFirst())
                    }
                    stack.addFirst(token)
                }
                "(" -> stack.addFirst(token)
                ")" -> {
                    while (stack.isNotEmpty() && stack.first() != "(") {
                        result.add(stack.removeFirst())
                    }
                    stack.removeFirst() // discarding "("
                }
                else -> result.add(token)
            }
        }
        while (stack.isNotEmpty()) result.add(stack.removeFirst())
        return result
    }

    private fun nextNode(tokens: ArrayDeque<String>, returnSingle: Boolean = false): CalculationNode<Int> {
        var root: CalculationNode<Int>? = null

        while (tokens.isNotEmpty()) {
            when (val token = tokens.removeFirst()) {
                "(" -> root = nextNode(tokens)
                ")" -> return root ?: throw IllegalArgumentException("Illegal $token bracket in $tokens")
                "+" -> root = CalculationNode.Operator(
                    root ?: throw IllegalArgumentException("Illegal '$token' operation (no left operand) in $tokens"),
                    nextNode(tokens, returnSingle = true), Int::plus
                )
                "*" -> root = CalculationNode.Operator(
                    root ?: throw IllegalArgumentException("Illegal '$token' operation (no left operand) in $tokens"),
                    nextNode(tokens, returnSingle = true), Int::times
                )
                else -> {
                    val node = try {
                        CalculationNode.Constant(token.toInt())
                    } catch (e: NumberFormatException) {
                        throw IllegalArgumentException("Can't parse token $token in $tokens")
                    }
                    if (root != null) {
                        throw IllegalArgumentException("Illegal '$token' constant (it should follow some operator) in $tokens")
                    }
                    root = node
                }
            }
            if (returnSingle) return root
        }
        return root ?: CalculationNode.Constant(0)
    }

    private sealed class CalculationNode<T : Number> {
        abstract fun evaluate(): T
        data class Constant<T : Number>(val value: T) : CalculationNode<T>() {
            override fun evaluate(): T = value
        }

        data class Operator<T : Number>(
            val left: CalculationNode<T>,
            val right: CalculationNode<T>,
            val action: (T, T) -> T
        ) : CalculationNode<T>() {
            override fun evaluate(): T = action(left.evaluate(), right.evaluate())
        }
    }
}

fun main() {
    listOf(
        { verifyResult(71, Day18().evaluate(checkInput1)) },
        { verifyResult(26, Day18().evaluate(checkInput2)) },
        { verifyResult(437, Day18().evaluate(checkInput3)) },
        { verifyResult(12240, Day18().evaluate(checkInput4)) },
        { verifyResult(13632, Day18().evaluate(checkInput5)) },
        { println("Result is " + Day18().evaluate(testInput)) }
    ).onEachIndexed { i, test -> measure(test, i) }
}

private const val checkInput1: String = "1 + 2 * 3 + 4 * 5 + 6"
private const val checkInput2: String = "2 * 3 + (4 * 5)"
private const val checkInput3: String = "5 + (8 * 3 + 9 + 3 * 4 * 3)"
private const val checkInput4: String = "5 * 9 * (7 * 3 * 3 + 9 * 3 + (8 + 6 * 4))"
private const val checkInput5: String = "((2 + 4 * 9) * (6 + 9 * 8 + 6) + 6) + 2 + 4 * 2"
//private const val checkInput6: String = "9 * 6 + 4 + 6 + (9 + 5 * 3 + 3 + (4 * 7 * 3 * 7 + 5)) + 4"

private val testInput by lazy { readResourceFile("/advent2020/day18-task1.txt") }