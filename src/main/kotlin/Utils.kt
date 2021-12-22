import kotlin.time.ExperimentalTime
import kotlin.time.measureTime

fun readResourceFile(path: String) =
    Utils::class.java.getResource(if (path.startsWith("/")) path else "/$path")!!.readText()

fun <T> verifyResult(expected: T, actual: T) {
    if (expected != actual) {
        throw RuntimeException("Actual result $actual differs from expected $expected")
    }
}

@OptIn(ExperimentalTime::class)
fun measure(test: () -> Unit, i: Int) {
    val duration = measureTime(test)
    println("Test $i succeeded in $duration")
}

fun <T> List<List<T>>.deepMutableCopy(): MutableList<MutableList<T>> = this.map { it.toMutableList() }.toMutableList()
fun <T> List<List<T>>.neighbours4(point: Coords): List<Coords> = point.let { (x, y) ->
    listOf(-1, 0, 1).flatMap { dx -> listOf(-1, 0, 1).map { dy -> dx to dy } }
        .filter { (dx, dy) -> (dx * dy == 0) && (dx != 0 || dy != 0) }
        .map { (dx, dy) -> (x + dx) to (y + dy) }
        .filter { (x, y) -> y in this.indices && x in this.first().indices }
}

fun <T> List<List<T>>.neighbours8(point: Coords): List<Coords> = point.let { (x, y) ->
    listOf(-1, 0, 1).flatMap { dx -> listOf(-1, 0, 1).map { dy -> dx to dy } }
        .filter { (dx, dy) -> dx != 0 || dy != 0 }
        .map { (dx, dy) -> (x + dx) to (y + dy) }
        .filter { (x, y) -> y in this.indices && x in this.first().indices }
}

fun Collection<Coords>.neighbours8(point: Coords): List<Coords> = point.let { (x, y) ->
    listOf(-1, 0, 1).flatMap { dy -> listOf(-1, 0, 1).map { dx -> dx to dy } }
        .filter { (dx, dy) -> dx != 0 || dy != 0 }
            .map { (dx, dy) -> (x + dx) to (y + dy) }
}

fun Collection<Coords>.neighbours9(point: Coords): List<Coords> = point.let { (x, y) ->
    listOf(-1, 0, 1).flatMap { dy -> listOf(-1, 0, 1).map { dx -> dx to dy } }
        .map { (dx, dy) -> (x + dx) to (y + dy) }
}


typealias Coords = Pair<Int, Int>
data class Coords3(val x: Int, val y: Int, val z: Int)

private class Utils