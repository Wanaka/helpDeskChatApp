package com.example.helpdeskchatapp.data.interfaces

import com.example.helpdeskchatapp.domain.model.ChatViewEntity

interface AdminRepository {
    suspend fun getChats(): List<ChatViewEntity>
}