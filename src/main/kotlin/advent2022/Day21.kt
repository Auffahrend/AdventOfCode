package advent2022

/*

 */
class Day21(testInput: String) {

    private val monkeys: Map<String, Monkey>
    private val root: Monkey.ExpressionMonkey  

    private val numberMonkeyPattern = "(.{4}): (-?\\d+)".toRegex()
    private val expressionMonkeyPattern = "(.{4}): (.{4}) (.) (.{4})".toRegex()

    init {
        monkeys = testInput.lines().filterNot { it.isEmpty() }
            .map {
                numberMonkeyPattern.matchEntire(it)
                    ?.destructured?.let { (name, number) -> Monkey.NumberMonkey(name, number.toLong()) }
                    ?: expressionMonkeyPattern.matchEntire(it)!!
                        .destructured.let { (name, left, operation, right) ->
                            Monkey.ExpressionMonkey(name, left, right, operation.first())
                        }
            }.associateBy { it.name }
        root = monkeys["root"]!! as Monkey.ExpressionMonkey
    }

    private sealed class Monkey(open val name: String) {
        class NumberMonkey(name: String, val value: Long) : Monkey(name)
        class ExpressionMonkey(name: String, val left: String, val right: String, val operation: Char) : Monkey(name) {
            var expected: Long? = null
        }
    }
    
    private fun Monkey.evaluate(): Long {
        return when (this) {
            is Monkey.NumberMonkey -> value
            is Monkey.ExpressionMonkey -> when (this.operation) {
                '+' -> left().evaluate() + right().evaluate()
                '-' -> left().evaluate() - right().evaluate()
                '*' -> left().evaluate() * right().evaluate()
                '/' -> left().evaluate() / right().evaluate()
                else -> throw IllegalStateException("Unknown operation ${this.operation}")
            }
        }
    }

    fun solve(): Long = root.evaluate()
    
    private fun Monkey.expectedValue(): Long? {
        if (name == "humn") return null
        
        return when (this) {
            is Monkey.NumberMonkey -> value
            is Monkey.ExpressionMonkey -> {
                if (expected != null) return expected

                val leftExpected = left().expectedValue()
                val rightExpected = right().expectedValue()
                return if (leftExpected != null && rightExpected != null) {
                     when (operation) {
                        '+' -> leftExpected + rightExpected
                        '-' -> leftExpected - rightExpected
                        '*' -> leftExpected * rightExpected
                        '/' -> leftExpected / rightExpected
                        else -> throw IllegalStateException("Unknown operation ${this.operation}")
                    }.also { expected = it }
                } else null
            }
        }
    }

    private fun Monkey.ExpressionMonkey.left() = monkeys[left]!!
    private fun Monkey.ExpressionMonkey.right() = monkeys[right]!!

    private fun Monkey.solveFor(expectedResult: Long): Long {
        if (name == "humn") return expectedResult

        if (this is Monkey.NumberMonkey) throw IllegalStateException("Solving can be only applied to expression monkeys")
        this as Monkey.ExpressionMonkey
        expected = expectedResult
        val leftM = left()
        val rightM = right()
        val lExpected = leftM.expectedValue()
        val rExpected = rightM.expectedValue()

        if (lExpected == null && rExpected == null) throw IllegalStateException("Can't solve for 2 unknowns")
        if (lExpected == null) {
            return leftM.solveFor(
                when (operation) {
                    '+' -> expectedResult - rExpected!!
                    '-' -> expectedResult + rExpected!!
                    '*' -> expectedResult / rExpected!!
                    '/' -> expectedResult * rExpected!!
                    else -> throw IllegalStateException("Unknown operation $operation")
                }
            )
        } else {
            return rightM.solveFor(
                when (operation) {
                    '+' -> expectedResult - lExpected
                    '-' -> lExpected - expectedResult
                    '*' -> expectedResult / lExpected
                    '/' -> lExpected / expectedResult
                    else -> throw IllegalStateException("Unknown operation $operation")
                }
            )
        }
    }
    
    fun solve2(): Long {
        val lExpected = monkeys[root.left]!!.expectedValue()
        val rExpected = monkeys[root.right]!!.expectedValue()
        
        return if (lExpected == null) monkeys[root.left]!!.solveFor(rExpected!!)
        else monkeys[root.right]!!.solveFor(lExpected)
    }
}
