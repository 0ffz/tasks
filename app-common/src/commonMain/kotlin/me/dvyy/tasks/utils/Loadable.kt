package me.dvyy.tasks.utils

import androidx.compose.runtime.Immutable

@Immutable
sealed interface Loadable<T> {
    @Immutable
    class Loading<T> : Loadable<T>

    @Immutable
    data class Loaded<T>(
        val data: T,
    ) : Loadable<T>

}

fun <T> Loadable<T>.loadedOrNull() = (this as? Loadable.Loaded<T>)?.data

fun <T> T.loaded() = Loadable.Loaded(this)
