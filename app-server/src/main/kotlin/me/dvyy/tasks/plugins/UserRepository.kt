package me.dvyy.tasks.plugins

import me.dvyy.sqlite.Database
import me.dvyy.sqlite.Identity
import me.dvyy.sqlite.tables.Table

class UserTable : Table(
    """
    CREATE TABLE IF NOT EXISTS users (
        name TEXT,
        id INTEGER PRIMARY KEY AUTOINCREMENT
    )
    """.trimIndent()
)

class UserRepository(val db: Database) {
    suspend fun initialize() = db.write {
        UserTable().create()
    }

    suspend fun getOrCreateUserId(name: String): Identity = db.read {
        val id = select("SELECT id FROM users WHERE name = ?", name)
            .firstOrNull { getInt(0) }
        if (id != null) return@read id
        exec("INSERT INTO users (name) VALUES (?)", name)
        select("SELECT last_insert_rowid()").first { getInt(0) }
    }

}