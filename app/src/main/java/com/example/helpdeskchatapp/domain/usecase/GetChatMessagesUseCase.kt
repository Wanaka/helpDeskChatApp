package com.example.helpdeskchatapp.domain.usecase

import com.example.helpdeskchatapp.data.ChatRepository
import com.example.helpdeskchatapp.data.FakeChatRepository
import com.example.helpdeskchatapp.domain.model.ChatMessage

class GetChatMessagesUseCase(
    private val repository: ChatRepository = FakeChatRepository()
) : UseCase<Int, List<ChatMessage>>() {
    override suspend fun invoke(params: Int): List<ChatMessage> {
        return repository.getMessages(params)
    }
}
