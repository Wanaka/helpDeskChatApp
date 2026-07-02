package haag.your.next.developer.domain.viewmodel

import androidx.lifecycle.viewModelScope
import haag.your.next.developer.domain.model.consumer.CreateChat
import haag.your.next.developer.domain.model.consumer.UserName
import haag.your.next.developer.domain.usecase.ClearPendingAdminIdUseCase
import haag.your.next.developer.domain.usecase.CreateChatUseCase
import haag.your.next.developer.domain.usecase.GetPendingAdminIdUseCase
import haag.your.next.developer.domain.usecase.SavePendingAdminIdUseCase
import haag.your.next.developer.domain.usecase.GetChatForUserUseCase
import haag.your.next.developer.domain.usecase.GetCurrentUserUseCase
import haag.your.next.developer.domain.usecase.GetFcmTokenUseCase
import haag.your.next.developer.domain.usecase.GetUserNameUseCase
import haag.your.next.developer.domain.usecase.IsAnonymousUseCase
import haag.your.next.developer.domain.usecase.LoginAnonymouslyUseCase
import haag.your.next.developer.domain.usecase.LogoutUseCase
import haag.your.next.developer.domain.usecase.UpdateFcmTokenUseCase
import haag.your.next.developer.domain.usecase.UpdateUserNameUseCase
import haag.your.next.developer.ui.common.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DeepLinkViewModel @Inject constructor(
    private val getCurrentUserUseCase: GetCurrentUserUseCase,
    private val isAnonymousUseCase: IsAnonymousUseCase,
    private val logoutUseCase: LogoutUseCase,
    private val loginAnonymouslyUseCase: LoginAnonymouslyUseCase,
    private val createChatUseCase: CreateChatUseCase,
    private val getChatForUserUseCase: GetChatForUserUseCase,
    private val getUserNameUseCase: GetUserNameUseCase,
    private val updateUserNameUseCase: UpdateUserNameUseCase,
    private val getFcmTokenUseCase: GetFcmTokenUseCase,
    private val updateFcmTokenUseCase: UpdateFcmTokenUseCase,
    private val savePendingAdminIdUseCase: SavePendingAdminIdUseCase,
    private val getPendingAdminIdUseCase: GetPendingAdminIdUseCase,
    private val clearPendingAdminIdUseCase: ClearPendingAdminIdUseCase
) : BaseViewModel() {

    private val _navigateToChat = MutableSharedFlow<String>(extraBufferCapacity = 1)
    val navigateToChat = _navigateToChat.asSharedFlow()

    private val _logoutEvent = MutableSharedFlow<Unit>(extraBufferCapacity = 1)
    val logoutEvent = _logoutEvent.asSharedFlow()

    private val _showNameOverlay = MutableStateFlow(false)
    val showNameOverlay = _showNameOverlay.asStateFlow()

    private val _isAnonymous = MutableStateFlow(false)
    val isAnonymous = _isAnonymous.asStateFlow()

    override fun loadData() {
        _uiState.value = UiState.Success
    }

    fun handleDeepLink(adminId: String) {
        viewModelScope.launch {
            if (getCurrentUserUseCase() == null) {
                loginAnonymouslyUseCase()
                    .onFailure {
                        _toastEvent.emit("Failed to connect. Please check your connection and try again.")
                        _logoutEvent.emit(Unit)
                        return@launch
                    }
            }

            val userId = getCurrentUserUseCase()
            if (userId != null) {
                getFcmTokenUseCase().onSuccess { token ->
                    updateFcmTokenUseCase(token)
                        .onFailure { _toastEvent.emit("Failed to update notification token") }
                }

                getUserNameUseCase(userId)
                    .onSuccess { name ->
                        if (name.name.isEmpty()) {
                            savePendingAdminIdUseCase(adminId)
                            _isAnonymous.value = true
                            _showNameOverlay.value = true
                        } else {
                            startChat(adminId, userId, name.name)
                        }
                    }
                    .onFailure {
                        logoutUseCase()
                            .onFailure { e -> _toastEvent.emit("Logout failed: ${e.message}") }
                        _logoutEvent.emit(Unit)
                    }
            } else {
                logoutUseCase()
                    .onFailure { _toastEvent.emit("Logout failed: ${it.message}") }
                _logoutEvent.emit(Unit)
            }
        }
    }

    fun updateName(data: UserName) {
        viewModelScope.launch {
            val userId = getCurrentUserUseCase() ?: return@launch
            val adminId = getPendingAdminIdUseCase()
            updateUserNameUseCase(data)
                .onSuccess {
                    _showNameOverlay.value = false
                    if (adminId != null) {
                        startChat(adminId, userId, data.name)
                    } else {
                        getChatForUserUseCase(userId)
                            .onSuccess { chatId ->
                                if (chatId != null) {
                                    _navigateToChat.emit(chatId)
                                } else {
                                    _toastEvent.emit("Please scan the QR code again to start your chat")
                                }
                            }
                            .onFailure {
                                _toastEvent.emit("Failed to load chat. Please scan the QR code again.")
                            }
                    }
                }
        }
    }

    fun findExistingChat() {
        viewModelScope.launch {
            val userId = getCurrentUserUseCase()

            if (userId == null || !isAnonymousUseCase()) return@launch

            val name = getUserNameUseCase(userId).getOrElse {
                _toastEvent.emit(it.message ?: "Failed to load user name")
                return@launch
            }

            if (name.name.isEmpty()) {
                _isAnonymous.value = true
                _showNameOverlay.value = true
                return@launch
            }

            getChatForUserUseCase(userId)
                .onSuccess { chatId ->
                    if (chatId != null) {
                        _navigateToChat.emit(chatId)
                    } else {
                        val recoveredAdminId = getPendingAdminIdUseCase()
                        if (recoveredAdminId != null) {
                            startChat(recoveredAdminId, userId, name.name)
                        } else {
                            _toastEvent.emit("Please scan the QR code again to start your chat")
                        }
                    }
                }
                .onFailure {
                    logoutUseCase()
                        .onFailure { e -> _toastEvent.emit("Logout failed: ${e.message}") }
                    _logoutEvent.emit(Unit)
                }
        }
    }

    private suspend fun startChat(adminId: String, userId: String, name: String) {
        createChatUseCase(
            CreateChat(
                adminId = adminId,
                userId = userId,
                senderName = name
            )
        )
            .onSuccess { chatId ->
                clearPendingAdminIdUseCase()
                _navigateToChat.emit(chatId)
            }
            .onFailure {
                logoutUseCase()
                    .onFailure { e -> _toastEvent.emit("Logout failed: ${e.message}") }
                _logoutEvent.emit(Unit)
            }
    }
}
