package com.example.helpdeskchatapp.domain.usecase

import com.example.helpdeskchatapp.data.interfaces.AdminRepository
import javax.inject.Inject

data class CreateChatParams(
    val adminId: String,
    val userId: String,
    val senderName: String
)

class CreateChatUseCase @Inject constructor(
    private val repository: AdminRepository
) : UseCase<CreateChatParams, Result<String>>() {
    override suspend fun invoke(params: CreateChatParams): Result<String> {
        return repository.createChat(params.adminId, params.userId, params.senderName)
    }
}
