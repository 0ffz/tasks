package me.dvyy.tasks.model.rank

@JvmInline
value class Rank(val string: String) {
    fun next(): Rank {
        return Rank(RankFunctions.getRankAfter(string))
    }

    fun previous(): Rank {
        return Rank(RankFunctions.getRankBefore(string))
    }

    fun between(other: Rank): Rank {
        return Rank(RankFunctions.getLexicographicMiddle(string, other.string))
    }

    override fun toString(): String {
        return string
    }

    companion object {
        val middle = Rank(RankFunctions.middleChar.toString())
    }
}

fun String.asRank() = Rank(this)

object RankFunctions {
    const val FIRST_CHAR = 'a'
    const val LAST_CHAR = 'z' + 1
    val middleChar = charBetweenOrNull(FIRST_CHAR, LAST_CHAR)!!

    /**
     * @return a rank that is lexicographically between two ranks [s1] and [s2]
     */
    fun getLexicographicMiddle(s1: String, s2: String): String {
        // Renamed inner variables to avoid shadowing the parameters
        val (str1, str2) = listOf(s1, s2).sorted()
        val first = str1.padEnd(str2.length, FIRST_CHAR)
        val second = str2.padEnd(str1.length, LAST_CHAR)

        val equalUntil = first.zip(second).indexOfFirst { it.first != it.second }

        // Fixes a potential out-of-bounds crash if the padded strings become identical
        // (e.g., if s1 is "z" and s2 is "za")
        val diffIndex = if (equalUntil == -1) first.length else equalUntil

        return (diffIndex..first.lastIndex)
            .firstNotNullOfOrNull { charBetweenOrNull(first[it], second[it])?.to(it) }
            ?.let { (char, index) -> first.take(index) + char }
            ?: (first + middleChar)
    }

    fun charBetweenOrNull(first: Char, second: Char): Char? {
        return ((first.code + second.code) / 2).toChar().takeIf { it != first && it != second }
    }

    /**
     * @return a rank that lexicographically comes after [rank]
     */
    fun getRankAfter(rank: String): String {
        // Find the rightmost character we can increment without exceeding 'z'
        for (i in rank.indices.reversed()) {
            if (rank[i] < LAST_CHAR - 1) {
                // Increment the character and drop everything after to shorten the rank
                return rank.substring(0, i) + (rank[i] + 1)
            }
        }
        // If all characters are 'z', append middleChar to create space
        return rank + middleChar
    }

    /**
     * @return a rank that lexicographically comes before [rank]
     */
    fun getRankBefore(rank: String): String {
        // Find the rightmost character we can safely decrement
        for (i in rank.indices.reversed()) {
            if (rank[i] > FIRST_CHAR) {
                // Decrement the character and drop everything after to shorten the rank
                val candidate = rank.substring(0, i) + (rank[i] - 1)

                // We must avoid generating strings made entirely of FIRST_CHAR (e.g., "a", "aa")
                // because they become dead-ends where no valid characters exist to put before them.
                return if (candidate.all { it == FIRST_CHAR }) {
                    candidate + middleChar
                } else {
                    candidate
                }
            }
        }
        // Fallback for edge cases where the string has no characters > 'a'
        return getLexicographicMiddle(rank, FIRST_CHAR.toString())
    }
}
