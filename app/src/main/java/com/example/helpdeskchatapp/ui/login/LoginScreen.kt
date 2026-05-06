package com.example.helpdeskchatapp.ui.login

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.helpdeskchatapp.domain.model.LoginParams
import com.example.helpdeskchatapp.domain.viewmodel.LoginViewModel
import com.example.helpdeskchatapp.ui.common.StateHandler
import com.example.helpdeskchatapp.ui.model.LoginState

@Composable
fun LoginRoute(
    onNavigateToAdmin: () -> Unit,
    viewModel: LoginViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    StateHandler(
        uiState = uiState,
        title = "Login",
        onRetry = { viewModel.loadData() }
    ) { state, paddingValues ->
        LoginScreen(
            state = state,
            paddingValues = paddingValues,
            onLogin = viewModel::login,
            onNavigateToAdmin = onNavigateToAdmin
        )
    }
}

@Composable
fun LoginScreen(
    state: LoginState,
    paddingValues: PaddingValues,
    onLogin: (LoginParams) -> Unit,
    onNavigateToAdmin: () -> Unit
) {
    var name by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    LaunchedEffect(state.loginResult) {
        if (state.loginResult != null) {
            onNavigateToAdmin()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Login to HelpDesk")
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = { onLogin(LoginParams(name, password)) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Login")
        }
    }
}
