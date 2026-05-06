package com.example.helpdeskchatapp.domain.model

data class ChatMessageViewEntity(
    val id: Int,
    val text: String,
    val senderId: String,
    val timestamp: Long = System.currentTimeMillis()
)
