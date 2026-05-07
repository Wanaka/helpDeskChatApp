package com.example.helpdeskchatapp.ui.admin

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.helpdeskchatapp.domain.viewmodel.AdminViewModel
import com.example.helpdeskchatapp.theme.MyApplicationTheme
import com.example.helpdeskchatapp.ui.common.StateHandler
import com.example.helpdeskchatapp.ui.common.components.CommonButton
import com.example.helpdeskchatapp.ui.common.components.CommonLazyColumn
import com.example.helpdeskchatapp.ui.model.AdminState
import com.example.helpdeskchatapp.ui.model.ListRowEntity

@Composable
fun AdminRoute(
    onNavigateToChat: (String) -> Unit,
    onLogout: () -> Unit,
    viewModel: AdminViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    StateHandler(
        uiState = uiState,
        title = "Admin Chats",
        onRetry = { viewModel.loadData() },
        content = {},
        staticContent = { state, paddingValues ->
            AdminScreen(
                state = state,
                paddingValues = paddingValues,
                onNavigateToChat = onNavigateToChat,
                onLogout = {
                    viewModel.logout(onLogout)
                }
            )
        },
    )
}

@Composable
fun AdminScreen(
    state: AdminState,
    paddingValues: PaddingValues,
    onNavigateToChat: (String) -> Unit,
    onLogout: () -> Unit
) {
    val listItems = state.chats.map { entity ->
        entity.copy(onClick = { onNavigateToChat(entity.id) })
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
    ) {
        CommonLazyColumn(
            items = listItems,
            modifier = Modifier.weight(1f),
            contentPadding = PaddingValues(bottom = 16.dp)
        )

        CommonButton(
            text = "Logout",
            onClick = onLogout,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun AdminScreenPreview() {
    MyApplicationTheme {
        AdminScreen(
            state = AdminState(
                chats = listOf(
                    ListRowEntity("1", "John Doe", "Hello!"),
                    ListRowEntity("2", "Jane Smith", "I have a question.")
                )
            ),
            paddingValues = PaddingValues(0.dp),
            onNavigateToChat = {},
            onLogout = {}
        )
    }
}
