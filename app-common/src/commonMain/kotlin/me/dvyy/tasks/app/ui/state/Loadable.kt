package me.dvyy.tasks.app.ui.state

sealed interface Loadable<T> {
    class Loading<T> : Loadable<T>
    class Loaded<T>(
        val data: T,
    ) : Loadable<T>

}

inline fun <T> Loadable<T>.loadedOrNull() = (this as? Loadable.Loaded<T>)?.data

inline fun <T> T.loaded() = Loadable.Loaded(this)
