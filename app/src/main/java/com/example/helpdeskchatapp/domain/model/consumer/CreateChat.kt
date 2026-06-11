package com.example.helpdeskchatapp.domain.model.consumer

data class CreateChat(
    val adminId: String,
    val userId: String,
    val senderName: String
)
