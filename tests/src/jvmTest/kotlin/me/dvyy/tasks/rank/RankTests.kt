package me.dvyy.tasks.rank

import io.kotest.matchers.shouldBe
import me.dvyy.tasks.DbTest
import me.dvyy.tasks.model.rank.Rank
import kotlin.test.Test

class RankTests : DbTest() {
//    @Test
//    fun `should generate valid intermediate ranks`() {
//        val start = Rank.middle
//        val end = start.next()
//        val ranks = mutableListOf<Rank>()
//        ranks.add(start)
//        ranks.add(end)
//
//        var currentLow = start
//        repeat(50) {
//            val middle = currentLow.between(end)
//            ranks.add(middle)
//            currentLow = middle
//        }
//
//        val sortedRanks = ranks.sortedBy { it.string }
//
//        assertEquals(sortedRanks, ranks, "Ranks should be naturally sortable by their string value")
//        assertEquals(ranks.size, ranks.distinct().size, "All generated ranks should be unique")
//    }

    @Test
    fun `rank should not explode when getting next rank`() {
        var rank = Rank.middle
        repeat(100) {
            rank = rank.next()
        }
        rank.string shouldBe "zzzzzzzw"
    }

    @Test
    fun `rank should not explode when getting middle rank`() {
        var first = Rank.middle
        val last = first.next()
        var middle = first
        repeat(100) {
            middle = middle.between(last)
        }
        middle.string shouldBe "nzzzzzzzzzzzzzzzzzzzz"
    }
}
