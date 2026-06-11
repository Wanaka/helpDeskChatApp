package com.example.helpdeskchatapp.ui.admin

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import com.example.helpdeskchatapp.ui.common.components.NameEntryDialog
import com.example.helpdeskchatapp.ui.common.components.QrCodeDialog
import com.example.helpdeskchatapp.ui.model.ListRowEntity
import com.example.helpdeskchatapp.util.CurrentUserId

@Composable
fun AdminRoute(
    onNavigateToChat: (String) -> Unit,
    onLogout: () -> Unit,
    viewModel: AdminViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val chats by viewModel.chats.collectAsStateWithLifecycle()
    val showNameOverlay by viewModel.showNameOverlay.collectAsStateWithLifecycle()
    var showQrCode by remember { mutableStateOf(false) }

    if (showNameOverlay) {
        NameEntryDialog(
            onConfirm = viewModel::updateName
        )
    }

    StateHandler(
        uiState = uiState,
        title = "Admin Chats",
        onRetry = { viewModel.loadData() },
        actions = {
            IconButton(onClick = { showQrCode = true }) {
                Icon(imageVector = Icons.Default.QrCode, contentDescription = "Show QR Code")
            }
        },
        content = { paddingValues ->
            AdminScreen(
                chats = chats,
                paddingValues = paddingValues,
                onNavigateToChat = onNavigateToChat,
                onLogout = {
                    viewModel.logout(onLogout)
                }
            )

        },
    )

    if (showQrCode) {
        QrCodeDialog(
            adminId = CurrentUserId.CURRENT_USER_ID,
            onDismiss = { showQrCode = false }
        )
    }
}


@Composable
fun AdminScreen(
    chats: List<ListRowEntity>,
    paddingValues: PaddingValues,
    onNavigateToChat: (String) -> Unit,
    onLogout: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
    ) {
        CommonLazyColumn(
            items = chats,
            modifier = Modifier.weight(1f),
            contentPadding = PaddingValues(bottom = 16.dp),
            onItemClick = { onNavigateToChat(it.id) }
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
            chats = listOf(
                ListRowEntity("1", "John Doe", "Hello!"),
                ListRowEntity("2", "Jane Smith", "I have a question.")
            ),
            paddingValues = PaddingValues(0.dp),
            onNavigateToChat = {},
            onLogout = {}
        )
    }
}
