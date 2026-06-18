package com.example.helpdeskchatapp.data.interfaces

import com.example.helpdeskchatapp.domain.model.producer.ChatMessageViewEntity
import com.example.helpdeskchatapp.domain.model.consumer.Message
import kotlinx.coroutines.flow.Flow

interface ChatRepository {
    fun getMessages(conversationId: String): Flow<List<ChatMessageViewEntity>>
    suspend fun sendMessage(message: Message): Result<String>
}
