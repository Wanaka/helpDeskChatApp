package com.example.helpdeskchatapp.ui.chat

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.rememberLazyListState
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
import com.example.helpdeskchatapp.domain.model.consumer.Message
import com.example.helpdeskchatapp.domain.viewmodel.ChatViewModel
import com.example.helpdeskchatapp.theme.MyApplicationTheme
import com.example.helpdeskchatapp.ui.common.ScrollToBottomOnChange
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
        subtitle = chatTitleData.company.takeIf { it.isNotBlank() },
        canNavigateBack = canNavigateBack,
        onBackClick = onBack,
        onRetry = { viewModel.loadData() },
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
    listState.ScrollToBottomOnChange(messages.size)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .consumeWindowInsets(paddingValues)
            .imePadding()
    ) {
        CommonLazyColumn(
            items = messages,
            modifier = Modifier.weight(1f),
            contentPadding = PaddingValues(16.dp),
            showDividers = false,
            verticalArrangement = Arrangement.spacedBy(8.dp),
            state = listState
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            CommonInputTextField(
                value = message,
                onValueChange = { message = it },
                label = "Message",
                modifier = Modifier.weight(1f)
            )
            CommonButton(
                text = "Send",
                onClick = {
                    if (message.isNotBlank()) {
                        sendMessage(Message(message = message))
                        message = ""
                    }
                },
                modifier = Modifier
                    .wrapContentWidth()
                    .height(56.dp)
            )
        }
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
