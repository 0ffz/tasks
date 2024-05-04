package me.dvyy.tasks.ui.elements.week

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun Task(
    name: String,
) {
    Box(Modifier.padding(4.dp)) {
        var field by remember { mutableStateOf("") }
        TextField(
            field,
            onValueChange = { field = it },
            colors = TextFieldDefaults.colors(
                unfocusedContainerColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
            ),
            modifier = Modifier.height(18.dp).padding(0.dp),

            shape = MaterialTheme.shapes.extraSmall,
        )
    }
}
