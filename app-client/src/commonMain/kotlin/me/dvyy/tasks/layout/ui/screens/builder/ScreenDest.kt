package me.dvyy.tasks.layout.ui.screens.builder

import androidx.compose.runtime.Immutable
import kotlinx.collections.immutable.ImmutableList
import kotlinx.serialization.Serializable
import me.dvyy.tasks.layout.ui.screens.allProjectsScreen
import me.dvyy.tasks.layout.ui.screens.emptyScreen
import me.dvyy.tasks.layout.ui.screens.projectScreen
import me.dvyy.tasks.layout.ui.screens.weekScreen
import me.dvyy.tasks.model.ListId

@Serializable
@Immutable
sealed interface ScreenDest {
    @Serializable
    data class Week(
        val startAtToday: Boolean = false,
        val takeDays: Int = 7,
    ) : ScreenDest


    @Serializable
    data class Projects(
        val projects: ImmutableList<ListId>? = null,
        val horizontal: Boolean = true,
        val staggered: Boolean = false,
    ) : ScreenDest

    @Serializable
    data class Project(val id: ListId) : ScreenDest

    @Serializable
    data object Empty : ScreenDest

    fun toScreen(): Screen = when (this) {
        is Week -> weekScreen(this)
        is Projects -> allProjectsScreen(this)
        is Project -> projectScreen(this)
        is Empty -> emptyScreen()
    }
}


