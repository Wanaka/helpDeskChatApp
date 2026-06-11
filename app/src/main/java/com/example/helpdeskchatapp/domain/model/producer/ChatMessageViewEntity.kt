package com.example.helpdeskchatapp.domain.model.producer

import com.google.firebase.Timestamp

data class ChatMessageViewEntity(
    val id: String,
    val text: String,
    val senderId: String,
    val timestamp: Timestamp
)
