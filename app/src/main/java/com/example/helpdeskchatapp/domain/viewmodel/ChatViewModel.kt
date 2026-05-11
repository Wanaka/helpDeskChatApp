package com.example.helpdeskchatapp.domain.viewmodel

import androidx.lifecycle.viewModelScope
import com.example.helpdeskchatapp.domain.mapper.chatDetailsMapper
import com.example.helpdeskchatapp.domain.model.ChatMessageViewEntity
import com.example.helpdeskchatapp.domain.model.Message
import com.example.helpdeskchatapp.domain.usecase.GetChatMessagesUseCase
import com.example.helpdeskchatapp.domain.usecase.SendMessageUseCase
import com.example.helpdeskchatapp.ui.common.UiState
import com.example.helpdeskchatapp.ui.model.ChatState
import com.example.helpdeskchatapp.ui.model.ListRowEntity
import com.example.helpdeskchatapp.util.CurrentUserId.CURRENT_USER_ID
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val getChatMessagesUseCase: GetChatMessagesUseCase,
    private val sendMessageUseCase: SendMessageUseCase
) : BaseViewModel<ChatState>() {

    private var currentConversationId: String = ""
    private val _messages =
        MutableStateFlow<List<ListRowEntity>>(emptyList())
    val messages = _messages.asStateFlow()

    fun initConversation(id: String) {
        currentConversationId = id
        loadData()
        observeMessages()
    }

    override fun loadData() {
        if (currentConversationId.isEmpty()) return

        viewModelScope.launch {
            _uiState.value = UiState.Loading
            observeMessages()
        }
    }

    private fun observeMessages() {
        viewModelScope.launch {
            getChatMessagesUseCase(currentConversationId)
                .collect { messages ->
                    _messages.value = messages.map {
                        it.chatDetailsMapper(
                            currentUserId = CURRENT_USER_ID
                        )
                    }
                    if (_uiState.value is UiState.Loading) {
                        _uiState.value = UiState.Success
                    }
                }
        }
    }

    fun sendMessage(message: Message) {
        viewModelScope.launch {
            val result = sendMessageUseCase(
                message.copy(
                    conversationId = currentConversationId,
                    senderId = CURRENT_USER_ID
                )
            )

            result.onFailure { error ->
                _toastEvent.emit(error.message ?: "Failed to send message")
            }
        }
    }

}
