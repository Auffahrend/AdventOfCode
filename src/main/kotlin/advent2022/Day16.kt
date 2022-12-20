package advent2022

import advent2020.firstAndRest
import java.util.concurrent.ConcurrentHashMap

/*

 */

class Day16(testInput: String) {
    private data class Valve(val name: String, val flow: Int, val connections: Set<String>)

    private val valves: Map<String, Valve>

    // distances include time to open the target valve
    private val distances: Map<Valve, Map<Valve, Int>>
    private val parser = "Valve ([A-Z]+) has flow rate=(\\d+); tunnels? leads? to valves? (.*)".toRegex()

    init {
        valves = testInput.lines().filterNot { it.isEmpty() }
            .mapNotNull { parser.matchEntire(it) }
            .map { mr ->
                mr.destructured.let { (name, flow, connects) ->
                    Valve(name, flow.toInt(), connects.split(", ").toSet())
                }
            }.associateBy { it.name }

        distances = findDistances()
    }

    private fun findDistances(): Map<Valve, Map<Valve, Int>> =
        valves.values.map { start ->
            val queue = mutableListOf(start)
            // distances include time to open the target valve, so offset by 1
            val distancesFromStart = mutableMapOf(start to 1).withDefault { Int.MAX_VALUE }
            while (queue.isNotEmpty()) {
                val v = queue.removeFirst()
                val newDistance = distancesFromStart[v]!! + 1
                v.connections.forEach { c ->
                    if (distancesFromStart.getValue(valves[c]!!) > newDistance) {
                        distancesFromStart[valves[c]!!] = newDistance
                        queue.add(valves[c]!!)
                    }
                }
            }
            start to distancesFromStart
        }.associateBy({ it.first }, { it.second })

    fun solve(withElephant: Boolean = false): Int = findFromStart(if (withElephant) 26 else 30, withElephant)

    private fun findFromStart(timeLeft: Int, withElephant: Boolean): Int =
        findBestScore(State(
            listOf(Actor(timeLeft, valves["AA"]!!)) +
                    (if (withElephant) listOf(Actor(timeLeft, valves["AA"]!!)) else emptyList()),
            valves.values.filter { it.flow > 0 }.map { it.name }.toSet(),
        ), true)
            .also { println("Paths analyzed $totalPaths") }

    private var totalPaths = 0

    private data class Actor(val timeLeft: Int, val position: Valve)
    private data class State(val actors: List<Actor>, val closedValves: Set<String>)

    private val cache = ConcurrentHashMap<State, Int>()
    private var i = 0
    private fun findBestScore(state: State, isTopLevel: Boolean = false): Int {
        return cache.getOrPut(state) {
            val (current, others) = state.actors.firstAndRest()

            state.closedValves
                .parallelStream()
                .map { valves[it]!! }
                .filter { distances[current.position]!![it]!! < current.timeLeft }
                .mapToInt { next ->
                    totalPaths++
                    if (isTopLevel) println("Analyzing ${++i} out of ${state.closedValves.size} top level valves")
                    val timeToActivate = distances[current.position]!![next]!!
                    next.flow * (current.timeLeft - timeToActivate) +
                            findBestScore(
                                State(
                                    others + Actor(current.timeLeft - timeToActivate, next),
                                    state.closedValves - next.name
                                )
                            )
                }
                .max().orElse(0)
        }
    }
}