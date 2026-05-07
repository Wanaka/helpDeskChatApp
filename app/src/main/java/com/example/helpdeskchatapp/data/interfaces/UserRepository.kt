package com.example.helpdeskchatapp.data.interfaces

import com.example.helpdeskchatapp.domain.model.LoginParams

interface UserRepository {
    suspend fun login(params: LoginParams): Result<String>
    suspend fun register(params: LoginParams): Result<String>
    fun getCurrentUser(): String?
    fun logout()
}
