package me.dvyy.tasks.database.helpers

import com.charleskorn.kaml.SingleLineStringStyle
import com.charleskorn.kaml.Yaml
import com.charleskorn.kaml.YamlConfiguration
import com.charleskorn.kaml.YamlList
import com.charleskorn.kaml.YamlMap
import com.charleskorn.kaml.YamlNode
import com.charleskorn.kaml.YamlNull
import com.charleskorn.kaml.YamlPath
import com.charleskorn.kaml.YamlScalar
import com.charleskorn.kaml.YamlTaggedNode
import com.charleskorn.kaml.yamlMap
import com.charleskorn.kaml.yamlScalar
import org.dizitart.kno2.documentOf
import org.dizitart.kno2.serialization.KotlinXSerializationMapper
import org.dizitart.no2.collection.Document

object DocumentYamlHelpers {
    val mapper = KotlinXSerializationMapper()
    val styledYaml = Yaml(
        configuration = YamlConfiguration(
            singleLineStringStyle = SingleLineStringStyle.PlainExceptAmbiguous
        )
    )

    fun decodeFromYaml(yaml: String): Document {
        return Yaml.default.parseToYamlNode(yaml).yamlMap.toDocument()
    }

    fun YamlMap.toDocument(): Document = decodeYamlNode(this) as Document

    fun Document.toYaml(): YamlMap {
        return encodeYamlNode(this).yamlMap
    }

    fun Document.encodeToString(): String {
        return styledYaml.encodeToString(YamlNode.serializer(), toYaml())
    }

    private fun encodeYamlNode(element: Any?): YamlNode = when(element) {
        is List<*> -> YamlList(element.map { encodeYamlNode(it!!) }, YamlPath.root)
        is Document -> YamlMap(element.associate { YamlScalar(it.first, YamlPath.root) to encodeYamlNode(it.second) }, YamlPath.root)
        null -> YamlNull(YamlPath.root)
        is String -> YamlScalar(element, YamlPath.root)
        else -> throw IllegalArgumentException("Unsupported type: ${element::class.simpleName}")
    }

    private fun decodeYamlNode(node: YamlNode): Any? = when(node) {
        is YamlList -> node.items.map { decodeYamlNode(it) }
        is YamlMap -> documentOf(*node.entries.map { it.key.yamlScalar.content to decodeYamlNode(it.value) }.toTypedArray())
        is YamlNull -> null
        is YamlScalar -> {
            runCatching { node.toInt() }.getOrNull()
                ?: runCatching { node.toDouble() }.getOrNull()
                ?: runCatching { node.toBoolean() }.getOrNull()
                ?: node.content
        }
        is YamlTaggedNode -> decodeYamlNode(node.innerNode)
    }
}
