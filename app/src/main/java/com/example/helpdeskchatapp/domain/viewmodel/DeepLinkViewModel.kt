package com.example.helpdeskchatapp.domain.viewmodel

import androidx.lifecycle.viewModelScope
import com.example.helpdeskchatapp.domain.model.consumer.CreateChat
import com.example.helpdeskchatapp.domain.model.consumer.UserName
import com.example.helpdeskchatapp.domain.usecase.CreateChatUseCase
import com.example.helpdeskchatapp.domain.usecase.GetChatForUserUseCase
import com.example.helpdeskchatapp.domain.usecase.GetCurrentUserUseCase
import com.example.helpdeskchatapp.domain.usecase.GetFcmTokenUseCase
import com.example.helpdeskchatapp.domain.usecase.GetUserNameUseCase
import com.example.helpdeskchatapp.domain.usecase.IsAnonymousUseCase
import com.example.helpdeskchatapp.domain.usecase.LoginAnonymouslyUseCase
import com.example.helpdeskchatapp.domain.usecase.LogoutUseCase
import com.example.helpdeskchatapp.domain.usecase.UpdateFcmTokenUseCase
import com.example.helpdeskchatapp.domain.usecase.UpdateUserNameUseCase
import com.example.helpdeskchatapp.ui.common.UiState
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
    private val updateFcmTokenUseCase: UpdateFcmTokenUseCase
) : BaseViewModel() {

    private val _navigateToChat = MutableSharedFlow<String>(extraBufferCapacity = 1)
    val navigateToChat = _navigateToChat.asSharedFlow()

    private val _logoutEvent = MutableSharedFlow<Unit>(extraBufferCapacity = 1)
    val logoutEvent = _logoutEvent.asSharedFlow()

    private val _showNameOverlay = MutableStateFlow(false)
    val showNameOverlay = _showNameOverlay.asStateFlow()

    private val _isAnonymous = MutableStateFlow(false)
    val isAnonymous = _isAnonymous.asStateFlow()

    private var pendingAdminId: String? = null

    override fun loadData() {
        _uiState.value = UiState.Success
    }

    fun handleDeepLink(adminId: String) {
        viewModelScope.launch {
            if (getCurrentUserUseCase() == null) {
                loginAnonymouslyUseCase()
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
                            pendingAdminId = adminId
                            _showNameOverlay.value = true
                            _isAnonymous.value = true
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
            updateUserNameUseCase(data)
                .onSuccess {
                    _showNameOverlay.value = false
                    pendingAdminId?.let { adminId ->
                        startChat(adminId, userId, data.name)
                        pendingAdminId = null
                    }
                }
        }
    }

    fun findExistingChat() {
        viewModelScope.launch {
            val userId = getCurrentUserUseCase()
            if (userId != null && isAnonymousUseCase()) {
                val nameResult = getUserNameUseCase(userId)
                val name = nameResult.getOrElse {
                    _toastEvent.emit(it.message ?: "Failed to load user name")
                    return@launch
                }
                if (name.name.isEmpty()) {
                    _showNameOverlay.value = true
                    return@launch
                }

                getChatForUserUseCase(userId)
                    .onSuccess { chatId ->
                        if (chatId != null) {
                            _navigateToChat.emit(chatId)
                        } else {
                            logoutUseCase()
                                .onFailure { e -> _toastEvent.emit("Logout failed: ${e.message}") }
                            _logoutEvent.emit(Unit)
                        }
                    }
                    .onFailure {
                        logoutUseCase()
                            .onFailure { e -> _toastEvent.emit("Logout failed: ${e.message}") }
                        _logoutEvent.emit(Unit)
                    }
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
            .onSuccess { chatId -> _navigateToChat.emit(chatId) }
            .onFailure {
                logoutUseCase()
                    .onFailure { e -> _toastEvent.emit("Logout failed: ${e.message}") }
                _logoutEvent.emit(Unit)
            }
    }
}
