package haag.your.next.developer.domain.viewmodel

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import haag.your.next.developer.domain.model.consumer.CreateChat
import haag.your.next.developer.domain.model.consumer.UserName
import haag.your.next.developer.domain.usecase.ExistingChatResult
import haag.your.next.developer.domain.usecase.FindExistingChatUseCase
import haag.your.next.developer.domain.usecase.LogoutUseCase
import haag.your.next.developer.domain.usecase.PrepareDeepLinkSessionUseCase
import haag.your.next.developer.domain.usecase.SavePendingAdminIdUseCase
import haag.your.next.developer.domain.usecase.StartChatUseCase
import haag.your.next.developer.domain.usecase.SubmitUserNameUseCase
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DeepLinkViewModel @Inject constructor(
    private val logoutUseCase: LogoutUseCase,
    private val prepareDeepLinkSessionUseCase: PrepareDeepLinkSessionUseCase,
    private val startChatUseCase: StartChatUseCase,
    private val submitUserNameUseCase: SubmitUserNameUseCase,
    private val findExistingChatUseCase: FindExistingChatUseCase,
    private val savePendingAdminIdUseCase: SavePendingAdminIdUseCase
) : BaseViewModel() {

    private val _navigateToChat = MutableSharedFlow<String>(extraBufferCapacity = 1)
    val navigateToChat = _navigateToChat.asSharedFlow()

    private val _logoutEvent = MutableSharedFlow<Unit>(extraBufferCapacity = 1)
    val logoutEvent = _logoutEvent.asSharedFlow()

    private val _showNameOverlay = MutableStateFlow(false)
    val showNameOverlay = _showNameOverlay.asStateFlow()

    private val _isAnonymous = MutableStateFlow(false)
    val isAnonymous = _isAnonymous.asStateFlow()

    fun handleDeepLink(adminId: String) {
        viewModelScope.launch {
            prepareDeepLinkSessionUseCase()
                .onSuccess { session ->
                    if (session.userName.isEmpty()) {
                        savePendingAdminIdUseCase(adminId)
                        _isAnonymous.value = true
                        _showNameOverlay.value = true
                    } else {
                        startChatUseCase(CreateChat(adminId = adminId, userId = session.userId, senderName = session.userName))
                            .onSuccess { _navigateToChat.emit(it) }
                            .onFailure { emitLogout() }
                    }
                }
                .onFailure { emitLogout("Failed to connect. Please check your connection and try again.") }
        }
    }

    fun updateName(data: UserName) {
        viewModelScope.launch {
            submitUserNameUseCase(data)
                .onSuccess { chatId ->
                    _showNameOverlay.value = false
                    if (chatId != null) {
                        _navigateToChat.emit(chatId)
                    } else {
                        _toastEvent.emit(NO_CHAT_MESSAGE)
                    }
                }
                .onFailure { _toastEvent.emit(it.message ?: "Failed to load chat. Please scan the QR code again.") }
        }
    }

    fun findExistingChat() {
        viewModelScope.launch {
            findExistingChatUseCase()
                .onSuccess { result ->
                    when (result) {
                        is ExistingChatResult.NavigateToChat -> _navigateToChat.emit(result.chatId)
                        is ExistingChatResult.ShowNameOverlay -> {
                            _isAnonymous.value = true
                            _showNameOverlay.value = true
                        }
                        is ExistingChatResult.NoChatFound -> _toastEvent.emit(NO_CHAT_MESSAGE)
                        is ExistingChatResult.NotApplicable -> Unit
                    }
                }
                .onFailure { emitLogout() }
        }
    }

    private suspend fun emitLogout(message: String = "Something went wrong. Please try again.") {
        logoutUseCase()
        _toastEvent.emit(message)
        _logoutEvent.emit(Unit)
    }

    companion object {
        private const val NO_CHAT_MESSAGE = "Please scan the QR code again to start your chat"
    }
}
