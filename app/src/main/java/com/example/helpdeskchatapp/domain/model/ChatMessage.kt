package com.example.helpdeskchatapp.domain.model

data class ChatMessage(
    val id: Int,
    val text: String,
    val senderId: String,
    val timestamp: Long = System.currentTimeMillis()
)
