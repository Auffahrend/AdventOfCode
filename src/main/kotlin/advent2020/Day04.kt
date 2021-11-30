package advent2020

import advent2020.Passport.Rule
import readResourceFile
import verifyResult

/*
--- Day 4: Passport Processing ---

You arrive at the airport only to realize that you grabbed your North Pole Credentials instead of your passport. While these documents are extremely similar, North Pole Credentials aren't issued by a country and therefore aren't actually valid documentation for travel in most of the world.

It seems like you're not the only one having problems, though; a very long line has formed for the automatic passport scanners, and the delay could upset your travel itinerary.

Due to some questionable network security, you realize you might be able to solve both of these problems at the same time.

The automatic passport scanners are slow because they're having trouble detecting which passports have all required fields. The expected fields are as follows:

    byr (Birth Year)
    iyr (Issue Year)
    eyr (Expiration Year)
    hgt (Height)
    hcl (Hair Color)
    ecl (Eye Color)
    pid (Passport ID)
    cid (Country ID)

Passport data is validated in batch files (your puzzle input). Each passport is represented as a sequence of key:value pairs separated by spaces or newlines. Passports are separated by blank lines.

Here is an example batch file containing four passports:

ecl:gry pid:860033327 eyr:2020 hcl:#fffffd
byr:1937 iyr:2017 cid:147 hgt:183cm

iyr:2013 ecl:amb cid:350 eyr:2023 pid:028048884
hcl:#cfa07d byr:1929

hcl:#ae17e1 iyr:2013
eyr:2024
ecl:brn pid:760753108 byr:1931
hgt:179cm

hcl:#cfa07d eyr:2025 pid:166559648
iyr:2011 ecl:brn hgt:59in

The first passport is valid - all eight fields are present. The second passport is invalid - it is missing hgt (the Height field).

The third passport is interesting; the only missing field is cid, so it looks like data from North Pole Credentials, not a passport at all! Surely, nobody would mind if you made the system temporarily ignore missing cid fields. Treat this "passport" as valid.

The fourth passport is missing two fields, cid and byr. Missing cid is fine, but missing any other field is not, so this passport is invalid.

According to the above rules, your improved system would report 2 valid passports.

Count the number of valid passports - those that have all required fields. Treat cid as optional. In your batch file, how many passports are valid?

 */
private class Day04(input: String) {
    private val passports = mutableListOf(Passport())
    private val validationRules = setOf(
        Rule("byr") { it.isYearBetween(1920..2002) },
        Rule("iyr") { it.isYearBetween(2010..2020) },
        Rule("eyr") { it.isYearBetween(2020..2030) },
        Rule("hgt") { it.isCmHeightBetween(150..193) || it.isInchHeightBetween(59..76) },
        Rule("hcl") { it.isValidHairColor() },
        Rule("ecl") { it.isValidEyeColor() },
        Rule("pid") { it.isValidCountryId() },
    )


    private fun String.isYearBetween(range: IntRange) = yearRegex.matches(this) && this.toInt() in range
    private fun String.isCmHeightBetween(range: IntRange): Boolean =
        cmHeightRegex.matchEntire(this)?.groups?.get(1)?.let { it.value.toInt() in range } ?: false
    private fun String.isInchHeightBetween(range: IntRange): Boolean =
        inchHeightRegex.matchEntire(this)?.groups?.get(1)?.let { it.value.toInt() in range } ?: false
    private fun String.isValidHairColor(): Boolean = colorRegex.matches(this)
    private fun String.isValidEyeColor(): Boolean = this in setOf("amb", "blu", "brn", "gry", "grn", "hzl", "oth")
    private fun String.isValidCountryId(): Boolean = countryIdRegex.matches(this)

    init {
        input.lines()
            .onEach {
                if (it.isEmpty()) {
                    passports.add(Passport())
                } else {
                    passports.last().addProperties(it)
                }
            }
    }

    fun solve(): Int = passports.count { it.isNotEmpty() && it.isValid(validationRules) }

    companion object {
        private val yearRegex = Regex("\\d{4}")
        private val cmHeightRegex = Regex("(\\d{3})cm")
        private val inchHeightRegex = Regex("(\\d{2})in")
        private val colorRegex = Regex("#[0-9a-f]{6}")
        private val countryIdRegex = Regex("[0-9]{9}")
    }
}

private class Passport {
    data class Rule(private val propertyName: String, private val check: (String) -> Boolean) {
        fun validate(passport: Passport): Boolean = passport.properties[propertyName]?.let(check) ?: false
    }

    val properties = mutableMapOf<String, String>()
    fun addProperties(data: String) {
        data
            .split(" ")
            .filter { it.isNotBlank() }
            .map { it.split(":") }
            .onEach { (key, value) -> properties[key] = value }
    }

    fun isNotEmpty(): Boolean = properties.isNotEmpty()
    fun isValid(validationRules: Set<Rule>): Boolean = validationRules.all { it.validate(this) }
}


fun main() {
    verifyResult(4, Day04(checkInput).solve())
    println(Day04(testInput).solve())
}

private const val checkInput: String = "" +
        "pid:087499704 hgt:74in ecl:grn iyr:2012 eyr:2030 byr:1980\n" +
        "hcl:#623a2f\n" +
        "\n" +
        "eyr:2029 ecl:blu cid:129 byr:1989\n" +
        "iyr:2014 pid:896056539 hcl:#a97842 hgt:165cm\n" +
        "\n" +
        "hcl:#888785\n" +
        "hgt:164cm byr:2001 iyr:2015 cid:88\n" +
        "pid:545766238 ecl:hzl\n" +
        "eyr:2022\n" +
        "\n" +
        "iyr:2010 hgt:158cm hcl:#b6652a ecl:blu byr:1944 eyr:2021 pid:093154719"

private val testInput by lazy { readResourceFile("/advent2020/day04-task1.txt") }