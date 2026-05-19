package me.dvyy.tasks.helpers

import me.dvyy.sqlite.Database
import kotlin.io.path.absolutePathString
import kotlin.io.path.createTempDirectory
import kotlin.io.path.outputStream

object Helpers {
    /**
     * Copies a database from a resource directory (including -wal and -shm files) into a temp folder then opens it as a [me.dvyy.sqlite.Database]
     */
    fun loadDbFromResource(path: String): Database {
        val tempDir = createTempDirectory("test-db")

        val dbFile = tempDir.resolve("test.db")
        val walFile = tempDir.resolve("test.db-wal")
        val shmFile = tempDir.resolve("test.db-shm")

        // Copy main database file and WAL/SHM files if they exist
        listOf(
            path to dbFile,
            "$path-wal" to walFile,
            "$path-shm" to shmFile
        ).forEach { (resourcePath, targetFile) ->
            Helpers::class.java.getResourceAsStream(resourcePath)?.use { input ->
                targetFile.outputStream().use { output ->
                    input.copyTo(output)
                }
            } ?: if (resourcePath == path) {
                throw IllegalArgumentException("Resource not found: $path")
            } else Unit
        }
        return Database(dbFile.absolutePathString())
    }
}