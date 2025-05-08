import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonArray
import kotlinx.serialization.json.buildJsonObject
import org.dizitart.no2.collection.Document
import org.dizitart.no2.collection.NitriteId

/**
 * Converts a Nitrite Document to a kotlinx.serialization JsonElement.
 *
 * Handles all common document value types:
 * - Primitives (String, Number, Boolean)
 * - Nested objects/maps
 * - Lists
 * - NitriteId
 * - Null values
 *
 * @param document The Nitrite Document to convert
 * @return A JsonElement representation of the document
 */
fun documentToJsonElement(document: Document): JsonElement {
    val jsonObject = buildJsonObject {
        for (pair in document) {
            val key = pair.first
            val value = pair.second
            put(key, valueToJsonElement(value))
        }
    }
    return jsonObject
}

/**
 * Converts a value from a Document to its appropriate JsonElement representation.
 *
 * @param value The value to convert
 * @return A JsonElement representation of the value
 */
private fun valueToJsonElement(value: Any?): JsonElement {
    return when (value) {
        null -> JsonNull
        is String -> JsonPrimitive(value)
        is Number -> JsonPrimitive(value)
        is Boolean -> JsonPrimitive(value)
        is Document -> documentToJsonElement(value)
        is Map<*, *> -> {
            buildJsonObject {
                for ((k, v) in value) {
                    if (k is String) {
                        put(k, valueToJsonElement(v))
                    }
                }
            }
        }
        is List<*> -> {
            buildJsonArray {
                for (item in value) {
                    add(valueToJsonElement(item))
                }
            }
        }
        is Array<*> -> {
            buildJsonArray {
                for (item in value) {
                    add(valueToJsonElement(item))
                }
            }
        }
        is NitriteId -> JsonPrimitive(value.toString())
        else -> JsonPrimitive(value.toString())
    }
}

/**
 * Extension function to easily convert a Document to a JsonElement.
 *
 * @return A JsonElement representation of this document
 */
fun Document.toJsonElement(): JsonElement = documentToJsonElement(this)
