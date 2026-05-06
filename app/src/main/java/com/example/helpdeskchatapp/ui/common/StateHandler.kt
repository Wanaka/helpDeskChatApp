package com.example.helpdeskchatapp.ui.common

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun <T> StateHandler(
    uiState: UiState<T>,
    title: String,
    canNavigateBack: Boolean = false,
    onBackClick: () -> Unit = {},
    onRetry: (() -> Unit)? = null,
    content: @Composable (T, PaddingValues) -> Unit,
) {
    Scaffold(
        topBar = {
            NavToolbar(
                title = title,
                canNavigateBack = canNavigateBack,
                onBackClick = onBackClick
            )
        }
    ) { paddingValues ->
        AnimatedContent(
            targetState = uiState,
            label = "state_transition",
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

                is UiState.Success -> {
                    content(state.data, paddingValues)
                }
            }
        }
    }
}
