package me.dvyy.tasks.app.ui.state

import androidx.compose.runtime.Immutable

@Immutable
sealed interface Loadable<T> {
    @Immutable
    class Loading<T> : Loadable<T>

    @Immutable
    class Loaded<T>(
        val data: T,
    ) : Loadable<T>

}

inline fun <T> Loadable<T>.loadedOrNull() = (this as? Loadable.Loaded<T>)?.data

inline fun <T> T.loaded() = Loadable.Loaded(this)
