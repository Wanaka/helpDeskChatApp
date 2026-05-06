package com.example.helpdeskchatapp.ui.admin

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.helpdeskchatapp.domain.viewmodel.AdminViewModel
import com.example.helpdeskchatapp.ui.common.StateHandler
import com.example.helpdeskchatapp.ui.common.components.CommonLazyColumn
import com.example.helpdeskchatapp.ui.model.AdminState
import com.example.helpdeskchatapp.ui.model.ListRowEntity
import androidx.compose.ui.tooling.preview.Preview
import com.example.helpdeskchatapp.theme.MyApplicationTheme

@Composable
fun AdminRoute(
    onNavigateToChat: (Int) -> Unit,
    viewModel: AdminViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    StateHandler(
        uiState = uiState,
        title = "Admin Chats",
        onRetry = { viewModel.loadData() }
    ) { state, paddingValues ->
        AdminScreen(state, paddingValues, onNavigateToChat)
    }
}

@Composable
fun AdminScreen(
    state: AdminState, 
    paddingValues: PaddingValues,
    onNavigateToChat: (Int) -> Unit
) {
    val listItems = state.chats.map { entity ->
        entity.copy(onClick = { onNavigateToChat(entity.id) })
    }

    CommonLazyColumn(
        items = listItems,
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues),
        contentPadding = PaddingValues(bottom = 16.dp)
    )
}

@Preview(showBackground = true)
@Composable
fun AdminScreenPreview() {
    MyApplicationTheme {
        AdminScreen(
            state = AdminState(
                chats = listOf(
                    ListRowEntity(1, "John Doe", "Hello!"),
                    ListRowEntity(2, "Jane Smith", "I have a question.")
                )
            ),
            paddingValues = PaddingValues(0.dp),
            onNavigateToChat = {}
        )
    }
}
