package advent2020

import readResourceFile
import verifyResult

/*
--- Day 8: Handheld Halting ---

Your flight to the major airline hub reaches cruising altitude without incident. While you consider checking the in-flight menu for one of those drinks that come with a little umbrella, you are interrupted by the kid sitting next to you.

Their handheld game console won't turn on! They ask if you can take a look.

You narrow the problem down to a strange infinite loop in the boot code (your puzzle input) of the device. You should be able to fix it, but first you need to be able to run the code in isolation.

The boot code is represented as a text file with one instruction per line of text. Each instruction consists of an operation (acc, jmp, or nop) and an argument (a signed number like +4 or -20).

    acc increases or decreases a single global value called the accumulator by the value given in the argument. For example, acc +7 would increase the accumulator by 7. The accumulator starts at 0. After an acc instruction, the instruction immediately below it is executed next.
    jmp jumps to a new instruction relative to itself. The next instruction to execute is found using the argument as an offset from the jmp instruction; for example, jmp +2 would skip the next instruction, jmp +1 would continue to the instruction immediately below it, and jmp -20 would cause the instruction 20 lines above to be executed next.
    nop stands for No OPeration - it does nothing. The instruction immediately below it is executed next.

For example, consider the following program:

nop +0
acc +1
jmp +4
acc +3
jmp -3
acc -99
acc +1
jmp -4
acc +6

These instructions are visited in this order:

nop +0  | 1
acc +1  | 2, 8(!)
jmp +4  | 3
acc +3  | 6
jmp -3  | 7
acc -99 |
acc +1  | 4
jmp -4  | 5
acc +6  |

First, the nop +0 does nothing. Then, the accumulator is increased from 0 to 1 (acc +1) and jmp +4 sets the next instruction to the other acc +1 near the bottom. After it increases the accumulator from 1 to 2, jmp -4 executes, setting the next instruction to the only acc +3. It sets the accumulator to 5, and jmp -3 causes the program to continue back at the first acc +1.

This is an infinite loop: with this sequence of jumps, the program will run forever. The moment the program tries to run any instruction a second time, you know it will never terminate.

Immediately before the program would run an instruction a second time, the value in the accumulator is 5.

Run your copy of the boot code. Immediately before any instruction is executed a second time, what value is in the accumulator?


--- Part Two ---

After some careful analysis, you believe that exactly one instruction is corrupted.

Somewhere in the program, either a jmp is supposed to be a nop, or a nop is supposed to be a jmp. (No acc instructions were harmed in the corruption of this boot code.)

The program is supposed to terminate by attempting to execute an instruction immediately after the last instruction in the file. By changing exactly one jmp or nop, you can repair the boot code and make it terminate correctly.

For example, consider the same program from above:

nop +0
acc +1
jmp +4
acc +3
jmp -3
acc -99
acc +1
jmp -4
acc +6

If you change the first instruction from nop +0 to jmp +0, it would create a single-instruction infinite loop, never leaving that instruction. If you change almost any of the jmp instructions, the program will still eventually find another jmp instruction and loop forever.

However, if you change the second-to-last instruction (from jmp -4 to nop -4), the program terminates! The instructions are visited in this order:

nop +0  | 1
acc +1  | 2
jmp +4  | 3
acc +3  |
jmp -3  |
acc -99 |
acc +1  | 4
nop -4  | 5
acc +6  | 6

After the last instruction (acc +6), the program terminates by attempting to run the instruction below the last instruction in the file. With this change, after the program terminates, the accumulator contains the value 8 (acc +1, acc +1, acc +6).

Fix the program so that it terminates normally by changing exactly one jmp (to nop) or nop (to jmp). What is the value of the accumulator after the program terminates?

 */
private class Day08 {
    private val commands: List<Command>

    private constructor(commands: List<Command>) {
        this.commands = commands
    }

    private var acc = 0
    private var code = 0

    constructor(commands: String) : this(commands.lines()
        .filter { it.isNotBlank() }
        .map { Command(it) }
        .toList())

    fun getAccBeforeLoop(): Int? {
        resetInterpreter()
        while (code < commands.size && commands[code].timesExecuted == 0) {
            commands[code].timesExecuted++
            when (commands[code].operation) {
                Operation.NOP -> code++
                Operation.ACC -> {
                    acc += commands[code].value; code++
                }

                Operation.JMP -> code += commands[code].value
            }
        }

        return if (code < commands.size) acc
        else null
    }

    private fun resetInterpreter() {
        acc = 0
        code = 0
        commands.onEach { it.timesExecuted = 0 }
    }

    fun fixAnInstructionAndGetAcc(): Int {
        return commands
            .mapIndexed { i, c -> i to c.flipCommand() }
            .filter { (_, c) -> c != null }
            .map { (i, fc) -> commands.subList(0, i) + fc!! + commands.subList(i + 1, commands.size) }
            .map { Day08(it) }
            .first { it.getAccBeforeLoop() == null }.acc
    }

    private data class Command(val operation: Operation, val value: Int, var timesExecuted: Int = 0) {
        constructor(text: String) : this(
            text.split(" ")[0].let { Operation.valueOf(it.uppercase()) },
            text.split(" ")[1].toInt(),
        )

        fun flipCommand(): Command? = when (operation) {
            Operation.NOP -> Command(Operation.JMP, value)
            Operation.JMP -> Command(Operation.NOP, value)
            else -> null
        }
    }

    private enum class Operation { NOP, JMP, ACC }
}

fun main() {
    verifyResult(8, Day08(checkInput).fixAnInstructionAndGetAcc())
    println("Check succeeded")

    println(Day08(testInput).fixAnInstructionAndGetAcc())
}

private const val checkInput: String = "" +
        "nop +0\n" +
        "acc +1\n" +
        "jmp +4\n" +
        "acc +3\n" +
        "jmp -3\n" +
        "acc -99\n" +
        "acc +1\n" +
        "jmp -4\n" +
        "acc +6"

private val testInput by lazy { readResourceFile("/advent2020/day08-task1.txt") }