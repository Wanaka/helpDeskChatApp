package com.example.helpdeskchatapp.domain.usecase

import com.example.helpdeskchatapp.data.interfaces.ChatRepository
import com.example.helpdeskchatapp.domain.model.producer.ChatMessageViewEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetChatMessagesUseCase @Inject constructor(
    private val repository: ChatRepository
) : FlowUseCase<String, List<ChatMessageViewEntity>>() {
    override fun invoke(params: String): Flow<List<ChatMessageViewEntity>> {
        return repository.getMessages(params)
    }
}
