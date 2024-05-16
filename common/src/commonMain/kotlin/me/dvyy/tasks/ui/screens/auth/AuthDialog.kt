package me.dvyy.tasks.ui.screens.auth

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Login
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import io.ktor.client.plugins.auth.providers.*
import kotlinx.coroutines.launch
import me.dvyy.tasks.state.LocalAppState

@Composable
fun AuthDialog() {
    val app = LocalAppState
    val scope = rememberCoroutineScope()
    var error by remember { mutableStateOf(false) }

    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    fun dismiss() {
        app.activeDialog.value = null
        username = ""
        password = ""
    }
    AlertDialog(
        onDismissRequest = { dismiss() },
        icon = { Icon(Icons.AutoMirrored.Outlined.Login, contentDescription = "Login icon") },
        title = { Text("Login") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Please login to the sync server.")
                TextField(
                    username,
                    isError = error,
                    singleLine = true,
                    onValueChange = {
                        error = false
                        username = it
                    },
                    label = { Text("Username") }
                )
                TextField(
                    password,
                    isError = error,
                    singleLine = true,
                    onValueChange = {
                        error = false
                        password = it
                    },
                    visualTransformation = PasswordVisualTransformation(),
                    supportingText = { if (error) Text("Incorrect username or password") },
                    label = { Text("Password") }
                )
            }
        },
        confirmButton = {
            TextButton(onClick = {
                scope.launch {
                    if (app.sync.checkAuth(DigestAuthCredentials(username, password))) {
                        app.auth.setAuth(username, password)
                        app.sync.updateAuth()
                        dismiss()
                        return@launch
                    }
                    error = true
                }
            }) { Text("Login") }
        },
        dismissButton = {
            TextButton(onClick = { dismiss() }) { Text("Dismiss") }
        }
    )
}
