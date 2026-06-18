package com.example.helpdeskchatapp.domain.viewmodel

import androidx.lifecycle.viewModelScope
import com.example.helpdeskchatapp.domain.mapper.chatDetailsMapper
import com.example.helpdeskchatapp.domain.model.consumer.Message
import com.example.helpdeskchatapp.domain.model.producer.UserNameViewEntity
import com.example.helpdeskchatapp.domain.usecase.GetAdminNameUseCase
import com.example.helpdeskchatapp.domain.usecase.GetChatMessagesUseCase
import com.example.helpdeskchatapp.domain.usecase.GetCurrentUserUseCase
import com.example.helpdeskchatapp.domain.usecase.GetUserNameUseCase
import com.example.helpdeskchatapp.domain.usecase.IsAnonymousUseCase
import com.example.helpdeskchatapp.domain.usecase.SendMessageUseCase
import com.example.helpdeskchatapp.ui.common.UiState
import com.example.helpdeskchatapp.ui.model.ListRowEntity
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
    private val getUserNameUseCase: GetUserNameUseCase,
    private val getAdminNameUseCase: GetAdminNameUseCase,
    private val getCurrentUserUseCase: GetCurrentUserUseCase
) : BaseViewModel() {

    private var currentConversationId: String = ""
    private var currentUserId: String = ""
    private val _messages =
        MutableStateFlow<List<ListRowEntity>>(emptyList())
    val messages = _messages.asStateFlow()

    private val _chatTitle =
        MutableStateFlow(UserNameViewEntity(name = "", company = ""))
    val chatTitle = _chatTitle.asStateFlow()

    fun initConversation(id: String) {
        currentConversationId = id
        viewModelScope.launch {
            currentUserId = getCurrentUserUseCase() ?: ""
            loadData()
            if (!isAnonymousUseCase()) getUserNameSetTitle() else getAdminNameSetTitle()
        }
    }

    override fun loadData() {
        if (currentConversationId.isEmpty()) return

        viewModelScope.launch {
            _uiState.value = UiState.Loading
            getChatMessagesUseCase(currentConversationId)
                .collect { messages ->
                    _messages.value = messages.map {
                        it.chatDetailsMapper(
                            currentUserId = currentUserId
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
            getUserNameUseCase(currentConversationId)
                .onSuccess { userName ->
                    _chatTitle.value = userName
                }
                .onFailure { error ->
                    _toastEvent.emit(error.message ?: "Failed to load chat title")
                }
        }
    }

    private fun getAdminNameSetTitle() {
        viewModelScope.launch {
            getAdminNameUseCase(currentConversationId)
                .onSuccess { adminName ->
                    _chatTitle.value = UserNameViewEntity(name = adminName, company = "")
                }
                .onFailure { error ->
                    _toastEvent.emit(error.message ?: "Failed to load chat title")
                }
        }
    }

    fun sendMessage(message: Message) {
        viewModelScope.launch {
            val result = sendMessageUseCase(
                message.copy(
                    conversationId = currentConversationId,
                    senderId = currentUserId
                )
            )

            result.onFailure { error ->
                _toastEvent.emit(error.message ?: "Failed to send message")
            }
        }
    }

}
