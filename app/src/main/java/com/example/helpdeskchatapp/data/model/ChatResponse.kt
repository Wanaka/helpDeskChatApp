package com.example.helpdeskchatapp.data.model

data class ChatResponse(
    val id: String,
    val sender: String,
    val message: String,
    val adminName: String,
    val userId: String
)
