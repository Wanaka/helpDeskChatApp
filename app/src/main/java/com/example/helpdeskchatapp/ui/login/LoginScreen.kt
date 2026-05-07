package com.example.helpdeskchatapp.ui.login

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.helpdeskchatapp.domain.model.LoginParams
import com.example.helpdeskchatapp.domain.viewmodel.LoginViewModel
import com.example.helpdeskchatapp.theme.MyApplicationTheme
import com.example.helpdeskchatapp.ui.common.StateHandler
import com.example.helpdeskchatapp.ui.common.components.CommonButton
import com.example.helpdeskchatapp.ui.common.components.CommonHeader
import com.example.helpdeskchatapp.ui.common.components.CommonInputTextField
import com.example.helpdeskchatapp.ui.model.LoginState

@Composable
fun LoginRoute(
    onNavigateToAdmin: () -> Unit,
    onNavigateToRegister: () -> Unit,
    viewModel: LoginViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    StateHandler(
        uiState = uiState,
        title = "Helpdesk Chat App",
        onRetry = { viewModel.loadData() },
        content = {},
        staticContent = { state, paddingValues ->
            LoginScreen(
                state = state,
                paddingValues = paddingValues,
                onLogin = viewModel::login,
                onNavigateToAdmin = onNavigateToAdmin,
                onNavigateToRegister = onNavigateToRegister
            )
        },
    )
}

@Composable
fun LoginScreen(
    state: LoginState,
    paddingValues: PaddingValues,
    onLogin: (LoginParams) -> Unit,
    onNavigateToAdmin: () -> Unit,
    onNavigateToRegister: () -> Unit
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
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        CommonHeader("Login as Admin")

        Spacer(modifier = Modifier.height(20.dp))

        CommonInputTextField(
            value = name,
            onValueChange = { name = it },
            label = "Email",
        )

        Spacer(modifier = Modifier.height(8.dp))

        CommonInputTextField(
            value = password,
            onValueChange = { password = it },
            label = "Password",
            isPasswordField = true
        )

        Spacer(modifier = Modifier.height(40.dp))

        CommonButton(
            text = "Login",
            onClick = { onLogin(LoginParams(name, password)) }
        )

        Spacer(modifier = Modifier.height(16.dp))

        TextButton(
            onClick = onNavigateToRegister
        ) {
            Text("Don't have an account? Register")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LoginScreenPreview() {
    MyApplicationTheme {
        LoginScreen(
            state = LoginState(),
            paddingValues = PaddingValues(0.dp),
            onLogin = {},
            onNavigateToAdmin = {},
            onNavigateToRegister = {}
        )
    }
}

