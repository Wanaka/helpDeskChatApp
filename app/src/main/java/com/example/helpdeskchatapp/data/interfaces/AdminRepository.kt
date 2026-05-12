package com.example.helpdeskchatapp.data.interfaces

import com.example.helpdeskchatapp.domain.model.ChatViewEntity
import kotlinx.coroutines.flow.Flow

interface AdminRepository {
    fun getChats(): Flow<List<ChatViewEntity>>
    suspend fun createChat(adminId: String, userId: String, senderName: String): Result<String>
    suspend fun getUserName(userId: String): String
    suspend fun getChatForUser(userId: String): Result<String?>
}
