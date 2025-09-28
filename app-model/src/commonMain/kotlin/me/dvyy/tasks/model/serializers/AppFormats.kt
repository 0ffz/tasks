package me.dvyy.tasks.model.serializers

import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import kotlin.uuid.Uuid

object AppFormats {
    val networkModule = SerializersModule {
        contextual(Uuid::class, UuidAsStringSerializer)
    }

    val json = Json {
        prettyPrint = true
        ignoreUnknownKeys = true
        encodeDefaults = false
        serializersModule = networkModule
    }
}
