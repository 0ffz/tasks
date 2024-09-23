package me.dvyy.tasks.app.ui

import androidx.compose.ui.input.pointer.PointerIcon
import java.awt.Cursor

actual object Cursors {
    actual val horizontalResize: PointerIcon =
        PointerIcon(Cursor.getPredefinedCursor(Cursor.S_RESIZE_CURSOR))
    actual val verticalResize: PointerIcon
        get() = PointerIcon(Cursor.getPredefinedCursor(Cursor.W_RESIZE_CURSOR))
}
