package com.example.helpdeskchatapp.data

import com.example.helpdeskchatapp.domain.model.ChatInfo
import kotlinx.coroutines.delay

interface AdminRepository {
    suspend fun getChats(): List<ChatInfo>
}

class FakeAdminRepository : AdminRepository {
    override suspend fun getChats(): List<ChatInfo> {
        delay(1000) // Simulate network
        return listOf(
            ChatInfo(1, "John Doe", "Hello, I need help!"),
            ChatInfo(2, "Jane Smith", "The app is crashing"),
            ChatInfo(3, "Bob Wilson", "Can I get a refund?"),
            ChatInfo(4, "Alice Brown", "How do I change my password?"),
            ChatInfo(5, "Charlie Davis", "Thanks for the support!")
        )
    }
}
