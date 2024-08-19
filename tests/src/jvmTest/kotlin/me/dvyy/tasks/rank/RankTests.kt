package me.dvyy.tasks.rank

import io.kotest.assertions.throwables.shouldNotThrowAny
import io.kotest.assertions.throwables.shouldThrow
import me.dvyy.tasks.DbTest
import me.dvyy.tasks.model.ListId
import me.dvyy.tasks.model.TaskId
import me.dvyy.tasks.model.network.Changelist
import me.dvyy.tasks.model.network.NetworkMessage
import me.dvyy.tasks.model.network.RankNetworkModel
import me.dvyy.tasks.plugins.ServerDataSource
import org.junit.jupiter.api.Test

class RankTests : DbTest() {
    @Test
    fun `should fail to insert rank outside of a-z`() {
        shouldThrow<IllegalArgumentException> {
            insert("/a")
        }
        shouldNotThrowAny {
            insert("zabc")
        }
    }

    fun insert(rank: String) {
        val source = ServerDataSource(serverDb)
        val taskId = TaskId.new()

        source.resolveMessages(
            Changelist(
                lastSynced = null, upTo = now(), messages = listOf(
                    NetworkMessage(RankNetworkModel(taskId.uuid, ListId.newProject().uuid, rank), taskId.uuid, now())
                )
            ), userSession
        )
    }
}
