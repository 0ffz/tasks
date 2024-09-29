package me.dvyy.tasks.core.ui.modifiers

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.PointerMatcher
import androidx.compose.foundation.onClick
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.PointerButton
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.onPointerEvent

@OptIn(ExperimentalComposeUiApi::class)
actual fun Modifier.onHoverIfAvailable(
    onEnter: () -> Unit,
    onExit: () -> Unit,
): Modifier = onPointerEvent(PointerEventType.Enter) { onEnter() }
    .onPointerEvent(PointerEventType.Exit) { onExit() }

@OptIn(ExperimentalFoundationApi::class)
actual fun Modifier.onMiddleMouseClick(onClick: () -> Unit): Modifier =
    onClick(matcher = PointerMatcher.mouse(PointerButton.Tertiary)) {
        onClick()
    }
