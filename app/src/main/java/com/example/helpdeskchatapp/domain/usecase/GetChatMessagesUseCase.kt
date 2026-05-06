package com.example.helpdeskchatapp.domain.usecase

import com.example.helpdeskchatapp.data.interfaces.ChatRepository
import com.example.helpdeskchatapp.data.repository.FakeChatRepository
import com.example.helpdeskchatapp.domain.model.ChatMessageViewEntity

class GetChatMessagesUseCase(
    private val repository: ChatRepository = FakeChatRepository()
) : UseCase<Int, List<ChatMessageViewEntity>>() {
    override suspend fun invoke(params: Int): List<ChatMessageViewEntity> {
        return repository.getMessages(params)
    }
}
