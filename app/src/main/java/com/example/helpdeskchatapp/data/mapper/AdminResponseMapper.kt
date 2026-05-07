package com.example.helpdeskchatapp.data.mapper

import com.example.helpdeskchatapp.data.model.ChatResponse
import com.example.helpdeskchatapp.domain.model.ChatViewEntity

fun ChatResponse.toDomain(): ChatViewEntity {
    return ChatViewEntity(
        id = id.toString(),
        sender = sender,
        message = message,
        adminName = adminName,
        userId = userId
    )
}
