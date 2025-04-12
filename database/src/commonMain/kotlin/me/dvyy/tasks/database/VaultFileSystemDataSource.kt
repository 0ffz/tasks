package me.dvyy.tasks.database

import me.dvyy.tasks.database.model.Note
import java.nio.file.Path
import java.security.MessageDigest
import kotlin.io.path.*

class VaultFileSystemDataSource(
    val vaultRoot: Path,
) {
    fun createOrSaveNote(path: VaultPath, fileContent: String = "") {
        val fsPath = path.toPath()
        fsPath.createParentDirectories().also { if (it.notExists()) it.createFile() }
        fsPath.writeText(fileContent)
    }

    fun deleteNote(path: VaultPath): Boolean {
        return path.toPath().deleteIfExists()
    }

    fun VaultPath.toPath(): Path {
        return vaultRoot.resolve(pathString)
    }

    /**
     * @return md5 hash of the saved document, to update in database.
     */
    @OptIn(ExperimentalStdlibApi::class)
    fun saveNote(path: VaultPath, note: Note): String {
        val content = note.stringify()
        createOrSaveNote(path, content)
        return MD5.digest(content.toByteArray()).toHexString()
    }

    fun moveNote(from: VaultPath, to: VaultPath) {
        to.toPath().createParentDirectories()
        from.toPath().moveTo(to.toPath())
    }

    fun Path.toVaultPath(): VaultPath {
        return VaultPath(relativeTo(vaultRoot).toString())
    }

    fun walkIndexable(directory: VaultPath): Sequence<VaultPath> = directory.toPath().walk()
        .filter { it.isRegularFile() && isIndexable(it) }
        .map { it.toVaultPath() }

    fun hash(path: VaultPath): String {
        return fileHash(path.toPath())
    }

    fun inputStream(path: VaultPath) = path.toPath().inputStream()

    companion object {
        val MD5 = MessageDigest.getInstance("MD5")

        @OptIn(ExperimentalStdlibApi::class)
        fun fileHash(file: Path): String = MD5.digest(file.readBytes()).toHexString()

        fun isIndexable(file: Path): Boolean = file.extension == "md"// || file.extension == "yaml"

        @OptIn(ExperimentalStdlibApi::class)
        fun Note.calculateHash(): String = MD5.digest(stringify().toByteArray()).toHexString()
    }
}
