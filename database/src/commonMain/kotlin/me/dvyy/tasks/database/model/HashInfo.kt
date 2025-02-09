package me.dvyy.tasks.database.model

import me.dvyy.tasks.database.VaultPath
import me.dvyy.tasks.database.helpers.DocumentHelpers.vaultPath
import me.dvyy.tasks.database.helpers.KeyHelpers
import org.dizitart.no2.collection.Document

data class HashInfo(
    val path: VaultPath,
    val md5hash: String?,
) {
    companion object {
        fun Document.toHashInfo() = HashInfo(
            vaultPath(),
            this[KeyHelpers.MD5_HASH_KEY] as String?,
        )
    }
}
