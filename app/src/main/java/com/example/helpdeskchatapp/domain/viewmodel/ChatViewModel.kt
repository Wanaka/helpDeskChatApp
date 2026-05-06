package com.example.helpdeskchatapp.domain.viewmodel

import androidx.lifecycle.viewModelScope
import com.example.helpdeskchatapp.domain.mapper.chatDetailsMapper
import com.example.helpdeskchatapp.domain.usecase.GetChatMessagesUseCase
import com.example.helpdeskchatapp.ui.common.UiState
import com.example.helpdeskchatapp.ui.model.ChatState
import com.example.helpdeskchatapp.util.CurrentUserId.CURRENT_USER_ID
import kotlinx.coroutines.launch

class ChatViewModel(
    private val getChatMessagesUseCase: GetChatMessagesUseCase = GetChatMessagesUseCase()
) : BaseViewModel<ChatState>() {

    private var currentConversationId: Int = -1

    fun initConversation(id: Int) {
        currentConversationId = id
        loadData()
    }

    override fun loadData() {
        if (currentConversationId == -1) return

        viewModelScope.launch {
            _uiState.value = UiState.Loading
            try {
                val messages = getChatMessagesUseCase(currentConversationId)
                _uiState.value = UiState.Success(
                    ChatState(messages = messages.map { it.chatDetailsMapper(currentUserId = CURRENT_USER_ID) })
                )
            } catch (e: Exception) {
                _uiState.value = UiState.Error(e.message ?: "Failed to load messages")
            }
        }
    }
}
