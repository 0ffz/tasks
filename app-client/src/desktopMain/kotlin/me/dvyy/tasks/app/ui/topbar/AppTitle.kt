package me.dvyy.tasks.app.ui.topbar

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import me.dvyy.tasks.app.ui.UI

@Composable
fun AppTitle() {
    Row {
        Spacer(Modifier.width(UI.padding.sm))
        Text(
            "Tasks — ",
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.primary,
            maxLines = 1,
        )
    }
}
