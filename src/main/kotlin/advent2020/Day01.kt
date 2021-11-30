package advent2020

import java.util.*

class Day1(expenseLines: Collection<Long>) {
    private val expenses = TreeMap<Long, Int>()

    init {
        expenseLines.forEach { value -> expenses.compute(value) { _, count -> (count ?: 0) + 1 } }
    }

    /*
    Before you leave, the Elves in accounting just need you to fix your expense report (your puzzle input); apparently, something isn't quite adding up.
    Specifically, they need you to find the two entries that sum to 2020 and then multiply those two numbers together.
    For example, suppose your expense report contained the following:
    1721
    979
    366
    299
    675
    1456
    In this list, the two entries that sum to 2020 are 1721 and 299. Multiplying them together produces 1721 * 299 = 514579, so the correct answer is 514579.
     */

    private fun findNLinesWhichAddTo(n: Int, targetSum: Long, expenses: TreeMap<Long, Int>): List<List<Long>> {
        if (n == 1) {
            val count = expenses[targetSum]
            return if (count != null && count > 0) {
                listOf(listOf(targetSum))
            } else emptyList()
        } else {
            val result = mutableListOf<List<Long>>()
            while (expenses.isNotEmpty()) {
                val first = expenses.firstEntry()
                if (first.value > 0) {
                    val left = first.key; expenses[first.key] = first.value - 1
                    findNLinesWhichAddTo(n - 1, targetSum - left, TreeMap(expenses))
                        .map { it + left }
                        .onEach { it.onEach { number -> expenses.compute(number) { _, count -> count!! - 1}  } }
                        .onEach { result.add(it) }
                } else expenses.remove(first.key)
            }
            return result
        }
    }

    fun solve(n: Int, targetSum: Long = 2020): List<Long> = findNLinesWhichAddTo(n, targetSum, expenses)
        .map { numbers -> numbers.reduce{ i, j -> i * j  } }
        .distinct()
}

private fun main() {
    Day1(actualData).solve(n = 3).map { println(it) }
}

private fun parseInput(input: String): List<Long> = input.lines().filter { it.isNotBlank() }.map { it.toLong() }
private val testData = ("1721\n" +
        "979\n" +
        "366\n" +
        "299\n" +
        "675\n" +
        "1456")
    .let(::parseInput)

private val actualData = ("" +
        "1863\n" +
        "1750\n" +
        "1767\n" +
        "1986\n" +
        "1180\n" +
        "1719\n" +
        "1946\n" +
        "1866\n" +
        "1939\n" +
        "1771\n" +
        "1766\n" +
        "1941\n" +
        "1728\n" +
        "1322\n" +
        "1316\n" +
        "1775\n" +
        "1776\n" +
        "1742\n" +
        "1726\n" +
        "1994\n" +
        "1949\n" +
        "1318\n" +
        "1223\n" +
        "1741\n" +
        "1816\n" +
        "1111\n" +
        "1991\n" +
        "1406\n" +
        "1230\n" +
        "1170\n" +
        "1823\n" +
        "1792\n" +
        "1148\n" +
        "1953\n" +
        "1706\n" +
        "1724\n" +
        "1307\n" +
        "1844\n" +
        "1943\n" +
        "1862\n" +
        "1812\n" +
        "1286\n" +
        "1837\n" +
        "1785\n" +
        "1998\n" +
        "1938\n" +
        "1248\n" +
        "1822\n" +
        "1829\n" +
        "1903\n" +
        "1131\n" +
        "1826\n" +
        "1892\n" +
        "1143\n" +
        "1898\n" +
        "1981\n" +
        "1225\n" +
        "1980\n" +
        "1850\n" +
        "1885\n" +
        "324\n" +
        "289\n" +
        "1914\n" +
        "1249\n" +
        "1848\n" +
        "1995\n" +
        "1962\n" +
        "1875\n" +
        "1827\n" +
        "1931\n" +
        "1244\n" +
        "1739\n" +
        "1897\n" +
        "1687\n" +
        "1907\n" +
        "1867\n" +
        "1922\n" +
        "1972\n" +
        "1842\n" +
        "1757\n" +
        "1610\n" +
        "1945\n" +
        "1835\n" +
        "1894\n" +
        "1265\n" +
        "1872\n" +
        "1963\n" +
        "1712\n" +
        "891\n" +
        "1813\n" +
        "1800\n" +
        "1235\n" +
        "1879\n" +
        "1732\n" +
        "1522\n" +
        "1335\n" +
        "1936\n" +
        "1830\n" +
        "1772\n" +
        "1700\n" +
        "2005\n" +
        "1253\n" +
        "1836\n" +
        "1935\n" +
        "1137\n" +
        "1951\n" +
        "1849\n" +
        "1883\n" +
        "1192\n" +
        "1824\n" +
        "1918\n" +
        "1965\n" +
        "1759\n" +
        "1195\n" +
        "1882\n" +
        "1748\n" +
        "1168\n" +
        "1200\n" +
        "1761\n" +
        "1896\n" +
        "527\n" +
        "1769\n" +
        "1560\n" +
        "1947\n" +
        "1997\n" +
        "1461\n" +
        "1828\n" +
        "1801\n" +
        "1877\n" +
        "1900\n" +
        "1924\n" +
        "1782\n" +
        "1718\n" +
        "515\n" +
        "1814\n" +
        "1744\n" +
        "1126\n" +
        "1791\n" +
        "1149\n" +
        "1932\n" +
        "1690\n" +
        "1707\n" +
        "1808\n" +
        "1957\n" +
        "1313\n" +
        "1132\n" +
        "1942\n" +
        "1934\n" +
        "1798\n" +
        "2009\n" +
        "1708\n" +
        "1774\n" +
        "1710\n" +
        "1797\n" +
        "1747\n" +
        "959\n" +
        "1955\n" +
        "1717\n" +
        "1716\n" +
        "1290\n" +
        "1654\n" +
        "1857\n" +
        "1968\n" +
        "1874\n" +
        "1853\n" +
        "1175\n" +
        "1493\n" +
        "1425\n" +
        "1125\n" +
        "1973\n" +
        "1790\n" +
        "467\n" +
        "1804\n" +
        "987\n" +
        "1944\n" +
        "2001\n" +
        "1895\n" +
        "1917\n" +
        "1218\n" +
        "1147\n" +
        "1884\n" +
        "1819\n" +
        "1179\n" +
        "1859\n" +
        "620\n" +
        "1219\n" +
        "2008\n" +
        "1871\n" +
        "1852\n" +
        "1263\n" +
        "1751\n" +
        "1989\n" +
        "1381\n" +
        "1250\n" +
        "1754\n" +
        "1725\n" +
        "1665\n" +
        "1352\n" +
        "1805\n" +
        "1325\n")
    .let(::parseInput)