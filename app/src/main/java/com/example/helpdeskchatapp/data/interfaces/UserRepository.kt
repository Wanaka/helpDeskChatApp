package com.example.helpdeskchatapp.data.interfaces

import com.example.helpdeskchatapp.domain.model.LoginParams

interface UserRepository {
    suspend fun login(params: LoginParams): Result<String>
}
