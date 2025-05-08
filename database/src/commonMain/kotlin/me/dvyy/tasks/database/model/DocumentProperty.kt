package me.dvyy.tasks.database.model

import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KClass
import kotlin.reflect.KProperty
import kotlin.reflect.typeOf

class DocumentProperty<T>(
    val name: String,
    val kClass: KClass<T & Any>,
    val default: () -> T,
) : ReadWriteProperty<DocumentWrapper, T> {
    override fun getValue(
        thisRef: DocumentWrapper,
        property: KProperty<*>,
    ): T {
        return thisRef.document.get(name) as T? ?: default()
    }

    override fun setValue(thisRef: DocumentWrapper, property: KProperty<*>, value: T) {
        thisRef.document.put(name, value)
    }
}

inline fun <reified T> property(key: String, noinline default: () -> T) = DocumentProperty<T>(
    key,
    typeOf<T>().classifier as KClass<T & Any>,
    default
)

