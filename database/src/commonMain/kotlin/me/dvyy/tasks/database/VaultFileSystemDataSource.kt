package me.dvyy.tasks.database

import org.dizitart.no2.collection.Document
import java.nio.file.Path
import java.security.MessageDigest
import kotlin.io.path.*

class VaultFileSystemDataSource(
    val root: Path,
) {
    fun createDocument(path: VaultPath) {
        path.toPath().createParentDirectories().createFile()
    }

    fun deleteDocument(path: VaultPath): Boolean {
        return path.toPath().deleteIfExists()
    }

    fun VaultPath.toPath(): Path {
        return root.resolve(pathString)
    }

    fun saveDocument(path: VaultPath, document: Document) {
        val content = document["fileContent"] as String
        val frontMatter =
        path.toPath().writeText(content)
    }

    fun Path.toVaultPath(): VaultPath {
        return VaultPath(relativeTo(root).toString())
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

        fun Document.toYamlFrontMatter() = buildString {
            appendLine("---")
            this@toYamlFrontMatter.forEach { pair ->
                appendLine("${pair.first}: ${pair.second}")
            }
            appendLine("---")
        }
    }
}
