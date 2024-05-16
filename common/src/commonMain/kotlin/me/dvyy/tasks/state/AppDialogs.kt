package me.dvyy.tasks.state

sealed class AppDialog {
    data object Auth : AppDialog()
}
