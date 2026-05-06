package com.example.helpdeskchatapp.data

import com.example.helpdeskchatapp.domain.model.ChatMessage
import kotlinx.coroutines.delay

interface ChatRepository {
    suspend fun getMessages(conversationId: Int): List<ChatMessage>
}

class FakeChatRepository : ChatRepository {
    override suspend fun getMessages(conversationId: Int): List<ChatMessage> {
        delay(800)
        return listOf(
            ChatMessage(1, "Hi, I'm having trouble with my account.", "user_123"),
            ChatMessage(2, "Hello! I can help you with that. What's the issue?", "admin_1"),
            ChatMessage(3, "I can't seem to reset my password.", "user_123"),
            ChatMessage(4, "Are you getting an error message?", "admin_1"),
            ChatMessage(5, "Yes, it says 'Invalid Token'.", "user_123"),
            ChatMessage(6, "Got it. Let me check your account details.", "admin_1")
        )
    }
}
