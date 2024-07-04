package me.dvyy.tasks.app.ui

import androidx.compose.ui.input.pointer.PointerIcon
import java.awt.Cursor

actual object Cursors {
    actual val horizontalResize: PointerIcon =
        PointerIcon(Cursor.getPredefinedCursor(Cursor.S_RESIZE_CURSOR))
}
