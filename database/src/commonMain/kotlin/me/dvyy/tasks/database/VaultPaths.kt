package me.dvyy.tasks.database

import java.nio.file.Path
import kotlin.io.path.createDirectories
import kotlin.io.path.div
import kotlin.io.path.exists

data class VaultPaths(
    val root: Path,
) {
    val configuration = (root / ".configuration").createDirectories()
    val dbPath = configuration / "vault.db"
    val settings = configuration / "settings.yml"

    fun Path.ensureExists() {
        if (!exists()) { createDirectories() }
    }
}
