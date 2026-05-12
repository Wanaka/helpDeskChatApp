package com.example.helpdeskchatapp.domain.usecase

import com.example.helpdeskchatapp.data.interfaces.AdminRepository
import com.example.helpdeskchatapp.domain.model.CreateChatParams
import javax.inject.Inject

class CreateChatUseCase @Inject constructor(
    private val repository: AdminRepository
) : UseCase<CreateChatParams, Result<String>>() {
    override suspend fun invoke(params: CreateChatParams): Result<String> {
        return repository.createChat(params.adminId, params.userId, params.senderName)
    }
}
