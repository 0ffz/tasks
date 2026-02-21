package me.dvyy.tasks.tasks.ui.elements.task.properties

sealed interface FocusedOption {
    data object None : FocusedOption
    data object Highlight : FocusedOption
}