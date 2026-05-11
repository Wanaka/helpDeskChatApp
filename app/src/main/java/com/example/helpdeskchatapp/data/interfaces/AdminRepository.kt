package com.example.helpdeskchatapp.data.interfaces

import com.example.helpdeskchatapp.domain.model.ChatViewEntity
import kotlinx.coroutines.flow.Flow

interface AdminRepository {
    fun getChats(): Flow<List<ChatViewEntity>>
}