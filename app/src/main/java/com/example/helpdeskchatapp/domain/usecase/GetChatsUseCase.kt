package com.example.helpdeskchatapp.domain.usecase

import com.example.helpdeskchatapp.data.interfaces.AdminRepository
import com.example.helpdeskchatapp.domain.model.ChatViewEntity
import javax.inject.Inject

class GetChatsUseCase @Inject constructor(
    private val repository: AdminRepository
) : NoParamUseCase<List<ChatViewEntity>>() {

    override suspend fun invoke(): List<ChatViewEntity> {
        return repository.getChats()
    }
}
