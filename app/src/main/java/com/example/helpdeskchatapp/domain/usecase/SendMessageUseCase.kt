package com.example.helpdeskchatapp.domain.usecase

import com.example.helpdeskchatapp.data.interfaces.ChatRepository
import com.example.helpdeskchatapp.domain.model.consumer.Message
import javax.inject.Inject

class SendMessageUseCase @Inject constructor(
    private val repository: ChatRepository
) : UseCase<Message, Result<String>>() {
    override suspend fun invoke(params: Message): Result<String> {
        return repository.sendMessage(params)
    }
}
