package com.example.helpdeskchatapp.ui.register

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
import com.example.helpdeskchatapp.domain.viewmodel.RegisterViewModel
import com.example.helpdeskchatapp.theme.MyApplicationTheme
import com.example.helpdeskchatapp.ui.common.StateHandler
import com.example.helpdeskchatapp.ui.common.components.CommonButton
import com.example.helpdeskchatapp.ui.common.components.CommonHeader
import com.example.helpdeskchatapp.ui.common.components.CommonInputTextField
import com.example.helpdeskchatapp.ui.model.LoginState

@Composable
fun RegisterRoute(
    onNavigateToAdmin: () -> Unit,
    onNavigateToLogin: () -> Unit,
    viewModel: RegisterViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    StateHandler(
        uiState = uiState,
        title = "Create Account",
        onRetry = { viewModel.loadData() }
    ) { state, paddingValues ->
        RegisterScreen(
            state = state,
            paddingValues = paddingValues,
            onRegister = viewModel::register,
            onNavigateToLogin = onNavigateToLogin,
            onNavigateToAdmin = onNavigateToAdmin
        )
    }
}

@Composable
fun RegisterScreen(
    state: LoginState,
    paddingValues: PaddingValues,
    onRegister: (LoginParams) -> Unit,
    onNavigateToLogin: () -> Unit,
    onNavigateToAdmin: () -> Unit
) {
    var email by remember { mutableStateOf("") }
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

        CommonHeader("Register Admin")

        Spacer(modifier = Modifier.height(20.dp))

        CommonInputTextField(
            value = email,
            onValueChange = { email = it },
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
            text = "Create Account",
            onClick = { onRegister(LoginParams(email, password)) }
        )

        Spacer(modifier = Modifier.height(16.dp))

        TextButton(onClick = onNavigateToLogin) {
            Text("Already have an account? Login")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun RegisterScreenPreview() {
    MyApplicationTheme {
        RegisterScreen(
            state = LoginState(),
            paddingValues = PaddingValues(0.dp),
            onRegister = {},
            onNavigateToLogin = {},
            onNavigateToAdmin = {}
        )
    }
}
