package com.example.helpdeskchatapp.domain.usecase

import com.example.helpdeskchatapp.data.interfaces.ChatRepository
import com.example.helpdeskchatapp.domain.model.ChatMessageViewEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetChatMessagesUseCase @Inject constructor(
    private val repository: ChatRepository
) : UseCase<String, Flow<List<ChatMessageViewEntity>>>() {
    override suspend fun invoke(params: String): Flow<List<ChatMessageViewEntity>> {
        return repository.getMessages(params)
    }
}
