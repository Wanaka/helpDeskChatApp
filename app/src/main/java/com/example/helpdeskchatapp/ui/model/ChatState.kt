package com.example.helpdeskchatapp.ui.model

import com.example.helpdeskchatapp.domain.model.ChatMessage

data class ChatState(
    val messages: List<ChatMessage> = emptyList(),
    val currentUserId: String = "admin_1" // Hardcoded for this exercise
)
