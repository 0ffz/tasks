package me.dvyy.tasks.database

import me.dvyy.tasks.database.helpers.DocumentHelpers.content
import me.dvyy.tasks.database.helpers.DocumentHelpers.frontMatter
import me.dvyy.tasks.database.helpers.DocumentYamlHelpers.encodeToString
import org.dizitart.no2.collection.Document
import java.nio.file.Path
import java.security.MessageDigest
import kotlin.io.path.*

class VaultFileSystemDataSource(
    val vaultRoot: Path,
) {
    fun createOrSaveDocument(path: VaultPath, fileContent: String = "") {
        val fsPath = path.toPath()
        fsPath.createParentDirectories().also { if (it.notExists()) it.createFile() }
        fsPath.writeText(fileContent)
    }

    fun deleteDocument(path: VaultPath): Boolean {
        return path.toPath().deleteIfExists()
    }

    fun VaultPath.toPath(): Path {
        return vaultRoot.resolve(pathString)
    }

    /**
     * @return md5 hash of the saved document, to update in database.
     */
    @OptIn(ExperimentalStdlibApi::class)
    fun saveDocument(path: VaultPath, document: Document): String {
        val content = document.stringify()
        createOrSaveDocument(path, content)
        return MD5.digest(content.toByteArray()).toHexString()
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

        fun Document.stringify() = "---\n${frontMatter().encodeToString()}\n---\n${content()}"

        @OptIn(ExperimentalStdlibApi::class)
        val Document.md5Hash get() = MD5.digest(stringify().toByteArray()).toHexString()
    }
}
