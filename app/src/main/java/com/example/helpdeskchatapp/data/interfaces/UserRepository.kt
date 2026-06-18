package com.example.helpdeskchatapp.data.interfaces

import com.example.helpdeskchatapp.domain.model.consumer.Login
import com.example.helpdeskchatapp.domain.model.consumer.UserName

interface UserRepository {
    suspend fun login(params: Login): Result<Unit>
    suspend fun register(params: Login): Result<Unit>
    suspend fun loginAnonymously(): Result<String>
    suspend fun updateUserName(params: UserName): Result<Unit>
    suspend fun getFcmToken(): Result<String>
    suspend fun updateFcmToken(token: String): Result<Unit>
    fun getCurrentUser(): String?
    fun isAnonymous(): Boolean
    suspend fun logout(): Result<Unit>
}
