package com.example.helpdeskchatapp.domain.usecase

import com.example.helpdeskchatapp.data.interfaces.AdminRepository
import com.example.helpdeskchatapp.domain.model.ChatViewEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetChatsUseCase @Inject constructor(
    private val repository: AdminRepository
) : NoParamUseCase<Flow<List<ChatViewEntity>>>() {

    override suspend fun invoke(): Flow<List<ChatViewEntity>> {
        return repository.getChats()
    }
}
