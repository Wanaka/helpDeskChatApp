package com.example.helpdeskchatapp.domain.model

data class Message(
    val message: String?,
    val conversationId: String? = "",
    val senderId: String? = ""
)
