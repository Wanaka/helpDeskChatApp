package com.example.helpdeskchatapp.domain.model.producer

data class ChatViewEntity(
    val id: String,
    val sender: String,
    val message: String,
    val adminName: String,
    val userId: String
)
