package me.dvyy.tasks.app.ui

sealed class AppDialog {
    data object Auth : AppDialog()
}
