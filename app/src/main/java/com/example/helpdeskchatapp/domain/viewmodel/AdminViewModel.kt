package com.example.helpdeskchatapp.domain.viewmodel

import androidx.lifecycle.viewModelScope
import com.example.helpdeskchatapp.domain.mapper.adminMapper
import com.example.helpdeskchatapp.domain.usecase.GetUserNameUseCase
import com.example.helpdeskchatapp.domain.usecase.GetChatsUseCase
import com.example.helpdeskchatapp.domain.usecase.LogoutUseCase
import com.example.helpdeskchatapp.domain.usecase.UpdateUserNameUseCase
import com.example.helpdeskchatapp.ui.common.UiState
import com.example.helpdeskchatapp.ui.model.AdminState
import com.example.helpdeskchatapp.ui.model.ListRowEntity
import com.example.helpdeskchatapp.util.CurrentUserId
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
    private val updateUserNameUseCase: UpdateUserNameUseCase
) : BaseViewModel<AdminState>() {

    private val _chats = MutableStateFlow<List<ListRowEntity>>(emptyList())
    val chats = _chats.asStateFlow()

    private val _showNameOverlay = MutableStateFlow(false)
    val showNameOverlay = _showNameOverlay.asStateFlow()

    init {
        checkAdminName()
        loadData()
    }

    private fun checkAdminName() {
        viewModelScope.launch {
            val name = getUserNameUseCase(CurrentUserId.CURRENT_USER_ID)
            if (name.first.isEmpty() || name.first == "Admin") {
                _showNameOverlay.value = true
            }
        }
    }

    fun updateName(data: Pair<String, String>) {
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
        logoutUseCase()
        onSuccess()
    }

    override fun loadData() {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            try {
                getChatsUseCase().collect { chats ->
                    _chats.value = chats.map { it.adminMapper() }
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
