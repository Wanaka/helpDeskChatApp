package com.example.helpdeskchatapp.domain.mapper

import com.example.helpdeskchatapp.domain.model.producer.ChatMessageViewEntity
import com.example.helpdeskchatapp.ui.model.ListRowEntity

fun ChatMessageViewEntity.toListRowEntity(currentUserId: String): ListRowEntity {
    val isFromMe = senderId == currentUserId

    return ListRowEntity(
        id = id,
        title = text,
        showLeftIcon = !isFromMe,
        showRightIcon = isFromMe,
        isChatLayout = true,
    )
}
