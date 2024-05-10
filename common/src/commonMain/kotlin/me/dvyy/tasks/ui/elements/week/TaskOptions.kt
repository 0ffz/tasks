package me.dvyy.tasks.ui.elements.week

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.material3.SheetValue.Hidden
import androidx.compose.material3.SheetValue.PartiallyExpanded
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.flow.filterNotNull
import me.dvyy.tasks.state.LocalAppState
import me.dvyy.tasks.state.TaskState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskOptions(task: TaskState?) = Box(Modifier) {
    val app = LocalAppState
    val sheetState = rememberBottomSheetScaffoldState(
        bottomSheetState = rememberStandardBottomSheetState(
            skipHiddenState = false,
        )
    )
    Box {
        val currentState = sheetState.bottomSheetState.currentValue
        LaunchedEffect(currentState) {
            when (currentState) {
                Hidden, PartiallyExpanded -> {
                    app.selectedTask.value = null
                }

                else -> {}
            }
        }
    }
    BottomSheetScaffold(
        sheetTonalElevation = 2.dp,
        sheetPeekHeight = 0.dp,
        sheetContent = {
            BoxWithConstraints(
                Modifier
                    .navigationBarsPadding()
                    .imePadding()
                    .padding(bottom = 10.dp)
            ) {
                Box(Modifier.padding(horizontal = 10.dp)) {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        val rememberedTask by snapshotFlow { task }.filterNotNull().collectAsState(task)
                        if (rememberedTask == null) return@Box
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text("Highlight:", style = MaterialTheme.typography.titleMedium)
                            HighlightButton(rememberedTask!!, Highlight.Unmarked)
                            HighlightButton(rememberedTask!!, Highlight.Important)
                            HighlightButton(rememberedTask!!, Highlight.InProgress)
                        }
                    }
                }
                // Content here
            }
        },
        scaffoldState = sheetState,
    ) { }
}

@Composable
fun HighlightButton(task: TaskState, highlight: Highlight) {
    OutlinedButton(
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = highlight.color,
        ),
        modifier = Modifier.size(25.dp),
        border = BorderStroke(2.dp, MaterialTheme.colorScheme.onSurface),
        shape = CircleShape,
        onClick = {
            task.highlight.value = highlight
        },
    ) { }
}

@Composable
fun keyboardAsState(): State<Boolean> {
    val isImeVisible = WindowInsets.ime.getBottom(LocalDensity.current) > 0
    return rememberUpdatedState(isImeVisible)
}
