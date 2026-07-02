package com.example.helpdeskchatapp.ui.chat

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import com.example.helpdeskchatapp.theme.Dimens
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.helpdeskchatapp.R
import com.example.helpdeskchatapp.domain.model.consumer.Message
import com.example.helpdeskchatapp.domain.viewmodel.ChatViewModel
import com.example.helpdeskchatapp.theme.MyApplicationTheme
import com.example.helpdeskchatapp.ui.common.ScrollToBottomOnChange
import com.example.helpdeskchatapp.ui.common.StateHandler
import com.example.helpdeskchatapp.ui.common.components.ChatInputField
import com.example.helpdeskchatapp.ui.common.components.CommonLazyColumn
import com.example.helpdeskchatapp.ui.common.composeContext
import com.example.helpdeskchatapp.ui.model.ListRowEntity
import com.example.helpdeskchatapp.util.toInitials

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
    val isAnonymous by viewModel.isAnonymous.collectAsStateWithLifecycle()
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
        avatarRes = if (isAnonymous) R.drawable.avatar_qr else null,
        avatarInitials = if (!isAnonymous) chatTitleData.name.toInitials() else null,
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
            contentPadding = PaddingValues(Dimens.dp16),
            showDividers = false,
            verticalArrangement = Arrangement.spacedBy(Dimens.dp8),
            state = listState
        )

        HorizontalDivider(
            modifier = Modifier.padding(horizontal = Dimens.dp16),
            color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = Dimens.dp12, vertical = Dimens.dp10),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(Dimens.dp10)
        ) {
            ChatInputField(
                value = message,
                onValueChange = { message = it },
                modifier = Modifier.weight(1f)
            )

            Box(
                modifier = Modifier
                    .size(Dimens.dp52)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary),
                contentAlignment = Alignment.Center
            ) {
                IconButton(
                    onClick = {
                        if (message.isNotBlank()) {
                            sendMessage(Message(message = message))
                            message = ""
                        }
                    }
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Send,
                        contentDescription = "Send",
                        tint = MaterialTheme.colorScheme.onPrimary
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ChatScreenPreview() {
    MyApplicationTheme {
        ChatScreen(
            paddingValues = PaddingValues(Dimens.dp0),
            sendMessage = {},
            messages = emptyList()
        )
    }
}
