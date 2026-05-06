package com.example.helpdeskchatapp.data.repository

import com.example.helpdeskchatapp.data.interfaces.UserRepository
import com.example.helpdeskchatapp.domain.model.LoginParams
import kotlinx.coroutines.delay

class FakeUserRepository : UserRepository {
    override suspend fun login(params: LoginParams): Result<String> {
        delay(1500) // Simulate network
        return if (params.username.contains("@") && params.password.length >= 6) {
            Result.success("Welcome, ${params.username}!")
        } else {
            Result.failure(Exception("Invalid email or password (min 6 chars)"))
        }
    }
}
