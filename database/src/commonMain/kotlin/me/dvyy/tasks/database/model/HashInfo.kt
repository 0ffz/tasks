package me.dvyy.tasks.database.model

import me.dvyy.tasks.database.VaultPath
import org.dizitart.no2.collection.Document

data class HashInfo(
    val path: VaultPath,
    val md5hash: String?,
) {
    companion object {
        fun Document.toHashInfo() = HashInfo(
            VaultPath(this["path"] as String),
            this["md5hash"] as String?,
        )
    }
}
