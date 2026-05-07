package com.example.helpdeskchatapp.ui.model

import androidx.compose.ui.graphics.vector.ImageVector

data class ListRowEntity(
    val id: String = "",
    val title: String,
    val subtitle: String? = null,
    val leftIcon: ImageVector? = null,
    val showLeftIcon: Boolean = false,
    val rightIcon: ImageVector? = null,
    val showRightIcon: Boolean = false,
    val isChatLayout: Boolean = false,
    val onClick: () -> Unit = {}
)
