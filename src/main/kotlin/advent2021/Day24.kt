package advent2021

import measure
import readResourceFile
import verifyResult
import java.util.*

/*

 */
private class Day24(
    val listing: String,
) {

    fun test(input: List<Int>): Memory {
        return ALU(listing, input)
            .let {
                it.eval()
                it.memory
            }
    }

    fun solve(): Long {
       return generateSequence(99_999_999_999_999) { it - 1 }
            .first { serial ->
                val digits = serial.toString().toCharArray().map { it.digitToInt() }
                if (digits.contains(0)) return@first false

                ALU(listing, digits).run {
                    eval()
                    memory[Register.z] == 0
                }
            }
    }

    class ALU(val code: List<Command>, input: List<Int>) {
        constructor(listing: String, input: List<Int>) : this(listing.lines().filterNot { it.isEmpty() }
            .map { Command.of(it) },
            input
        )

        val inputsQueue: MutableList<Int> = input.toMutableList()
        val memory: Memory = EnumMap(Register::class.java)

        private fun read(reg: Register): Int = memory[reg] ?: 0

        fun execute(c: Command) {
            when (c) {
                is Command.Inp -> memory[c.register] = inputsQueue.removeFirst()
                is Command.AddN -> memory[c.a] = read(c.a) + c.b
                is Command.AddR -> memory[c.a] = read(c.a) + read(c.b)
                is Command.DivN -> memory[c.a] = read(c.a) / c.b
                is Command.DivR -> memory[c.a] = read(c.a) / read(c.a)
                is Command.EqlN -> memory[c.a] = if (read(c.a) == c.b) 1 else 0
                is Command.EqlR -> memory[c.a] = if (read(c.a) == read(c.b)) 1 else 0
                is Command.ModN -> memory[c.a] = read(c.a) % c.b
                is Command.ModR -> memory[c.a] = read(c.a) % read(c.b)
                is Command.MulN -> memory[c.a] = read(c.a) * c.b
                is Command.MulR -> memory[c.a] = read(c.a) * read(c.b)
            }
        }

        fun eval() =
            code.onEach { execute(it) }
    }

    sealed class Command {
        companion object {
            fun of(line: String): Command {
                val parts = line.split(" ")
                return when (parts[0]) {
                    "inp" -> Inp(Register.valueOf(parts[1]))
                    "add" -> if (parts[2][0].isLetter()) AddR(Register.valueOf(parts[1]), Register.valueOf(parts[2]))
                    else AddN(Register.valueOf(parts[1]), parts[2].toInt())
                    "mul" -> if (parts[2][0].isLetter()) MulR(Register.valueOf(parts[1]), Register.valueOf(parts[2]))
                    else MulN(Register.valueOf(parts[1]), parts[2].toInt())
                    "div" -> if (parts[2][0].isLetter()) DivR(Register.valueOf(parts[1]), Register.valueOf(parts[2]))
                    else DivN(Register.valueOf(parts[1]), parts[2].toInt())
                    "mod" -> if (parts[2][0].isLetter()) ModR(Register.valueOf(parts[1]), Register.valueOf(parts[2]))
                    else ModN(Register.valueOf(parts[1]), parts[2].toInt())
                    "eql" -> if (parts[2][0].isLetter()) EqlR(Register.valueOf(parts[1]), Register.valueOf(parts[2]))
                    else EqlN(Register.valueOf(parts[1]), parts[2].toInt())
                    else -> throw RuntimeException("Can't parse $line")
                }
            }
        }

        data class Inp(val register: Register) : Command()
        data class AddR(val a: Register, val b: Register) : Command()
        data class AddN(val a: Register, val b: Int) : Command()

        data class MulR(val a: Register, val b: Register) : Command()
        data class MulN(val a: Register, val b: Int) : Command()

        data class DivR(val a: Register, val b: Register) : Command()
        data class DivN(val a: Register, val b: Int) : Command()

        data class ModR(val a: Register, val b: Register) : Command()
        data class ModN(val a: Register, val b: Int) : Command()

        data class EqlR(val a: Register, val b: Register) : Command()
        data class EqlN(val a: Register, val b: Int) : Command()
    }

    enum class Register {
        w, x, y, z
    }
}

private typealias Memory = EnumMap<Day24.Register, Int>

fun main() {
    listOf(
        { verifyResult(-5, Day24(checkInput1).test(listOf(5)).let { it[Day24.Register.x] }) },
        { verifyResult(5, Day24(checkInput1).test(listOf(-5)).let { it[Day24.Register.x] }) },

        { verifyResult(1, Day24(checkInput2).test(listOf(3, 9)).let { it[Day24.Register.z] }) },
        { verifyResult(0, Day24(checkInput2).test(listOf(3, 8)).let { it[Day24.Register.z] }) },

        { verifyResult(1, Day24(checkInput2).test(listOf(9)).let { it[Day24.Register.z] }) },
        { verifyResult(0, Day24(checkInput2).test(listOf(9)).let { it[Day24.Register.y] }) },
        { verifyResult(0, Day24(checkInput2).test(listOf(9)).let { it[Day24.Register.x] }) },
        { verifyResult(1, Day24(checkInput2).test(listOf(9)).let { it[Day24.Register.w] }) },

        { println("Result is " + Day24(testInput).solve()) }
    ).onEachIndexed { i, test -> measure(test, i) }
}


private const val checkInput1: String = "" +
        "inp x\n" +
        "mul x -1"
private const val checkInput2: String = "" +
        "inp z\n" +
        "inp x\n" +
        "mul z 3\n" +
        "eql z x\n"
private const val checkInput3: String = "" +
        "inp w\n" +
        "add z w\n" +
        "mod z 2\n" +
        "div w 2\n" +
        "add y w\n" +
        "mod y 2\n" +
        "div w 2\n" +
        "add x w\n" +
        "mod x 2\n" +
        "div w 2\n" +
        "mod w 2\n"

private val testInput by lazy { readResourceFile("/advent2021/day24-task1.txt") }