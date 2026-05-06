package com.example.helpdeskchatapp.ui.chat

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.helpdeskchatapp.domain.model.ChatMessage
import com.example.helpdeskchatapp.domain.viewmodel.ChatViewModel
import com.example.helpdeskchatapp.ui.common.StateHandler
import com.example.helpdeskchatapp.ui.model.ChatState

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
        title = "Chat",
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
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(state.messages) { message ->
                val isFromMe = message.senderId == state.currentUserId
                MessageBubble(message, isFromMe)
            }
        }
    }
}

@Composable
fun MessageBubble(message: ChatMessage, isFromMe: Boolean) {
    val alignment = if (isFromMe) Alignment.CenterEnd else Alignment.CenterStart
    val color = if (isFromMe) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.secondaryContainer
    val contentColor = if (isFromMe) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSecondaryContainer
    val shape = if (isFromMe) {
        RoundedCornerShape(16.dp, 16.dp, 4.dp, 16.dp)
    } else {
        RoundedCornerShape(16.dp, 16.dp, 16.dp, 4.dp)
    }

    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = alignment
    ) {
        Surface(
            color = color,
            contentColor = contentColor,
            shape = shape,
            modifier = Modifier.widthIn(max = 280.dp)
        ) {
            Text(
                text = message.text,
                modifier = Modifier.padding(12.dp),
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}
