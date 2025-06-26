package me.dvyy.tasks.model.database

object RankFunctions {
    const val FIRST_CHAR = 'a'
    const val LAST_CHAR = 'z' + 1
    val middleChar = charBetweenOrNull(FIRST_CHAR, LAST_CHAR)!!

    fun getLexicographicMiddle(s1: String, s2: String): String {
        val (s1, s2) = listOf(s1, s2).sorted()
        val first = s1.padEnd(s2.length, FIRST_CHAR)
        val second = s2.padEnd(s1.length, LAST_CHAR)
        val equalUntil = first.zip(second).indexOfFirst { it.first != it.second }
        return (equalUntil..first.lastIndex)
            .firstNotNullOfOrNull { charBetweenOrNull(first[it], second[it])?.to(it) }
            ?.let { (char, index) -> first.take(index) + char }
            ?: (first + middleChar)
    }

    fun charBetweenOrNull(first: Char, second: Char): Char? {
        return ((first.code + second.code) / 2).toChar().takeIf { it != first && it != second }
    }

    fun getRankAfter(rank: String): String {
        return getLexicographicMiddle(rank, LAST_CHAR.toString())
    }

    fun getRankBefore(rank: String): String {
        return getLexicographicMiddle(rank, FIRST_CHAR.toString())
    }
}
