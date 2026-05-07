package com.example.helpdeskchatapp.domain.mapper

import com.example.helpdeskchatapp.domain.model.ChatMessageViewEntity
import com.example.helpdeskchatapp.ui.model.ListRowEntity

fun ChatMessageViewEntity.chatDetailsMapper(currentUserId: String): ListRowEntity {
    val isFromMe = senderId == currentUserId

    return ListRowEntity(
        id = id,
        title = text,
        showLeftIcon = !isFromMe,
        showRightIcon = isFromMe,
        isChatLayout = true,
    )
}
