package com.example.helpdeskchatapp.domain.usecase

import com.example.helpdeskchatapp.data.AdminRepository
import com.example.helpdeskchatapp.data.FakeAdminRepository
import com.example.helpdeskchatapp.domain.model.ChatInfo

class GetChatsUseCase(
    private val repository: AdminRepository = FakeAdminRepository()
) : NoParamUseCase<List<ChatInfo>>() {

    override suspend fun invoke(): List<ChatInfo> {
        return repository.getChats()
    }
}
