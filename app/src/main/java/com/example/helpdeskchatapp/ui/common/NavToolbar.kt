package com.example.helpdeskchatapp.ui.common

import androidx.compose.foundation.layout.RowScope
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import com.example.helpdeskchatapp.ui.common.components.NavBackButton
import com.example.helpdeskchatapp.ui.common.components.NavToolbarTitle

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NavToolbar(
    title: String,
    subtitle: String? = null,
    avatarInitials: String? = null,
    canNavigateBack: Boolean = false,
    onBackClick: () -> Unit = {},
    actions: @Composable RowScope.() -> Unit = {}
) {
    TopAppBar(
        title = {
            NavToolbarTitle(
                title = title,
                subtitle = subtitle,
                avatarInitials = avatarInitials
            )
        },
        navigationIcon = {
            if (canNavigateBack) NavBackButton(onClick = onBackClick)
        },
        actions = actions,
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primary,
            scrolledContainerColor = MaterialTheme.colorScheme.primary,
            navigationIconContentColor = MaterialTheme.colorScheme.onPrimary,
            titleContentColor = MaterialTheme.colorScheme.onPrimary,
            actionIconContentColor = MaterialTheme.colorScheme.onPrimary
        )
    )
}
