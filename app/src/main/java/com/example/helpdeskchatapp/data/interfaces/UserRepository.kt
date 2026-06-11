package com.example.helpdeskchatapp.data.interfaces

import com.example.helpdeskchatapp.domain.model.consumer.LoginParams
import com.example.helpdeskchatapp.domain.model.consumer.UserName

interface UserRepository {
    suspend fun login(params: LoginParams): Result<String>
    suspend fun register(params: LoginParams): Result<String>
    suspend fun loginAnonymously(): Result<String>
    suspend fun updateUserName(params: UserName): Result<Unit>
    suspend fun updateFcmToken(token: String): Result<Unit>
    fun getCurrentUser(): String?
    fun isAnonymous(): Boolean
    fun logout()
}
