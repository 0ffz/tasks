package me.dvyy.tasks.utils

import androidx.compose.runtime.compositionLocalOf
import com.mohamedrejeb.compose.dnd.DragAndDropState
import kotlin.uuid.Uuid

val LocalDragAndDropState = compositionLocalOf<DragAndDropState<Uuid>> { error("No local drag and drop state") }
//val LocalReorderState = compositionLocalOf<ReorderState<Uuid>> { error("No local drag and drop state") }