package haag.your.next.developer.domain.viewmodel

import androidx.lifecycle.viewModelScope
import haag.your.next.developer.domain.mapper.toListRowEntity
import haag.your.next.developer.domain.model.consumer.Message
import haag.your.next.developer.domain.model.producer.UserNameViewEntity
import haag.your.next.developer.domain.usecase.GetAdminNameUseCase
import haag.your.next.developer.domain.usecase.GetChatMessagesUseCase
import haag.your.next.developer.domain.usecase.GetCurrentUserUseCase
import haag.your.next.developer.domain.usecase.GetUserNameUseCase
import haag.your.next.developer.domain.usecase.IsAnonymousUseCase
import haag.your.next.developer.domain.usecase.SaveLocalReadTimestampUseCase
import haag.your.next.developer.domain.usecase.SendMessageUseCase
import haag.your.next.developer.ui.common.ActiveChatTracker
import haag.your.next.developer.ui.common.UiState
import haag.your.next.developer.ui.model.ListRowEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
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
    private val getCurrentUserUseCase: GetCurrentUserUseCase,
    private val saveLocalReadTimestampUseCase: SaveLocalReadTimestampUseCase
) : BaseViewModel() {

    private var currentConversationId: String = ""
    private var currentUserId: String = ""
    private val _messages =
        MutableStateFlow<List<ListRowEntity>>(emptyList())
    val messages = _messages.asStateFlow()

    private val _chatTitle =
        MutableStateFlow(UserNameViewEntity(name = "", company = ""))
    val chatTitle = _chatTitle.asStateFlow()

    private val _isAnonymous = MutableStateFlow(false)
    val isAnonymous = _isAnonymous.asStateFlow()

    override fun onCleared() {
        super.onCleared()
        ActiveChatTracker.currentConversationId = null
        CoroutineScope(Dispatchers.IO).launch {
            saveLocalReadTimestampUseCase(currentConversationId)
        }
    }

    fun initConversation(id: String) {
        currentConversationId = id
        ActiveChatTracker.currentConversationId = id
        viewModelScope.launch {
            currentUserId = getCurrentUserUseCase() ?: ""
            val anonymous = isAnonymousUseCase()
            _isAnonymous.value = anonymous
            loadData()
            if (!anonymous) getUserNameSetTitle() else getAdminNameSetTitle()
        }
    }

    override fun loadData() {
        if (currentConversationId.isEmpty()) return

        viewModelScope.launch {
            _uiState.value = UiState.Loading
            getChatMessagesUseCase(currentConversationId)
                .collect { messages ->
                    _messages.value = messages.map {
                        it.toListRowEntity(currentUserId = currentUserId)
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
