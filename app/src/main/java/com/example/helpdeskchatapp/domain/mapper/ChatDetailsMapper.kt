package com.example.helpdeskchatapp.domain.mapper

import com.example.helpdeskchatapp.domain.model.ChatMessageViewEntity
import com.example.helpdeskchatapp.ui.model.ListRowEntity

fun ChatMessageViewEntity.chatDetailsMapper(currentUserId: String): ListRowEntity {
    val isFromMe = this.senderId == currentUserId

    return ListRowEntity(
        id = this.id,
        title = this.text,
        showLeftIcon = !isFromMe,
        showRightIcon = isFromMe,
        isChatLayout = true
    )
}
