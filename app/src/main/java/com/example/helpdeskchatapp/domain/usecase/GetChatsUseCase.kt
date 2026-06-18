package com.example.helpdeskchatapp.domain.usecase

import com.example.helpdeskchatapp.data.interfaces.AdminRepository
import com.example.helpdeskchatapp.domain.model.producer.ChatViewEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetChatsUseCase @Inject constructor(
    private val repository: AdminRepository
) : FlowUseCase<String, List<ChatViewEntity>>() {
    override fun invoke(params: String): Flow<List<ChatViewEntity>> {
        return repository.getChats(params)
    }
}
