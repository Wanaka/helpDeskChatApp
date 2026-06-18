package com.example.helpdeskchatapp.data.interfaces

import com.example.helpdeskchatapp.domain.model.producer.ChatViewEntity
import com.example.helpdeskchatapp.domain.model.producer.UserNameViewEntity
import kotlinx.coroutines.flow.Flow

interface AdminRepository {
    fun getChats(adminId: String): Flow<List<ChatViewEntity>>
    suspend fun createChat(adminId: String, userId: String, senderName: String): Result<String>
    suspend fun getUserName(userId: String): Result<UserNameViewEntity>
    suspend fun getChatForUser(userId: String): Result<String?>
}
