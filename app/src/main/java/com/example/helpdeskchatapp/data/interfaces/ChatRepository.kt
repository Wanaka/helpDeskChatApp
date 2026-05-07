package com.example.helpdeskchatapp.data.interfaces

import com.example.helpdeskchatapp.domain.model.ChatMessageViewEntity
import com.example.helpdeskchatapp.domain.model.Message
import kotlinx.coroutines.flow.Flow

interface ChatRepository {
    suspend fun getMessages(conversationId: String): Flow<List<ChatMessageViewEntity>>
    suspend fun sendMessage(message: Message): Result<String>
}
