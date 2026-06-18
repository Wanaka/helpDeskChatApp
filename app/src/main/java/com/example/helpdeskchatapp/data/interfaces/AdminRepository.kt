package com.example.helpdeskchatapp.data.interfaces

import com.example.helpdeskchatapp.domain.model.consumer.UserName
import com.example.helpdeskchatapp.domain.model.producer.ChatViewEntity
import kotlinx.coroutines.flow.Flow

interface AdminRepository {
    fun getChats(adminId: String): Flow<List<ChatViewEntity>>
    suspend fun createChat(adminId: String, userId: String, senderName: String): Result<String>
    suspend fun getUserName(userId: String): Result<UserName>
    suspend fun getChatForUser(userId: String): Result<String?>
}
