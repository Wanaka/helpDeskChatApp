package com.example.helpdeskchatapp.ui.chat

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.helpdeskchatapp.domain.model.consumer.Message
import com.example.helpdeskchatapp.domain.viewmodel.ChatViewModel
import com.example.helpdeskchatapp.theme.MyApplicationTheme
import com.example.helpdeskchatapp.ui.common.StateHandler
import com.example.helpdeskchatapp.ui.common.components.CommonButton
import com.example.helpdeskchatapp.ui.common.components.CommonInputTextField
import com.example.helpdeskchatapp.ui.common.components.CommonLazyColumn
import com.example.helpdeskchatapp.ui.common.composeContext
import com.example.helpdeskchatapp.ui.model.ListRowEntity

@Composable
fun ChatRoute(
    conversationId: String,
    onBack: () -> Unit,
    canNavigateBack: Boolean = true,
    viewModel: ChatViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val messages by viewModel.messages.collectAsStateWithLifecycle()
    val chatTitleData by viewModel.chatTitle.collectAsStateWithLifecycle()
    val context = composeContext()

    LaunchedEffect(viewModel.toastEvent) {
        viewModel.toastEvent.collect { message ->
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        }
    }

    LaunchedEffect(conversationId) {
        viewModel.initConversation(conversationId)
    }

    StateHandler(
        uiState = uiState,
        title = chatTitleData.name,
        subtitle = chatTitleData.company,
        canNavigateBack = canNavigateBack,
        onBackClick = onBack,
        onRetry = { viewModel.loadData() },
        staticContent = { _, _ -> },
        content = { paddingValues ->
            ChatScreen(
                messages,
                paddingValues,
                sendMessage = viewModel::sendMessage
            )
        }
    )
}

@Composable
fun ChatScreen(
    messages: List<ListRowEntity>,
    paddingValues: PaddingValues,
    sendMessage: (Message) -> Unit,
) {
    var message by remember { mutableStateOf("") }
    val listState = rememberLazyListState()

    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
    ) {
        CommonLazyColumn(
            items = messages,
            modifier = Modifier.weight(1f),
            contentPadding = PaddingValues(16.dp),
            showDividers = false,
            verticalArrangement = Arrangement.spacedBy(8.dp),
            state = listState
        )

        CommonInputTextField(
            value = message,
            onValueChange = { message = it },
            label = "Message",
        )

        CommonButton(
            text = "Send",
            onClick = {
                if (message.isNotBlank()) {
                    sendMessage(Message(message = message))
                    message = ""
                }
            }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ChatScreenPreview() {
    MyApplicationTheme {
        ChatScreen(
            paddingValues = PaddingValues(0.dp),
            sendMessage = {},
            messages = emptyList()
        )
    }
}
