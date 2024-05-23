package me.dvyy.tasks.tasks.ui.elements.list

import androidx.compose.runtime.Immutable
import kotlinx.datetime.LocalDate
import kotlinx.serialization.Serializable

sealed interface ListTitle {
    @Immutable
    data class Date(val date: LocalDate) : ListTitle

    @Immutable
    @Serializable
    data class Project(val name: String) : ListTitle
}
