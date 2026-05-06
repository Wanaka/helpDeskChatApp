package com.example.helpdeskchatapp.data.repository

import com.example.helpdeskchatapp.data.interfaces.ChatRepository
import com.example.helpdeskchatapp.data.mapper.toDomain
import com.example.helpdeskchatapp.data.model.ChatMessageResponse
import com.example.helpdeskchatapp.domain.model.ChatMessageViewEntity
import kotlinx.coroutines.delay

class FakeChatRepository : ChatRepository {

    override suspend fun getMessages(conversationId: Int): List<ChatMessageViewEntity> {
        delay(800)
        val apiResponse = listOf(
            ChatMessageResponse(
                1,
                "Hi, I'm having trouble with my account.",
                "user_123",
                System.currentTimeMillis()
            ),
            ChatMessageResponse(
                2,
                "Hello! I can help you with that. What's the issue?",
                "admin_1",
                System.currentTimeMillis()
            ),
            ChatMessageResponse(
                3,
                "I can't seem to reset my password.",
                "user_123",
                System.currentTimeMillis()
            ),
            ChatMessageResponse(
                4,
                "Are you getting an error message?",
                "admin_1",
                System.currentTimeMillis()
            ),
            ChatMessageResponse(
                5,
                "Yes, it says 'Invalid Token'.",
                "user_123",
                System.currentTimeMillis()
            ),
            ChatMessageResponse(
                6,
                "Got it. Let me check your account details.",
                "admin_1",
                System.currentTimeMillis()
            )
        )
        return apiResponse.map { it.toDomain() }
    }
}
