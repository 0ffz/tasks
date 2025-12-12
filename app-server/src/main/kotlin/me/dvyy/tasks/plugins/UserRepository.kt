package me.dvyy.tasks.plugins

import me.dvyy.sqlite.Database
import me.dvyy.sqlite.Identity
import me.dvyy.tasks.server.database.ServerQueries

class UserRepository(
    val db: Database,
    val dao: ServerQueries,
) {
    suspend fun initialize() = db.write {
        dao.create()
    }

    suspend fun getOrCreateUserId(name: String): Identity = db.write {
        dao.users.getOrCreateUserId(name)
    }
}