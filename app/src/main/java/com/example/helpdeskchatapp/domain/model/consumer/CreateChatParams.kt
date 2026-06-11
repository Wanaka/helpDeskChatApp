package com.example.helpdeskchatapp.domain.model.consumer

data class CreateChatParams(
    val adminId: String,
    val userId: String,
    val senderName: String
)
