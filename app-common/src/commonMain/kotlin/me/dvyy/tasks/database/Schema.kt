package me.dvyy.tasks.database

import me.dvyy.syncengine.db.Database
import me.dvyy.syncengine.db.WriteTransaction

class Schema(
    val syncedTables: List<RollbackTable>,
) {
    suspend fun initTables() {
        Database.write {
            syncedTables.forEach { it.create() }
        }
    }

    context(tx: WriteTransaction)
    fun rollbackAll() {
        syncedTables.forEach { it.rollback() }
    }
}