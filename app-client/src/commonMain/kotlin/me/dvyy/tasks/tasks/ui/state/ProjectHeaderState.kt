package me.dvyy.tasks.tasks.ui.state

import kotlinx.datetime.LocalDate

sealed interface ProjectHeaderState {
    data class Named(
        val displayName: String,
        val onRename: (String) -> Unit,
    ) : ProjectHeaderState

    data class Date(val date: LocalDate) : ProjectHeaderState

    object Loading : ProjectHeaderState
}