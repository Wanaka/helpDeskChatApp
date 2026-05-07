package com.example.helpdeskchatapp.data.model

import com.google.firebase.Timestamp

data class ChatMessageResponse(
    val id: Int,
    val text: String,
    val senderId: String,
    val timestamp: Timestamp
)