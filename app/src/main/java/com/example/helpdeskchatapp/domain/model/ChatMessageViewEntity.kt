package com.example.helpdeskchatapp.domain.model

import com.google.firebase.Timestamp

data class ChatMessageViewEntity(
    val id: String,
    val text: String,
    val senderId: String,
    val timestamp: Timestamp
)
