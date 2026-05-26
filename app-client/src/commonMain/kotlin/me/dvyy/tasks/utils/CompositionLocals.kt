package me.dvyy.tasks.utils

import androidx.compose.runtime.compositionLocalOf
import com.mohamedrejeb.compose.dnd.DragAndDropState
import me.dvyy.tasks.layout.ui.screens.builder.ScreenDest
import kotlin.uuid.Uuid

sealed interface Dragged {
    data class Task(val uuid: Uuid) : Dragged
    data class Layout(
        val destination: ScreenDest,
        val source: Uuid?,
    ) : Dragged
}

val LocalDragAndDropState = compositionLocalOf<DragAndDropState<Dragged>> { error("No local drag and drop state") }
//val LocalReorderState = compositionLocalOf<ReorderState<Uuid>> { error("No local drag and drop state") }