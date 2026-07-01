package com.example.helpdeskchatapp.ui.model

import androidx.compose.ui.graphics.vector.ImageVector

data class ListRowEntity(
    val id: String,
    val title: String,
    val secondSubtitle: String? = null,
    val thirdSubtitle: String? = null,
    val leftIcon: ImageVector? = null,
    val rightIcon: ImageVector? = null,
    val isChatLayout: Boolean = false,
    val isFromMe: Boolean = false,
    val showBadge: Boolean = false
)
