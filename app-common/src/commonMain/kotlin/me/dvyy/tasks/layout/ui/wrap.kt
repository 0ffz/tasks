package me.dvyy.tasks.layout.ui

import androidx.compose.runtime.Composable

inline fun LayoutStructure.Single.wrap(crossinline wrap: @Composable (original: @Composable () -> Unit) -> Unit): LayoutStructure.Single =
    object : LayoutStructure.Single.Wrap(this) {
        @Composable
        override fun content() {
            wrap { super.content() }
        }
    }
