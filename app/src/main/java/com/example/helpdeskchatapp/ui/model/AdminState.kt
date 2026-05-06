package com.example.helpdeskchatapp.ui.model

import com.example.helpdeskchatapp.domain.model.ChatInfo

data class AdminState(
    val chats: List<ChatInfo> = emptyList()
)
