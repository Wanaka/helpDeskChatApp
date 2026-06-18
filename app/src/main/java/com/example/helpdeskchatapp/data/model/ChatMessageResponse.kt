package com.example.helpdeskchatapp.data.model

data class ChatMessageResponse(
    val id: String,
    val text: String,
    val senderId: String,
    val timestamp: Long
)