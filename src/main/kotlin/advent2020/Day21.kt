package advent2020

import measure
import readResourceFile
import verifyResult

/*
--- Day 21: Allergen Assessment ---

You reach the train's last stop and the closest you can get to your vacation island without getting wet. There aren't even any boats here, but nothing can stop you now: you build a raft. You just need a few days' worth of food for your journey.

You don't speak the local language, so you can't read any ingredients lists. However, sometimes, allergens are listed in a language you do understand. You should be able to use this information to determine which ingredient contains which allergen and work out which foods are safe to take with you on your trip.

You start by compiling a list of foods (your puzzle input), one food per line. Each line includes that food's ingredients list followed by some or all of the allergens the food contains.

Each allergen is found in exactly one ingredient. Each ingredient contains zero or one allergen. Allergens aren't always marked; when they're listed (as in (contains nuts, shellfish) after an ingredients list), the ingredient that contains each listed allergen will be somewhere in the corresponding ingredients list. However, even if an allergen isn't listed, the ingredient that contains that allergen could still be present: maybe they forgot to label it, or maybe it was labeled in a language you don't know.

For example, consider the following list of foods:

mxmxvkd kfcds sqjhc nhms (contains dairy, fish)
trh fvjkl sbzzf mxmxvkd (contains dairy)
sqjhc fvjkl (contains soy)
sqjhc mxmxvkd sbzzf (contains fish)

The first food in the list has four ingredients (written in a language you don't understand): mxmxvkd, kfcds, sqjhc, and nhms. While the food might contain other allergens, a few allergens the food definitely contains are listed afterward: dairy and fish.

The first step is to determine which ingredients can't possibly contain any of the allergens in any food in your list. In the above example, none of the ingredients kfcds, nhms, sbzzf, or trh can contain an allergen. Counting the number of times any of these ingredients appear in any ingredients list produces 5: they all appear once each except sbzzf, which appears twice.

Determine which ingredients cannot possibly contain any of the allergens in your list. How many times do any of those ingredients appear?

--- Part Two ---

Now that you've isolated the inert ingredients, you should have enough information to figure out which ingredient contains which allergen.

In the above example:

    mxmxvkd contains dairy.
    sqjhc contains fish.
    fvjkl contains soy.

Arrange the ingredients alphabetically by their allergen and separate them by commas to produce your canonical dangerous ingredient list. (There should not be any spaces in your canonical dangerous ingredient list.) In the above example, this would be mxmxvkd,sqjhc,fvjkl.

Time to stock your raft with supplies. What is your canonical dangerous ingredient list?

 */
private class Day21(
) {
    private lateinit var foods: List<Food>
    private val foodByAllergen = mutableMapOf<Allergen, MutableSet<Food>>()
    private val foodByIngredient = mutableMapOf<Ingredient, MutableSet<Food>>()
    private val allergenToIngredient = mutableMapOf<Allergen, Ingredient>()
    private val ingredientToAllergen = mutableMapOf<Ingredient, Allergen>()

    fun solve(): Int {
        resolveAllergensToIngredients()
        return foods.flatMap { it.ingredients }.filterNot { ingredientToAllergen.containsKey(it) }.count()
    }

    fun solve2(): String {
        if (allergenToIngredient.isEmpty()) resolveAllergensToIngredients()
        return allergenToIngredient.toSortedMap().values.joinToString(separator = ",") { it }
    }

    private fun resolveAllergensToIngredients() {
        val allergenCandidates: MutableMap<Allergen, MutableSet<Ingredient>> =
            foodByAllergen.mapValues { (_, fs) -> intersectIngredients(fs).toMutableSet() }.toMutableMap()

        var updated = true
        while (allergenCandidates.isNotEmpty() && updated) {
            updated = false
            allergenCandidates.filterValues { ings -> ings.size == 1 }
                .onEach { (a, i) ->
                    val ingredient = i.first()
                    allergenToIngredient[a] = ingredient
                    ingredientToAllergen[ingredient] = a
                    allergenCandidates.remove(a)
                    allergenCandidates.onEach { (_, candidates) -> candidates.remove(ingredient) }
                    updated = true
                }
        }

        if (allergenCandidates.isNotEmpty()) throw RuntimeException("Could not resolve all allergen candidates. Left: $allergenCandidates")
    }

    private fun intersectIngredients(foods: Set<Food>): Set<Ingredient> =
        foods.map { it.ingredients }.reduce(Set<Ingredient>::intersect)

    constructor(testInput: String) : this() {
        var counter = 0
        foods = testInput.lines().filter { it.isNotBlank() }
            .map { it.split("(contains ") }
            .map { (p1, p2) ->
                Food(
                    counter++,
                    p1.trim().split(" ").toSet(),
                    p2.trim().replace(")", "").split(", ").toSet()
                )
            }
        foods.onEach { f -> f.allergens.onEach { a -> foodByAllergen.computeIfAbsent(a) { mutableSetOf() }.add(f) } }
        foods.onEach { f ->
            f.ingredients.onEach { i ->
                foodByIngredient.computeIfAbsent(i) { mutableSetOf() }.add(f)
            }
        }
    }

    private data class Food(
        val id: Int,
        var ingredients: Set<Ingredient>,
        var allergens: Set<Allergen>,
    )

}

private typealias Ingredient = String
private typealias Allergen = String

fun main() {
    listOf(
        { verifyResult("mxmxvkd,sqjhc,fvjkl", Day21(checkInput).solve2()) },
        { println("Result is " + Day21(testInput).solve2()) }
    ).onEachIndexed { i, test -> measure(test, i) }
}

private const val checkInput: String = "" +
        "mxmxvkd kfcds sqjhc nhms (contains dairy, fish)\n" +
        "trh fvjkl sbzzf mxmxvkd (contains dairy)\n" +
        "sqjhc fvjkl (contains soy)\n" +
        "sqjhc mxmxvkd sbzzf (contains fish)\n"

private val testInput by lazy { readResourceFile("/advent2020/day21-task1.txt") }