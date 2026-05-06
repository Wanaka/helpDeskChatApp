package com.example.helpdeskchatapp.data.repository

import com.example.helpdeskchatapp.data.interfaces.AdminRepository
import com.example.helpdeskchatapp.data.mapper.toDomain
import com.example.helpdeskchatapp.data.model.ChatResponse
import com.example.helpdeskchatapp.domain.model.ChatViewEntity
import kotlinx.coroutines.delay

class FakeAdminRepository : AdminRepository {

    override suspend fun getChats(): List<ChatViewEntity> {
        delay(1000) // Simulate network
        val apiResponse = listOf(
            ChatResponse(1, "John Doe", "Hello, I need help!"),
            ChatResponse(2, "Jane Smith", "The app is crashing"),
            ChatResponse(3, "Bob Wilson", "Can I get a refund?"),
            ChatResponse(4, "Alice Brown", "How do I change my password?"),
            ChatResponse(5, "Charlie Davis", "Thanks for the support!")
        )
        return apiResponse.map { it.toDomain() }
    }
}
