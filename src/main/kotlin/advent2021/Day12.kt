package advent2021

import measure
import readResourceFile
import verifyResult
import java.util.*

/*
--- Day 12: Passage Pathing ---

With your submarine's subterranean subsystems subsisting suboptimally, the only way you're getting out of this cave anytime soon is by finding a path yourself. Not just a path - the only way to know if you've found the best path is to find all of them.

Fortunately, the sensors are still mostly working, and so you build a rough map of the remaining caves (your puzzle input). For example:

start-A
start-b
A-c
A-b
b-d
A-end
b-end

This is a list of how all of the caves are connected. You start in the cave named start, and your destination is the cave named end. An entry like b-d means that cave b is connected to cave d - that is, you can move between them.

So, the above cave system looks roughly like this:

    start
    /   \
c--A-----b--d
    \   /
     end

Your goal is to find the number of distinct paths that start at start, end at end, and don't visit small caves more than once. There are two types of caves: big caves (written in uppercase, like A) and small caves (written in lowercase, like b). It would be a waste of time to visit any small cave more than once, but big caves are large enough that it might be worth visiting them multiple times. So, all paths you find should visit small caves at most once, and can visit big caves any number of times.

Given these rules, there are 10 paths through this example cave system:

start,A,b,A,c,A,end
start,A,b,A,end
start,A,b,end
start,A,c,A,b,A,end
start,A,c,A,b,end
start,A,c,A,end
start,A,end
start,b,A,c,A,end
start,b,A,end
start,b,end

(Each line in the above list corresponds to a single path; the caves visited by that path are listed in the order they are visited and separated by commas.)

Note that in this cave system, cave d is never visited by any path: to do so, cave b would need to be visited twice (once on the way to cave d and a second time when returning from cave d), and since cave b is small, this is not allowed.

Here is a slightly larger example:

dc-end
HN-start
start-kj
dc-start
dc-HN
LN-dc
HN-end
kj-sa
kj-HN
kj-dc

The 19 paths through it are as follows:

start,HN,dc,HN,end
start,HN,dc,HN,kj,HN,end
start,HN,dc,end
start,HN,dc,kj,HN,end
start,HN,end
start,HN,kj,HN,dc,HN,end
start,HN,kj,HN,dc,end
start,HN,kj,HN,end
start,HN,kj,dc,HN,end
start,HN,kj,dc,end
start,dc,HN,end
start,dc,HN,kj,HN,end
start,dc,end
start,dc,kj,HN,end
start,kj,HN,dc,HN,end
start,kj,HN,dc,end
start,kj,HN,end
start,kj,dc,HN,end
start,kj,dc,end

Finally, this even larger example has 226 paths through it:

fs-end
he-DX
fs-he
start-DX
pj-DX
end-zg
zg-sl
zg-pj
pj-he
RW-he
fs-DX
pj-RW
zg-RW
start-pj
he-WI
zg-he
pj-fs
start-RW

How many paths through this cave system are there that visit small caves at most once?

Your puzzle answer was 5333.
--- Part Two ---

After reviewing the available paths, you realize you might have time to visit a single small cave twice. Specifically, big caves can be visited any number of times, a single small cave can be visited at most twice, and the remaining small caves can be visited at most once. However, the caves named start and end can only be visited exactly once each: once you leave the start cave, you may not return to it, and once you reach the end cave, the path must end immediately.

Now, the 36 possible paths through the first example above are:

start,A,b,A,b,A,c,A,end
start,A,b,A,b,A,end
start,A,b,A,b,end
start,A,b,A,c,A,b,A,end
start,A,b,A,c,A,b,end
start,A,b,A,c,A,c,A,end
start,A,b,A,c,A,end
start,A,b,A,end
start,A,b,d,b,A,c,A,end
start,A,b,d,b,A,end
start,A,b,d,b,end
start,A,b,end
start,A,c,A,b,A,b,A,end
start,A,c,A,b,A,b,end
start,A,c,A,b,A,c,A,end
start,A,c,A,b,A,end
start,A,c,A,b,d,b,A,end
start,A,c,A,b,d,b,end
start,A,c,A,b,end
start,A,c,A,c,A,b,A,end
start,A,c,A,c,A,b,end
start,A,c,A,c,A,end
start,A,c,A,end
start,A,end
start,b,A,b,A,c,A,end
start,b,A,b,A,end
start,b,A,b,end
start,b,A,c,A,b,A,end
start,b,A,c,A,b,end
start,b,A,c,A,c,A,end
start,b,A,c,A,end
start,b,A,end
start,b,d,b,A,c,A,end
start,b,d,b,A,end
start,b,d,b,end
start,b,end

The slightly larger example above now has 103 paths through it, and the even larger example now has 3509 paths through it.

Given these new rules, how many paths through this cave system are there?

 */
private class Day12(
) {
    companion object {
        const val start = "start"
        const val end = "end"
    }

    private val allCaves: MutableMap<String, Cave> = mutableMapOf()

    private fun addCaveConnection(from: String, to: String) {
        allCaves.computeIfAbsent(from) { Cave(it) }
            .connectTo(allCaves.computeIfAbsent(to) { Cave(it) })
    }

    fun solve(): Long {
        val completePaths = mutableListOf<Path>()
        val incompletePaths = LinkedList(listOf(Path().add(allCaves[start]!!)))
        while (incompletePaths.isNotEmpty()) {
            val path = incompletePaths.removeFirst()
            val newPaths = path.lastCave().connectsTo
                .filter { path.canAdd(it) }
                .map { path.add(it) }
            newPaths.filter { it.isComplete() }.onEach { completePaths.add(it) }
            newPaths.filterNot { it.isComplete() }.onEach { incompletePaths.add(it) }
        }
        return completePaths.size.toLong()
    }

    constructor(testInput: String) : this() {
        testInput.lines().filterNot { it.isBlank() }
            .map { it.split("-") }
            .onEach { addCaveConnection(it[0], it[1]) }
    }

    private data class Cave(val id: String) {
        val connectsTo: MutableSet<Cave> = mutableSetOf()
        val isBig = id.first().isUpperCase()
        fun connectTo(other: Cave) {
            connectsTo.add(other)
            other.connectsTo.add(this)
        }
    }

    private class Path {
        val steps: MutableList<Cave> = mutableListOf()
        val visitedSmall: MutableMap<Int, Set<Cave>> = mutableMapOf()
        fun lastCave() = steps.last()
        fun canAdd(cave: Cave) = cave.isBig
                || notVisited(cave)
                || canVisitSecondTime(cave)

        private fun notVisited(cave: Cave) = !visitedSmall.values.any { it.contains(cave) }

        private fun canVisitSecondTime(cave: Cave): Boolean = visitedSmall[2].let { it == null || it.isEmpty() }
                && cave.id != start && cave.id != end

        fun add(cave: Cave): Path = Path().also {
            it.steps.addAll(steps + cave)
            if (!cave.isBig) {
                if ((visitedSmall[1] ?: emptySet()).contains(cave)) {
                    it.visitedSmall[1] = visitedSmall[1]!! - cave
                    it.visitedSmall[2] = setOf(cave)
                } else {
                    it.visitedSmall.putAll(visitedSmall)
                    it.visitedSmall.merge(1, setOf(cave), Set<Cave>::plus)
                }
            } else {
                it.visitedSmall.putAll(visitedSmall)
            }
        }

        fun isComplete() = lastCave().id == end
        override fun toString(): String = steps.joinToString { it.id }
    }
}

fun main() {
    listOf(
        { verifyResult(36, Day12(checkInput1).solve()) },
        { verifyResult(103, Day12(checkInput2).solve()) },
        { verifyResult(3509, Day12(checkInput3).solve()) },
        { println("Result is " + Day12(testInput).solve()) }
    ).onEachIndexed { i, test -> measure(test, i) }
}

private const val checkInput1: String = "" +
        "start-A\n" +
        "start-b\n" +
        "A-c\n" +
        "A-b\n" +
        "b-d\n" +
        "A-end\n" +
        "b-end\n"

private const val checkInput2: String = "" +
        "dc-end\n" +
        "HN-start\n" +
        "start-kj\n" +
        "dc-start\n" +
        "dc-HN\n" +
        "LN-dc\n" +
        "HN-end\n" +
        "kj-sa\n" +
        "kj-HN\n" +
        "kj-dc\n"

private const val checkInput3: String = "" +
        "fs-end\n" +
        "he-DX\n" +
        "fs-he\n" +
        "start-DX\n" +
        "pj-DX\n" +
        "end-zg\n" +
        "zg-sl\n" +
        "zg-pj\n" +
        "pj-he\n" +
        "RW-he\n" +
        "fs-DX\n" +
        "pj-RW\n" +
        "zg-RW\n" +
        "start-pj\n" +
        "he-WI\n" +
        "zg-he\n" +
        "pj-fs\n" +
        "start-RW\n"

private val testInput by lazy { readResourceFile("/advent2021/day12-task1.txt") }