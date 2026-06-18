package com.example.helpdeskchatapp.domain.viewmodel

import androidx.lifecycle.viewModelScope
import com.example.helpdeskchatapp.domain.mapper.toListRowEntity
import com.example.helpdeskchatapp.domain.model.consumer.UserName
import com.example.helpdeskchatapp.domain.usecase.GetCurrentUserUseCase
import com.example.helpdeskchatapp.domain.usecase.GetUserNameUseCase
import com.example.helpdeskchatapp.domain.usecase.GetChatsUseCase
import com.example.helpdeskchatapp.domain.usecase.LogoutUseCase
import com.example.helpdeskchatapp.domain.usecase.UpdateUserNameUseCase
import com.example.helpdeskchatapp.ui.common.UiState
import com.example.helpdeskchatapp.ui.model.ListRowEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AdminViewModel @Inject constructor(
    private val getChatsUseCase: GetChatsUseCase,
    private val logoutUseCase: LogoutUseCase,
    private val getUserNameUseCase: GetUserNameUseCase,
    private val updateUserNameUseCase: UpdateUserNameUseCase,
    private val getCurrentUserUseCase: GetCurrentUserUseCase
) : BaseViewModel() {

    private val _chats = MutableStateFlow<List<ListRowEntity>>(emptyList())
    val chats = _chats.asStateFlow()

    private val _adminId = MutableStateFlow("")
    val adminId = _adminId.asStateFlow()

    private val _showNameOverlay = MutableStateFlow(false)
    val showNameOverlay = _showNameOverlay.asStateFlow()

    init {
        checkAdminName()
        loadData()
    }

    private fun checkAdminName() {
        viewModelScope.launch {
            val adminId = getCurrentUserUseCase() ?: return@launch
            getUserNameUseCase(adminId)
                .onSuccess { name ->
                    if (name.name.isEmpty() || name.name == "Admin") {
                        _showNameOverlay.value = true
                    }
                }
                .onFailure {
                    _toastEvent.emit("Failed to load admin name")
                }
        }
    }

    fun updateName(data: UserName) {
        viewModelScope.launch {
            val result = updateUserNameUseCase(data)
            result.onSuccess {
                _showNameOverlay.value = false
            }.onFailure {
                _toastEvent.emit("Failed to update name")
            }
        }
    }

    fun logout(onSuccess: () -> Unit) {
        viewModelScope.launch {
            logoutUseCase()
                .onFailure { _toastEvent.emit("Logout failed: ${it.message}") }
            // Navigate regardless — even if token cleanup failed, the user is signed out
            onSuccess()
        }
    }

    override fun loadData() {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            val resolvedAdminId = getCurrentUserUseCase()
            if (resolvedAdminId == null) {
                _uiState.value = UiState.Error("User not authenticated")
                return@launch
            }
            _adminId.value = resolvedAdminId
            try {
                getChatsUseCase(resolvedAdminId).collect { chats ->
                    _chats.value = chats.map { it.toListRowEntity() }
                    if (_uiState.value is UiState.Loading) {
                        _uiState.value = UiState.Success
                    }
                }
            } catch (e: Exception) {
                _uiState.value = UiState.Error(e.message ?: "Failed to load chats")
            }
        }
    }
}
