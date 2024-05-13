package me.dvyy.tasks.model

import com.benasher44.uuid.Uuid
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.cbor.Cbor
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule

object AppFormats {
    val networkModule = SerializersModule {
        contextual(Uuid::class, UuidAsStringSerializer)
    }

    val binaryModule = SerializersModule {
        contextual(Uuid::class, UuidSerializer)
    }

    @OptIn(ExperimentalSerializationApi::class)
    val cbor = Cbor {
        encodeDefaults = false
        ignoreUnknownKeys = true
        serializersModule = binaryModule
    }

    val json = Json {
        prettyPrint = true
        ignoreUnknownKeys = true
        encodeDefaults = false
        serializersModule = networkModule
    }
}
