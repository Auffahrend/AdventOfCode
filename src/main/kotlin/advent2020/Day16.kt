package advent2020

import measure
import readResourceFile
import verifyResult
import kotlin.time.ExperimentalTime

/*
--- Day 16: Ticket Translation ---

As you're walking to yet another connecting flight, you realize that one of the legs of your re-routed trip coming up is on a high-speed train. However, the train ticket you were given is in a language you don't understand. You should probably figure out what it says before you get to the train station after the next flight.

Unfortunately, you can't actually read the words on the ticket. You can, however, read the numbers, and so you figure out the fields these tickets must have and the valid ranges for values in those fields.

You collect the rules for ticket fields, the numbers on your ticket, and the numbers on other nearby tickets for the same train service (via the airport security cameras) together into a single document you can reference (your puzzle input).

The rules for ticket fields specify a list of fields that exist somewhere on the ticket and the valid ranges of values for each field. For example, a rule like class: 1-3 or 5-7 means that one of the fields in every ticket is named class and can be any value in the ranges 1-3 or 5-7 (inclusive, such that 3 and 5 are both valid in this field, but 4 is not).

Each ticket is represented by a single line of comma-separated values. The values are the numbers on the ticket in the order they appear; every ticket has the same format. For example, consider this ticket:

.--------------------------------------------------------.
| ????: 101    ?????: 102   ??????????: 103     ???: 104 |
|                                                        |
| ??: 301  ??: 302             ???????: 303      ??????? |
| ??: 401  ??: 402           ???? ????: 403    ????????? |
'--------------------------------------------------------'

Here, ? represents text in a language you don't understand. This ticket might be represented as 101,102,103,104,301,302,303,401,402,403; of course, the actual train tickets you're looking at are much more complicated. In any case, you've extracted just the numbers in such a way that the first number is always the same specific field, the second number is always a different specific field, and so on - you just don't know what each position actually means!

Start by determining which tickets are completely invalid; these are tickets that contain values which aren't valid for any field. Ignore your ticket for now.

For example, suppose you have the following notes:

class: 1-3 or 5-7
row: 6-11 or 33-44
seat: 13-40 or 45-50

your ticket:
7,1,14

nearby tickets:
7,3,47
40,4,50
55,2,20
38,6,12

It doesn't matter which position corresponds to which field; you can identify invalid nearby tickets by considering only whether tickets contain values that are not valid for any field. In this example, the values on the first nearby ticket are all valid for at least one field. This is not true of the other three nearby tickets: the values 4, 55, and 12 are are not valid for any field. Adding together all of the invalid values produces your ticket scanning error rate: 4 + 55 + 12 = 71.

Consider the validity of the nearby tickets you scanned. What is your ticket scanning error rate?

--- Part Two ---

Now that you've identified which tickets contain invalid values, discard those tickets entirely. Use the remaining valid tickets to determine which field is which.

Using the valid ranges for each field, determine what order the fields appear on the tickets. The order is consistent between all tickets: if seat is the third field, it is the third field on every ticket, including your ticket.

For example, suppose you have the following notes:

class: 0-1 or 4-19
row: 0-5 or 8-19
seat: 0-13 or 16-19

your ticket:
11,12,13

nearby tickets:
3,9,18
15,1,5
5,14,9

Based on the nearby tickets in the above example, the first position must be row, the second position must be class, and the third position must be seat; you can conclude that in your ticket, class is 12, row is 11, and seat is 13.

Once you work out which field is which, look for the six fields on your ticket that start with the word departure. What do you get if you multiply those six values together?

 */
private class Day16(
    private val rules: List<FieldRule>,
    private val myTicket: Ticket,
    private val otherTickets: List<Ticket>,
) {
    lateinit var fieldsWithRules: List<FieldRule>

    fun scanErrorRate(): Int = otherTickets.flatMap { it.invalidFields(rules) }.sum()
    fun allocateFields() {
        val validTickets = otherTickets.filter { it.invalidFields(rules).isEmpty() }
        val fieldCandidates = myTicket.fields.map { rules.toMutableList() }.toTypedArray()

        validTickets.onEach { ticket ->
            ticket.fields.onEachIndexed { i, value ->
                fieldCandidates[i].removeAll { !it.valueSatisfiesTheRule(value) }
            }
        }

        do {
            val ruleEliminated = fieldCandidates.filter { it.size == 1 }.map { it.first() }
                .flatMap { singltoneCandidate ->
                    fieldCandidates.filter { it.size > 1 }
                        .map { it.remove(singltoneCandidate) }
                }.fold(false, Boolean::or)
        } while (ruleEliminated)

        fieldCandidates.onEachIndexed { i, it ->
            if (it.size != 1) throw RuntimeException("There are several candidates left for field $i: $it")
        }
        fieldsWithRules = fieldCandidates.map { it.first() }
    }

    fun getMyFields(prefix: String): List<Int> =
        fieldsWithRules.flatMapIndexed { i, rule ->
            if (rule.name.startsWith(prefix)) setOf(i) else emptySet()
        }.map { i -> myTicket.fields[i] }

    constructor(testInput: String) : this(
        testInput.lines().takeWhile { it.isNotBlank() }.map { FieldRule.of(it) },
        testInput.lines().indexOfFirst { it == "your ticket:" }
            .let { i -> testInput.lines()[i + 1] }.let { Ticket(it) },
        testInput.lines().indexOfFirst { it == "nearby tickets:" }
            .let { i -> testInput.lines().drop(i + 1).filter { it.isNotBlank() } }.map { Ticket(it) }
    )

    data class Ticket(val fields: List<Int>) {
        constructor(input: String) : this(
            input.split(",").map { it.toInt() }
        )

        fun invalidFields(rules: List<FieldRule>): List<Int> =
            fields.filterNot { value -> rules.any { it.valueSatisfiesTheRule(value) } }
    }

    data class FieldRule(
        val name: String,
        val allowedRanges: List<IntRange>
    ) {
        fun valueSatisfiesTheRule(value: Int) = allowedRanges.any { range -> value in range }

        companion object {
            fun of(input: String) = input.split(": ")
                .let { (name, ranges) ->
                    FieldRule(
                        name, ranges.split(" or ")
                            .map { range ->
                                range.split("-")
                                    .let { (l, r) -> l.toInt()..r.toInt() }
                            })
                }
        }

    }
}

fun main() {
    listOf(
        { verifyResult(71, Day16(checkInput).scanErrorRate()) },
        {
            println("Result is " + Day16(testInput).run {
                allocateFields()
                getMyFields("departure").fold(1L, Long::times)
            })
        }
    ).onEachIndexed { i, test -> measure(test, i) }
}

private const val checkInput: String = "" +
        "class: 1-3 or 5-7\n" +
        "row: 6-11 or 33-44\n" +
        "seat: 13-40 or 45-50\n" +
        "\n" +
        "your ticket:\n" +
        "7,1,14\n" +
        "\n" +
        "nearby tickets:\n" +
        "7,3,47\n" +
        "40,4,50\n" +
        "55,2,20\n" +
        "38,6,12"

private val testInput by lazy { readResourceFile("/advent2020/day16-task1.txt") }