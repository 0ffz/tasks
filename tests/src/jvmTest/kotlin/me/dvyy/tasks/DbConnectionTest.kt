package me.dvyy.tasks

import me.dvyy.sqlite.Database
import me.dvyy.syncengine.SyncServer
import me.dvyy.syncengine.client.mutators.MutatorQueue
import me.dvyy.syncengine.client.sync.SyncClient
import me.dvyy.syncengine.mockService
import me.dvyy.tasks.model.database.AppDAO
import me.dvyy.tasks.model.database.AppSchema
import me.dvyy.tasks.model.database.mutators.Mutator
import kotlin.test.Test

class DbConnectionTest : DbTest() {
    val clientDatabase = Database.temporary()
    val serverDatabase = Database.temporary()

    @Test
    fun testDbConnection() {
        AppDAO(clientDatabase)
        val mutatorQueue = MutatorQueue(clientDatabase, AppDAO(clientDatabase), Mutator.serializer())
        val server = SyncServer(serverDatabase, AppSchema)
        val mockSyncService = server.mockService(user = 0)
        SyncClient(
            db = clientDatabase,
            mutators = mutatorQueue,
            schema = AppSchema,
            syncService = mockSyncService
        )
    }
}
