package com.example.helpdeskchatapp.ui.admin

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.helpdeskchatapp.domain.model.ChatInfo
import com.example.helpdeskchatapp.domain.viewmodel.AdminViewModel
import com.example.helpdeskchatapp.ui.common.StateHandler
import com.example.helpdeskchatapp.ui.model.AdminState

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
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues),
        contentPadding = PaddingValues(bottom = 16.dp)
    ) {
        items(state.chats) { chat ->
            ChatItem(chat, onClick = { onNavigateToChat(chat.id) })
            HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
        }
    }
}

@Composable
fun ChatItem(chat: ChatInfo, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Default.AccountCircle,
            contentDescription = null,
            modifier = Modifier.size(40.dp),
            tint = MaterialTheme.colorScheme.primary
        )
        
        Spacer(modifier = Modifier.width(16.dp))
        
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = chat.senderName,
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = chat.lastMessage,
                style = MaterialTheme.typography.bodySmall,
                maxLines = 1
            )
        }
        
        Icon(
            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
            contentDescription = "Open chat",
            tint = MaterialTheme.colorScheme.outline
        )
    }
}
