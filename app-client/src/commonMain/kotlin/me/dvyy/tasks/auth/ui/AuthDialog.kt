package me.dvyy.tasks.auth.ui

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.outlined.Link
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import me.dvyy.tasks.app.AppIcons
import me.dvyy.tasks.auth.ui.LoginState.Error
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun AuthDialog(
    auth: AuthViewModel = koinViewModel(),
) {
    rememberCoroutineScope()
    var error by remember { mutableStateOf(false) }

    var serverUrl by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val loginState by auth.loginState.collectAsState()

//    Icon(Icons.AutoMirrored.Outlined.Login, contentDescription = "Login icon")
    OutlinedTextField(
        serverUrl,
        isError = loginState is Error.Connection,
        singleLine = true,
        leadingIcon = { Icon(AppIcons.Link, contentDescription = "Login icon") },
        onValueChange = {
            error = false
            serverUrl = it
        },
        supportingText = {
            if (loginState is Error.Connection)
                Text("Connection error")
        },
        modifier = Modifier.fillMaxWidth(),
        label = { Text("Server URL") }
    )
    OutlinedTextField(
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
        modifier = Modifier.fillMaxWidth(),
        label = { Text("Username") }
    )
    OutlinedTextField(
        password,
        isError = loginState is Error.InvalidCredentials,
        singleLine = true,
        onValueChange = {
            error = false
            password = it
        },
        modifier = Modifier.fillMaxWidth(),
        visualTransformation = PasswordVisualTransformation(),
        supportingText = { if (error) Text("Incorrect username or password") },
        label = { Text("Password") }
    )
//    FilledTonalButton(onClick = {
//        scope.launch {
//            if (auth.login(serverUrl, username, password) is AuthResult.Success)
//                dismiss()
//        }
//    }) {
//        Icon(Icons.AutoMirrored.Outlined.Login, contentDescription = "Switch account")
//        Text(text = "Login")
//    }
//    TextButton(onClick = { dismiss() }) { Text("Dismiss") }
}
