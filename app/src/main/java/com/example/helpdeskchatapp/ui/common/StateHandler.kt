package com.example.helpdeskchatapp.ui.common

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun StateHandler(
    uiState: UiState,
    title: String,
    subtitle: String? = null,
    avatarInitials: String? = null,
    avatarRes: Int? = null,
    canNavigateBack: Boolean = false,
    onBackClick: () -> Unit = {},
    onRetry: (() -> Unit)? = null,
    actions: @Composable RowScope.() -> Unit = {},
    content: @Composable (PaddingValues) -> Unit,
) {
    Scaffold(
        containerColor = MaterialTheme.colorScheme.primary,
        topBar = {
            NavToolbar(
                title = title,
                subtitle = subtitle,
                avatarInitials = avatarInitials,
                avatarRes = avatarRes,
                canNavigateBack = canNavigateBack,
                onBackClick = onBackClick,
                actions = actions
            )
        }
    ) { paddingValues ->
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = paddingValues.calculateTopPadding()),
            shape = RoundedCornerShape(topStart = 25.dp, topEnd = 25.dp),
            color = MaterialTheme.colorScheme.background,
            border = BorderStroke(
                width = 1.dp,
                color = MaterialTheme.colorScheme.onPrimary
            )
        ) {
            AnimatedContent(
                targetState = uiState,
                modifier = Modifier.fillMaxSize()
            ) { state ->
                when (state) {
                    is UiState.Loading -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    }

                    is UiState.Error -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Text(
                                    text = state.message,
                                    color = MaterialTheme.colorScheme.error
                                )
                                onRetry?.let {
                                    Button(onClick = it) {
                                        Text("Retry")
                                    }
                                }
                            }
                        }
                    }

                    UiState.Success -> {
                        content(PaddingValues(bottom =  paddingValues.calculateBottomPadding()))
                    }
                }
            }
        }
    }
}
