package com.example.helpdeskchatapp.domain.usecase

import com.example.helpdeskchatapp.data.interfaces.AdminRepository
import com.example.helpdeskchatapp.data.repository.FakeAdminRepository
import com.example.helpdeskchatapp.domain.model.ChatViewEntity

class GetChatsUseCase(
    private val repository: AdminRepository = FakeAdminRepository()
) : NoParamUseCase<List<ChatViewEntity>>() {

    override suspend fun invoke(): List<ChatViewEntity> {
        return repository.getChats()
    }
}
