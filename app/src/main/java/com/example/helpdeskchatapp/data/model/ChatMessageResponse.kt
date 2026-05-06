package com.example.helpdeskchatapp.data.model

data class ChatMessageResponse(
    val id: Int,
    val text: String,
    val senderId: String,
    val timestamp: Long
)