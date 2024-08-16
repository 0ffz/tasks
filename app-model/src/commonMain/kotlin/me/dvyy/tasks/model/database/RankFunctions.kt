package me.dvyy.tasks.model.database

object RankFunctions {
    val firstChar = 'a'
    val lastChar = 'z' + 1
    val middleChar = charBetweenOrNull(firstChar, lastChar)!!

    fun getLexicographicMiddle(s1: String, s2: String): String {
        val (s1, s2) = listOf(s1, s2).sorted()
        val first = s1.padEnd(s2.length, firstChar)
        val second = s2.padEnd(s1.length, lastChar)
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
        return getLexicographicMiddle(rank, lastChar.toString())
    }

    fun getRankBefore(rank: String): String {
        return getLexicographicMiddle(rank, firstChar.toString())
    }
}
