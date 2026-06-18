package com.example.helpdeskchatapp.domain.mapper

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.AccountCircle
import com.example.helpdeskchatapp.domain.model.producer.ChatViewEntity
import com.example.helpdeskchatapp.ui.model.ListRowEntity

fun ChatViewEntity.toListRowEntity(): ListRowEntity {
    val title = if (company.isNotBlank()) "$sender | $company" else sender
    val preview = message.take(40).let { if (message.length > 40) "$it…" else it }
    return ListRowEntity(
        id = id,
        title = title,
        subtitle = preview,
        leftIcon = Icons.Default.AccountCircle,
        showLeftIcon = true,
        rightIcon = Icons.AutoMirrored.Filled.KeyboardArrowRight,
        showRightIcon = true
    )
}
