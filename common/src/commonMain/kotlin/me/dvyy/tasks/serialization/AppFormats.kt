package me.dvyy.tasks.serialization

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.cbor.Cbor

object AppFormats {
    @OptIn(ExperimentalSerializationApi::class)
    val cbor = Cbor {
        encodeDefaults = false
        ignoreUnknownKeys = true
    }

}
