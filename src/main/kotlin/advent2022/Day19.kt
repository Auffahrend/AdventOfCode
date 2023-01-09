package advent2022

import log
import java.time.Duration
import java.time.Instant
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ForkJoinPool
import java.util.concurrent.ForkJoinTask
import java.util.concurrent.atomic.AtomicInteger

/*

 */
class Day19(testInput: String) {
    private val blueprintParser = "Blueprint (\\d+):\\s+Each ore robot costs (\\d+) ore.\\s+Each clay robot costs (\\d+) ore.\\s+Each obsidian robot costs (\\d+) ore and (\\d+) clay.\\s+Each geode robot costs (\\d+) ore and (\\d+) obsidian".toRegex()
    private val blueprints: List<Blueprint> = blueprintParser.findAll(testInput)
        .map { it.destructured }
        .map { (name, oreCostOre, clayCostOre, obsCostOre, obsCostClay, geodeCostOre, geodeCostObs) ->
            Blueprint(name.toInt(),
                oreRobotCost = Resources(ore = oreCostOre.toInt()),
                clayRobotCost = Resources(ore = clayCostOre.toInt()),
                obsidianRobotCost = Resources(ore = obsCostOre.toInt(), clay = obsCostClay.toInt()),
                geodeRobotCost  = Resources(ore = geodeCostOre.toInt(), obsidian = geodeCostObs.toInt()),
                ) }
        .toList()
    private data class Resources(val ore: Int = 0, val clay: Int = 0, val obsidian: Int = 0, val geodes: Int = 0) {

        operator fun plus(other: Resources) = Resources(
            ore + other.ore, clay + other.clay,
            obsidian + other.obsidian, geodes + other.geodes,
        )

        operator fun minus(other: Resources) = Resources(
            ore - other.ore, clay - other.clay,
            obsidian - other.obsidian, geodes - other.geodes,
        )

        infix fun enoughFor(cost: Resources): Boolean =
            ore >= cost.ore && clay >= cost.clay && obsidian >= cost.obsidian && geodes >= cost.geodes
    }


    private data class Blueprint(
        val id: Int,
        val oreRobotCost: Resources, val clayRobotCost: Resources,
        val obsidianRobotCost: Resources, val geodeRobotCost: Resources,
    ) {
        fun needMoreClayRobots(robots: Resources): Boolean =
            1.0 * obsidianRobotCost.clay / obsidianRobotCost.ore / (robots.clay / robots.ore) > 1.0
    }


    private data class State(
        val timeLeft: Int,
        val blueprint: Blueprint,
        val resources: Resources,
        val robots: Resources,
        var bestGeodeYield: AtomicInteger,
    ) {
        fun optimisticGeodeYield(): Int {
            // assume we can produce 1 additional goede robot per turn
            return resources.geodes + timeLeft * robots.geodes + timeLeft * (timeLeft - 1) / 2
        }
    }

    // caches maximum obsidian achievable from the state
    private val cache = ConcurrentHashMap<State, Int>()
    private val cacheHits = AtomicInteger(0)
    private val cacheMisses = AtomicInteger(0)


    private fun dfsForMaxGeodes(state: State): Int {

//        if (cache.contains(state)) {
//            cacheHits.incrementAndGet()
//            return cache[state]!!
//        }
//        cacheMisses.incrementAndGet()

        if (state.timeLeft == 0) return state.resources.geodes
//            .also { cache[state] = it }

        if (state.optimisticGeodeYield() < state.bestGeodeYield.get()) return 0

        val (timeLeft, blueprint, resources, robots) = state

        val options =
            listOf(
                blueprint.geodeRobotCost to Resources(geodes = 1),
                blueprint.obsidianRobotCost to Resources(obsidian = 1),
            ) +
                    (listOf(
                        blueprint.clayRobotCost to Resources(clay = 1),
                        blueprint.oreRobotCost to Resources(ore = 1),
                    ).let { if (blueprint.needMoreClayRobots(robots)) it else it.reversed() }) +
                    listOf(
                        Resources() to Resources(), // just wait
                    )

        val subRoutines = options
            .filter { (robotCost, _) -> resources enoughFor robotCost }
            .map { (robotCost, newRobot) ->
                ForkJoinTask.adapt<Int> {
                    dfsForMaxGeodes(
                        State(
                            timeLeft - 1,
                            blueprint,
                            resources - robotCost + robots,
                            robots + newRobot,
                            state.bestGeodeYield
                        )
                    )
                }
            }
        subRoutines.forEach { ForkJoinPool.commonPool().submit(it) }
        subRoutines.forEach {
            val yield = it.get()
                do {
                    val oldBest = state.bestGeodeYield.get()
                } while (yield > oldBest && !state.bestGeodeYield.compareAndSet(oldBest, yield))
            }
        return state.bestGeodeYield.get()
//            .also { cache[state] = it }
    }

    fun solve(time: Int = 24, size: Int = 0): Long {
        val results = blueprints.take(if (size == 0) blueprints.size else size)
//            .parallelStream()
            .map {
                javaClass.log("Considering Blueprint ${it.id}...")
                val started = Instant.now()
                val initialState = State(time, it, Resources(), Resources(ore = 1), AtomicInteger(0))
                val bestResult = dfsForMaxGeodes(initialState)
                    .also { res ->
                        javaClass.log(
                            "Best result for blueprint ${it.id} is $res geodes. Evaluated in ${
                                Duration.between(
                                    started,
                                    Instant.now()
                                )
                            }"
                        )
                    }
                it.id to bestResult.toLong()
            }

        return if (size == 0) results.fold(0L) { l, r -> l + r.first * r.second}
            else results.fold(1L) { l, r -> l * r.second}
    }
}