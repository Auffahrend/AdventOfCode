package advent2022

/*
    https://adventofcode.com/2022/day/13
 */
class Day13(testInput: String) {
    private sealed interface Node {
        fun compare(other: Node): Int {
            return when (this) {
                is IntNode -> when (other) {
                    is IntNode -> this.v.compareTo(other.v)
                    is ListNode -> ListNode(mutableListOf(this)).compare(other)
                }

                is ListNode -> when (other) {
                    is IntNode -> this.compare(ListNode(mutableListOf(other)))
                    is ListNode -> compareLists(this, other)
                }
            }
        }

        fun compareLists(first: ListNode, second: ListNode): Int {
            (0 until first.list.size.coerceAtMost(second.list.size)).map { i ->
                val res = first.list[i].compare(second.list[i])
                if (res != 0) return res
            }

            return first.list.size.compareTo(second.list.size)
        }
    }

    private val digitsReg = "\\d+".toRegex()
    private val pairs: List<Pair<Node, Node>>

    init {
        pairs = testInput.split("\n\n")
            .map { it.split("\n") }
            .map { (first, second) -> parse(first) to parse(second) }
    }


    private fun parse(line: String): Node {
        val compact = line.replace(" ", "")
        // [[1],[2,3,4]]
        // [1,[2,[3,[4,[5,6,0]]]],8,9]
        val stack = mutableListOf<ListNode>()
        var i = 0
        while (i < compact.length) {
            when {
                compact[i] == '[' -> {
                    stack.add(ListNode(mutableListOf()))
                    i++
                }

                compact[i] == ']' -> {
                    val el = stack.removeLast()
                    if (stack.isEmpty()) return el
                    else stack.last().list.add(el)
                    i++
                }

                compact[i].isDigit() -> {
                    val digits = digitsReg.find(compact, i)!!.range
                    stack.last().list.add(IntNode(compact.substring(digits).toInt()))
                    i = digits.last + 1
                }

                else -> i++
            }
        }
        throw IllegalArgumentException("Input $compact is malformed")
    }

    private data class IntNode(val v: Int) : Node

    private data class ListNode(val list: MutableList<Node>) : Node

    fun solve(): Int {
        return pairs
            .mapIndexed { i, (f, s) -> if (f.compare(s) < 0) i + 1 else 0 }
            .sum()
    }

    fun solve2(): Int {
        val added1 = parse("[[2]]")
        val added2 = parse("[[6]]")
        val added = listOf(added1, added2)
        return (pairs.flatMap { (f, s) -> listOf(f, s) } + added)
            .sortedWith(Comparator(Node::compare))
            .mapIndexed { i, it -> if (it in added) i+1 else 1 }
            .reduce(Int::times)
    }

}