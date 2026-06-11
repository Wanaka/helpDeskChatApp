package com.example.helpdeskchatapp.domain.viewmodel

import androidx.lifecycle.viewModelScope
import com.example.helpdeskchatapp.domain.mapper.chatDetailsMapper
import com.example.helpdeskchatapp.domain.model.consumer.Message
import com.example.helpdeskchatapp.domain.model.consumer.UserName
import com.example.helpdeskchatapp.domain.usecase.GetChatMessagesUseCase
import com.example.helpdeskchatapp.domain.usecase.GetUserNameUseCase
import com.example.helpdeskchatapp.domain.usecase.IsAnonymousUseCase
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
    private val sendMessageUseCase: SendMessageUseCase,
    private val isAnonymousUseCase: IsAnonymousUseCase,
    private val getUserNameUseCase: GetUserNameUseCase
) : BaseViewModel<ChatState>() {

    private var currentConversationId: String = ""
    private val _messages =
        MutableStateFlow<List<ListRowEntity>>(emptyList())
    val messages = _messages.asStateFlow()

    private val _chatTitle =
        MutableStateFlow(UserName(name = "Admin Chat", company = "Company Name"))
    val chatTitle = _chatTitle.asStateFlow()

    fun initConversation(id: String) {
        currentConversationId = id
        loadData()
        if (!isAnonymousUseCase()) getUserNameSetTitle()
    }

    override fun loadData() {
        if (currentConversationId.isEmpty()) return

        viewModelScope.launch {
            _uiState.value = UiState.Loading
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

    private fun getUserNameSetTitle() {
        viewModelScope.launch {

            val chatTitleData = getUserNameUseCase(currentConversationId)
            _chatTitle.value = chatTitleData
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
