package me.dvyy.tasks.ui.elements.week

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.mohamedrejeb.compose.dnd.DragAndDropContainer
import com.mohamedrejeb.compose.dnd.drag.DraggableItem
import com.mohamedrejeb.compose.dnd.drop.dropTarget
import com.mohamedrejeb.compose.dnd.rememberDragAndDropState
import com.mohamedrejeb.compose.dnd.reorder.ReorderContainer
import com.mohamedrejeb.compose.dnd.reorder.ReorderableItem
import com.mohamedrejeb.compose.dnd.reorder.rememberReorderState
import kotlinx.datetime.LocalDate

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun DayList(
    date: LocalDate,
    isToday: Boolean,
    height: Int,
) {
    val dragAndDropState = rememberDragAndDropState<String>()
    val reorderState = rememberReorderState<String>()
//    DragAndDropContainer(state = dragAndDropState) {
    ReorderContainer(state = reorderState) {
        Column(Modifier.fillMaxHeight().fillMaxWidth()) {
            DayTitle(date, isToday)
            LazyColumn(Modifier.padding(16.dp), userScrollEnabled = false) {
                items(height, key = { it }) {
                    Timeslot {
                        var taskName by remember { mutableStateOf("Tast $it") }
                        var highlight by remember { mutableStateOf(Highlights.Unmarked) }
                        val highlightAnimate by animateColorAsState(highlight.color)

//                    var offsetX by remember { mutableStateOf(0f) }
//                    var offsetY by remember { mutableStateOf(0f) }
                        ReorderableItem(
                            state = reorderState,
//                        DraggableItem(
//                            state = dragAndDropState,
                            key = it,
                            data = taskName,
                            modifier = Modifier.dropTarget(
                                state = dragAndDropState,
                                key = it, // Unique key for each drop target
                                onDrop = { state -> // Data passed from the draggable item
                                    println("Dropped $state")
                                    // Handle drop
                                }
                            )
                        ) {
                            Box(
                                Modifier
                                /*.offset { IntOffset(offsetX.roundToInt(), offsetY.roundToInt()) }
                                .pointerInput(Unit) {
                                    detectDragGestures { change, dragAmount ->
                                        change.consume()
                                        offsetX += dragAmount.x
                                        offsetY += dragAmount.y
                                    }
                                }*/
                            ) {
                                Task(
                                    taskName,
                                    onNameChange = { taskName = it },
                                    highlight = highlightAnimate,
                                    onTab = {
                                        highlight =
                                            Highlights.entries[(highlight.ordinal + 1) % Highlights.entries.size]
                                    },
                                )
                            }
                        }
                    }
                    HorizontalDivider()
                }
            }
        }
    }
}

enum class Highlights(
    val color: Color
) {
    Unmarked(Color.Transparent),
    Important(Color.Red.copy(alpha = 0.5f)),
    InProgress(Color.Yellow.copy(alpha = 0.5f)),
//    Done(Color.Green.copy(alpha = 0.5f)),
}

@Composable
fun DayTitle(date: LocalDate, isToday: Boolean) {
    val color =
        if (isToday) MaterialTheme.colorScheme.tertiary
        else MaterialTheme.colorScheme.onPrimaryContainer
    Row(
        Modifier.padding(12.dp),
        verticalAlignment = Alignment.Bottom,
    ) {
        Text(
            "${date.month.name.lowercase().capitalize()} ${date.dayOfMonth}",
            Modifier.weight(1f, true),
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = color,
        )
        Text(
            date.dayOfWeek.name.lowercase().capitalize().take(3),
            style = MaterialTheme.typography.headlineSmall,
            color = color.copy(alpha = 0.6f)
        )
    }
    HorizontalDivider(
        thickness = 2.dp,
        color = color
    )
}


fun Int.toTwoDigitString(): String {
    return if (this < 10) "0$this" else this.toString()
}
