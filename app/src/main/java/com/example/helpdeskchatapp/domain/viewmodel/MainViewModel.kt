package com.example.helpdeskchatapp.domain.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.helpdeskchatapp.domain.model.CreateChatParams
import com.example.helpdeskchatapp.domain.usecase.CreateChatUseCase
import com.example.helpdeskchatapp.domain.usecase.GetChatForUserUseCase
import com.example.helpdeskchatapp.domain.usecase.GetCurrentUserUseCase
import com.example.helpdeskchatapp.domain.usecase.GetUserNameUseCase
import com.example.helpdeskchatapp.domain.usecase.IsAnonymousUseCase
import com.example.helpdeskchatapp.domain.usecase.LoginAnonymouslyUseCase
import com.example.helpdeskchatapp.domain.usecase.LogoutUseCase
import com.example.helpdeskchatapp.domain.usecase.UpdateUserNameUseCase
import com.example.helpdeskchatapp.navigation.AdminRouteKey
import com.example.helpdeskchatapp.navigation.DeepLinkLoadingKey
import com.example.helpdeskchatapp.navigation.LoginRouteKey
import androidx.navigation3.runtime.NavKey
import com.example.helpdeskchatapp.util.CurrentUserId
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val getCurrentUserUseCase: GetCurrentUserUseCase,
    private val isAnonymousUseCase: IsAnonymousUseCase,
    private val logoutUseCase: LogoutUseCase,
    private val loginAnonymouslyUseCase: LoginAnonymouslyUseCase,
    private val createChatUseCase: CreateChatUseCase,
    private val getChatForUserUseCase: GetChatForUserUseCase,
    private val getUserNameUseCase: GetUserNameUseCase,
    private val updateUserNameUseCase: UpdateUserNameUseCase
) : ViewModel() {

    private val _navigateToChat = MutableSharedFlow<String>(
        replay = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    val navigateToChat = _navigateToChat.asSharedFlow()

    private val _logoutEvent = MutableSharedFlow<Unit>()
    val logoutEvent = _logoutEvent.asSharedFlow()

    private val _showNameOverlay = MutableStateFlow(false)
    val showNameOverlay = _showNameOverlay.asStateFlow()

    private var pendingAdminId: String? = null

    fun handleDeepLink(adminId: String) {
        viewModelScope.launch {
            if (getCurrentUserUseCase() == null) {
                loginAnonymouslyUseCase()
            }

            val userId = getCurrentUserUseCase()
            if (userId != null) {
                CurrentUserId.CURRENT_USER_ID = userId
                
                val name = getUserNameUseCase(userId)
                if (name.isEmpty()) {
                    pendingAdminId = adminId
                    _showNameOverlay.value = true
                } else {
                    startChat(adminId, userId, name)
                }
            } else {
                logoutUseCase()
                _logoutEvent.emit(Unit)
            }
        }
    }

    fun updateName(name: String) {
        viewModelScope.launch {
            val userId = getCurrentUserUseCase() ?: return@launch
            val result = updateUserNameUseCase(name)
            result.onSuccess {
                _showNameOverlay.value = false
                pendingAdminId?.let { adminId ->
                    startChat(adminId, userId, name)
                    pendingAdminId = null
                }
            }.onFailure {
                // Handle failure
            }
        }
    }

    private suspend fun startChat(adminId: String, userId: String, name: String) {
        val result = createChatUseCase(
            CreateChatParams(
                adminId = adminId,
                userId = userId,
                senderName = name
            )
        )
        
        result.onSuccess { chatId ->
            _navigateToChat.emit(chatId)
        }.onFailure {
            logoutUseCase()
            _logoutEvent.emit(Unit)
        }
    }

    fun findExistingChat() {
        viewModelScope.launch {
            val userId = getCurrentUserUseCase()
            if (userId != null && isAnonymousUseCase()) {
                CurrentUserId.CURRENT_USER_ID = userId
                
                val name = getUserNameUseCase(userId)
                if (name.isEmpty()) {
                    _showNameOverlay.value = true
                    return@launch
                }

                val result = getChatForUserUseCase(userId)
                result.onSuccess { chatId ->
                    if (chatId != null) {
                        _navigateToChat.emit(chatId)
                    } else {
                        logoutUseCase()
                        _logoutEvent.emit(Unit)
                    }
                }.onFailure {
                    logoutUseCase()
                    _logoutEvent.emit(Unit)
                }
            }
        }
    }

    fun getInitialRoute(conversationId: String? = null): NavKey {
        if (conversationId != null) return DeepLinkLoadingKey
        
        val userId = getCurrentUserUseCase()
        return if (userId != null) {
            CurrentUserId.CURRENT_USER_ID = userId
            if (isAnonymousUseCase()) {
                DeepLinkLoadingKey
            } else {
                AdminRouteKey
            }
        } else {
            LoginRouteKey
        }
    }
}
