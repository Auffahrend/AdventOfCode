package advent2022

/*

 */
class Day20(testInput: String) {

    private val list: CircularLinkedList

    private data class Node(val value: Int) {
        fun detach() {
            previous.next = next
            next.previous = previous
            previous = this
            next = this
        }

        fun insertAfter(newPrevious: Node) {
            previous = newPrevious
            next = newPrevious.next
            previous.next = this
            next.previous = this
        }

        fun next(offset: Int): Node {
            var result = this
            repeat(offset) { result = result.next }
            return result
        }

        var previous: Node = this
        var next: Node = this

    }

    private class CircularLinkedList(val initialOrder: List<Node>) {
        val zero = initialOrder.first { it.value == 0 }
        fun mix(key: Long = 1L, times: Int = 1) {
            repeat(times) {
                initialOrder
                    .forEach { node ->
                        val newPrevious = findNewPrevious(node, key)
                        if (newPrevious != null) {
                            node.detach()
                            node.insertAfter(newPrevious)
                        }
                    }
            }
        }
        private fun findNewPrevious(node: Node, key: Long): Node? {
            var positiveOffset = node.value
            while (positiveOffset < 0) positiveOffset += initialOrder.size - 1
            positiveOffset %= (initialOrder.size - 1)
            positiveOffset = ((key * positiveOffset) % (initialOrder.size - 1)).toInt()

            return if (positiveOffset != 0) node.next(positiveOffset) else null
        }

        fun groveCoords(): List<Int> = listOf(zero.next(1000), zero.next(2000), zero.next(3000))
            .map { it.value }
    }

    init {
        val input = testInput.lines().filter { it.isNotEmpty() }
            .map { Node(it.toInt()) }
        // fixing links
        (input.windowed(2) +
                listOf(listOf(input.last(), input.first())))
            .forEach { (l, r) ->
                l.next = r
                r.previous = l
            }
        list = CircularLinkedList(input)
    }

    fun solve1(): Int {
        list.mix()
        return list.groveCoords().sum()
    }
    fun solve2(): Long {
        val key = 811589153L
        list.mix(key, 10)
        return list.groveCoords().sumOf { it * key }
    }
}