package com.example.helpdeskchatapp.domain.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation3.runtime.NavKey
import com.example.helpdeskchatapp.domain.model.consumer.CreateChat
import com.example.helpdeskchatapp.domain.model.consumer.UserName
import com.example.helpdeskchatapp.domain.usecase.CreateChatUseCase
import com.example.helpdeskchatapp.domain.usecase.GetChatForUserUseCase
import com.example.helpdeskchatapp.domain.usecase.GetCurrentUserUseCase
import com.example.helpdeskchatapp.domain.usecase.GetUserNameUseCase
import com.example.helpdeskchatapp.domain.usecase.IsAnonymousUseCase
import com.example.helpdeskchatapp.domain.usecase.LoginAnonymouslyUseCase
import com.example.helpdeskchatapp.domain.usecase.LogoutUseCase
import com.example.helpdeskchatapp.domain.usecase.UpdateFcmTokenUseCase
import com.example.helpdeskchatapp.domain.usecase.UpdateUserNameUseCase
import com.example.helpdeskchatapp.navigation.AdminRouteKey
import com.example.helpdeskchatapp.navigation.DeepLinkLoadingKey
import com.example.helpdeskchatapp.navigation.LoginRouteKey
import com.example.helpdeskchatapp.util.CurrentUserId
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
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
    private val updateUserNameUseCase: UpdateUserNameUseCase,
    private val updateFcmTokenUseCase: UpdateFcmTokenUseCase
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

    private val _isAnonymous = MutableStateFlow(false)
    val isAnonymous = _isAnonymous.asStateFlow()

    private var pendingAdminId: String? = null

    fun handleDeepLink(adminId: String) {
        viewModelScope.launch {
            if (getCurrentUserUseCase() == null) {
                loginAnonymouslyUseCase()
            }

            val userId = getCurrentUserUseCase()
            if (userId != null) {
                try {
                    val token = FirebaseMessaging.getInstance().token.await()
                    updateFcmTokenUseCase(token)
                } catch (e: Exception) {
                }

                CurrentUserId.CURRENT_USER_ID = userId
                
                val name = getUserNameUseCase(userId)
                if (name.name.isEmpty()) {
                    pendingAdminId = adminId
                    _showNameOverlay.value = true
                    _isAnonymous.value = true
                } else {
                    startChat(adminId, userId, name.name)
                }
            } else {
                logoutUseCase()
                _logoutEvent.emit(Unit)
            }
        }
    }

    fun updateName(data: UserName) {
        viewModelScope.launch {
            val userId = getCurrentUserUseCase() ?: return@launch
            val result = updateUserNameUseCase(data)
            result.onSuccess {
                _showNameOverlay.value = false
                pendingAdminId?.let { adminId ->
                    startChat(adminId, userId, data.name)
                    pendingAdminId = null
                }
            }.onFailure {
                // Handle failure
            }
        }
    }

    private suspend fun startChat(adminId: String, userId: String, name: String) {
        val result = createChatUseCase(
            CreateChat(
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
                if (name.name.isEmpty()) {
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
