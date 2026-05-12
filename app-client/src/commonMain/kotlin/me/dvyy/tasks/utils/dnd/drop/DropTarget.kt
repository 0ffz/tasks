/*
 * Copyright 2023, Mohamed Ben Rejeb and the Compose Dnd project contributors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.mohamedrejeb.compose.dnd.drop

import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.node.CompositionLocalConsumerModifierNode
import androidx.compose.ui.node.LayoutAwareModifierNode
import androidx.compose.ui.node.ModifierNodeElement
import androidx.compose.ui.node.currentValueOf
import androidx.compose.ui.platform.InspectorInfo
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.toSize
import com.mohamedrejeb.compose.dnd.DragAndDropState
import com.mohamedrejeb.compose.dnd.LocalDragAndDropInfo
import com.mohamedrejeb.compose.dnd.drag.DraggedItemState

/**
 * Mark this composable as a drop target.
 *
 * @param zIndex The z-index of the drop target.
 * @param state The drag and drop state.
 * @param dropAlignment The alignment of the dropped item.
 * @param dropOffset The offset of the dropped item.
 * @param onDrop The action to perform when an item is dropped onto the target.
 * Accepts the dragged item state as a parameter.
 * @param onDragEnter The action to perform when an item is dragged over the target.
 * Accepts the dragged item state as a parameter.
 * @param onDragExit The action to perform when an item is dragged out of the target.
 * Accepts the dragged item state as a parameter.
 */
fun <T> Modifier.dropTarget(
    state: DragAndDropState<T>,
    zIndex: Float = 0f,
    dropAlignment: Alignment = Alignment.Center,
    dropOffset: Offset = Offset.Zero,
    dropAnimationEnabled: Boolean = false,
    shouldStartDragAndDrop: (state: DraggedItemState<T>) -> Boolean = { true },
    onDragEnter: (state: DraggedItemState<T>) -> Unit = {},
    onDragExit: (state: DraggedItemState<T>) -> Unit = {},
    onDrop: (state: DraggedItemState<T>) -> Unit = {},
): Modifier =
    this then DropTargetNodeElement(
        state = state,
        zIndex = zIndex,
        dropAlignment = dropAlignment,
        dropOffset = dropOffset,
        dropAnimationEnabled = dropAnimationEnabled,
        shouldStartDragAndDrop = shouldStartDragAndDrop,
        onDrop = onDrop,
        onDragEnter = onDragEnter,
        onDragExit = onDragExit,
    )

private data class DropTargetNodeElement<T>(
    val state: DragAndDropState<T>,
    val zIndex: Float,
    val dropAlignment: Alignment,
    val dropOffset: Offset,
    val dropAnimationEnabled: Boolean,
    val shouldStartDragAndDrop: (state: DraggedItemState<T>) -> Boolean,
    val onDrop: (state: DraggedItemState<T>) -> Unit,
    val onDragEnter: (state: DraggedItemState<T>) -> Unit,
    val onDragExit: (state: DraggedItemState<T>) -> Unit,
) : ModifierNodeElement<DropTargetNode<T>>() {
    override fun create(): DropTargetNode<T> =
        DropTargetNode(
            dropTargetState = DropTargetState(
                zIndex = zIndex,
                size = Size.Zero,
                topLeft = Offset.Zero,
                dropAlignment = dropAlignment,
                dropOffset = dropOffset,
                dropAnimationEnabled = dropAnimationEnabled,
                shouldStartDragAndDrop = shouldStartDragAndDrop,
                onDrop = onDrop,
                onDragEnter = onDragEnter,
                onDragExit = onDragExit,
            ),
            state = state,
        )

    override fun update(node: DropTargetNode<T>) {
        node.apply {
            this.state = state
            dropTargetState.zIndex = zIndex
            dropTargetState.dropAlignment = dropAlignment
            dropTargetState.dropOffset = dropOffset
            dropTargetState.dropAnimationEnabled = dropAnimationEnabled
            dropTargetState.shouldStartDragAndDrop = shouldStartDragAndDrop
            dropTargetState.onDrop = onDrop
            dropTargetState.onDragEnter = onDragEnter
            dropTargetState.onDragExit = onDragExit
        }
    }

    override fun InspectorInfo.inspectableProperties() {
        name = "DropTarget"
        properties["state"] = state
        properties["zIndex"] = zIndex
        properties["dropAlignment"] = dropAlignment
        properties["dropOffset"] = dropOffset
        properties["dropAnimationEnabled"] = dropAnimationEnabled
        properties["onDrop"] = onDrop
        properties["onDragEnter"] = onDragEnter
        properties["onDragExit"] = onDragExit
    }
}

private data class DropTargetNode<T>(
    val dropTargetState: DropTargetState<T>,
    var state: DragAndDropState<T>,
) : Modifier.Node(),
    LayoutAwareModifierNode,
    CompositionLocalConsumerModifierNode {
    private var key: Long = -1
    private var isShadow = false

    override fun onAttach() {
        isShadow = currentValueOf(LocalDragAndDropInfo).isShadow

        if (isShadow) {
            return
        }
        key = state.getKey()
        state.addDropTarget(key, dropTargetState)
    }

    override fun onPlaced(coordinates: LayoutCoordinates) {
        if (isShadow || !isAttached) return
        state.addDropTarget(key, dropTargetState)

        val size = coordinates.size.toSize()
        val topLeft = coordinates.positionInRoot()

        // Calculate clipped bounds by intersecting with parents
        var visibleBounds = Rect(topLeft, size)
        var current: LayoutCoordinates? = coordinates.parentLayoutCoordinates
        while (current != null) {
            val parentRect = Rect(current.positionInRoot(), current.size.toSize())
            // Intersection ensures we don't detect drops in clipped areas of scrollable rows
            visibleBounds = visibleBounds.intersect(parentRect)
            if (visibleBounds.isEmpty) break
            current = current.parentLayoutCoordinates
        }

        dropTargetState.size = visibleBounds.size
        dropTargetState.topLeft = visibleBounds.topLeft
    }

    override fun onRemeasured(size: IntSize) {
        if (isShadow) {
            return
        }

        dropTargetState.size = size.toSize()
    }

    override fun onReset() {
        if (isShadow) {
            return
        }

        state.removeDropTarget(key)
    }

    override fun onDetach() {
        if (isShadow) {
            return
        }

        state.removeDropTarget(key)
    }
}
