package me.dvyy.tasks.database

import me.dvyy.tasks.database.helpers.DocumentYamlHelpers
import org.dizitart.kno2.documentOf
import org.dizitart.kno2.filters.notWithin
import org.dizitart.no2.Nitrite
import java.io.InputStream

class VaultIndexer(
    val vault: VaultDataSource,
    val fs: VaultFileSystemDataSource,
    val db: Nitrite,
) {

    fun indexRoot() {
        val seen = indexDirectory(VaultPath.root)
        vault.removeAll("path" notWithin seen.map { it.pathString })
    }

    fun indexDirectory(directory: VaultPath): List<VaultPath> {
        val seenFiles = fs.walkIndexable(directory)
            .map { path ->
                vault.getHashInfo(path)?.let { existing ->
                    val hash = fs.hash(path)
                    if (existing.md5hash != hash) index(path)
                } ?: index(path)
                path
            }
            .toList()
        return seenFiles
    }

    fun index(path: VaultPath) {
        val hash = fs.hash(path)
        index(path, fs.inputStream(path), hash)
    }

    /** Reads yaml front matter for an input stream and adds properties to [db] */
    fun index(path: VaultPath, inputStream: InputStream, hash: String) {
        // read front matter and content
        var frontMatter: String? = null
        val content = inputStream.bufferedReader().useLines { lines ->
            val acc = StringBuilder()
            var hasFrontMatter: Boolean? = null
            for (line in lines) {
                if(hasFrontMatter == null) {
                    hasFrontMatter = line == "---"
                    continue
                }
                if (line == "---" && hasFrontMatter && frontMatter == null) {
                    frontMatter = acc.toString()
                    acc.clear()
                } else {
                    acc.appendLine(line)
                }
            }
            acc.toString()
        }

        // convert front matter and content to document
        //TODO decode yaml lists and objects correctly
        val frontMatterDoc = frontMatter?.let { DocumentYamlHelpers.decodeFromYaml(it) } ?: documentOf()
        val document = documentOf(
            "md5hash" to hash,
            "frontMatter" to frontMatterDoc,
            "fileContent" to content
        )
        vault.upsertDocument(path, document)
    }

//    fun vaultPath(file: Path): VaultPath = VaultPath(file.relativeTo(root).pathString)

}
