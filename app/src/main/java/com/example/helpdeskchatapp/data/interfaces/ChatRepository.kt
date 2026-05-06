package com.example.helpdeskchatapp.data.interfaces

import com.example.helpdeskchatapp.domain.model.ChatMessageViewEntity

interface ChatRepository {
    suspend fun getMessages(conversationId: Int): List<ChatMessageViewEntity>
}
