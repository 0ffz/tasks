package me.dvyy.tasks.ui.elements.week

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.input.key.*
import androidx.compose.ui.unit.dp

@Composable
fun Task(
    name: String,
    onNameChange: (String) -> Unit,
    highlight: Color = Color.Transparent,
    onTab: () -> Unit,
) {
    Surface(
        color = highlight,
        modifier = Modifier.padding(4.dp),
        shape = MaterialTheme.shapes.large
    ) {
        Row {
            BasicTextField(
                name,
                singleLine = true,
                onValueChange = onNameChange,
                cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
                textStyle = MaterialTheme.typography.bodySmall
                    .copy(color = MaterialTheme.colorScheme.onPrimaryContainer),
                modifier = Modifier.fillMaxWidth()
                    .padding(vertical = 8.dp, horizontal = 16.dp)
                    .onKeyEvent { event ->
                        if (event.isCtrlPressed && event.key == Key.E && event.type == KeyEventType.KeyDown) {
                            onTab()
                            true
                        } else false
                    }
            )
        }
    }
}
