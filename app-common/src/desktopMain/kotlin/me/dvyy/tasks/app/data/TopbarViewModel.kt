package me.dvyy.tasks.app.data

import androidx.compose.ui.window.WindowPlacement
import androidx.compose.ui.window.WindowScope
import androidx.compose.ui.window.WindowState
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.awt.Dimension
import java.awt.Point
import java.awt.Toolkit

class TopbarViewModel(
    val windowState: WindowState,
    val windowScope: WindowScope,
    private val onClose: () -> Unit,
) : ViewModel() {

    val _floatingWindowSize = MutableStateFlow<WindowSize?>(null)
    val floatingWindowSize = _floatingWindowSize.asStateFlow()

    fun minimize() {
        windowState.isMinimized = true
    }

    fun ensureMaximized() {
        when (OS.get()) {
            OS.WINDOWS -> {
                if (_floatingWindowSize.value != null) return
                val window = windowScope.window
                val graphicsConfiguration = window.graphicsConfiguration
                val insets = Toolkit.getDefaultToolkit().getScreenInsets(graphicsConfiguration)
                val bounds = graphicsConfiguration.bounds
                _floatingWindowSize.update<WindowSize?> { WindowSize(window.size, window.location) }
                window.setSize(bounds.width, bounds.height - insets.bottom)
                window.setLocation(bounds.x, bounds.y)
            }

            else -> {
                windowState.placement = WindowPlacement.Maximized
            }

        }
    }

    fun ensureFloating() {
        when (OS.get()) {
            OS.WINDOWS -> {
                if (_floatingWindowSize.value == null) return
                val window = windowScope.window
                _floatingWindowSize.value?.let {
                    window.size = it.size
                    window.location = it.location
                    _floatingWindowSize.update { null }
                }
            }

            else -> {
                windowState.placement = WindowPlacement.Floating
            }
        }
    }

    fun toggleMaximized() = when (OS.get()) {
        OS.WINDOWS -> {
            if (_floatingWindowSize.value == null) ensureMaximized()
            else ensureFloating()
        }

        else -> {
            if (windowState.placement == WindowPlacement.Maximized)
                ensureFloating()
            else ensureMaximized()
        }
    }

    fun closeWindow() {
        onClose()
    }
}

class WindowSize(
    val size: Dimension,
    val location: Point,
)
