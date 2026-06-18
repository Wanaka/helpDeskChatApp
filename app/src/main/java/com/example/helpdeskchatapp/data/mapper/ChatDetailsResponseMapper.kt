package com.example.helpdeskchatapp.data.mapper

import com.example.helpdeskchatapp.data.model.ChatMessageResponse
import com.example.helpdeskchatapp.domain.model.producer.ChatMessageViewEntity

fun ChatMessageResponse.toDomain(): ChatMessageViewEntity {
    return ChatMessageViewEntity(
        id = id,
        text = text,
        senderId = senderId,
        timestamp = timestamp,
    )
}
