package me.dvyy.tasks.auth.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Login
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import me.dvyy.tasks.app.ui.DialogViewModel
import me.dvyy.tasks.auth.ui.LoginState.Error
import me.dvyy.tasks.di.koinViewModel
import me.dvyy.tasks.sync.data.SyncClient

@Composable
fun AuthDialog(
    dialogs: DialogViewModel = koinViewModel(),
    auth: AuthViewModel = koinViewModel()
) {
    val scope = rememberCoroutineScope()
    var error by remember { mutableStateOf(false) }

    var serverUrl by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val loginState by auth.loginState.collectAsState()

    fun dismiss() {
        dialogs.dismiss()
    }
    AlertDialog(
        onDismissRequest = { dismiss() },
        icon = { Icon(Icons.AutoMirrored.Outlined.Login, contentDescription = "Login icon") },
        title = { Text("Login") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text("Please login to the sync server.")
                TextField(
                    serverUrl,
                    isError = loginState is Error.Connection,
                    singleLine = true,
                    onValueChange = {
                        error = false
                        serverUrl = it
                    },
                    supportingText = {
                        if (loginState is Error.Connection)
                            Text("Connection error")
                    },
                    placeholder = { Text("https://") },
                    label = { Text("Server URL") }
                )
                TextField(
                    username,
                    isError = loginState is Error.InvalidCredentials,
                    singleLine = true,
                    onValueChange = {
                        error = false
                        username = it
                    },
                    supportingText = {
                        if (loginState is Error.InvalidCredentials)
                            Text("Incorrect username or password")
                    },
                    label = { Text("Username") }
                )
                TextField(
                    password,
                    isError = loginState is Error.InvalidCredentials,
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
                    if (auth.login(serverUrl, username, password) == SyncClient.AuthResult.SUCCESS)
                        dismiss()
                }
            }) {
                Text("Login")
            }
        },
        dismissButton = {
            TextButton(onClick = { dismiss() }) { Text("Dismiss") }
        }
    )
}
