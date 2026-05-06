package com.example.helpdeskchatapp.ui.chat

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.helpdeskchatapp.domain.viewmodel.ChatViewModel
import com.example.helpdeskchatapp.theme.MyApplicationTheme
import com.example.helpdeskchatapp.ui.common.StateHandler
import com.example.helpdeskchatapp.ui.common.components.CommonLazyColumn
import com.example.helpdeskchatapp.ui.model.ChatState
import com.example.helpdeskchatapp.ui.model.ListRowEntity

@Composable
fun ChatRoute(
    conversationId: Int,
    onBack: () -> Unit,
    viewModel: ChatViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(conversationId) {
        viewModel.initConversation(conversationId)
    }

    StateHandler(
        uiState = uiState,
        title = "Chat $conversationId",
        canNavigateBack = true,
        onBackClick = onBack,
        onRetry = { viewModel.loadData() }
    ) { state, paddingValues ->
        ChatScreen(state, paddingValues)
    }
}

@Composable
fun ChatScreen(state: ChatState, paddingValues: PaddingValues) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
    ) {
        CommonLazyColumn(
            items = state.messages,
            modifier = Modifier.weight(1f),
            contentPadding = PaddingValues(16.dp),
            showDividers = false,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ChatScreenPreview() {
    MyApplicationTheme {
        ChatScreen(
            state = ChatState(
                messages = listOf(
                    ListRowEntity(
                        id = 1,
                        title = "Hello!",
                        showLeftIcon = true,
                        isChatLayout = true
                    ),
                    ListRowEntity(
                        id = 2,
                        title = "Hi there, how can I help you?",
                        showRightIcon = true,
                        isChatLayout = true
                    )
                ),
//                currentUserId = "admin_1"
            ),
            paddingValues = PaddingValues(0.dp)
        )
    }
}
