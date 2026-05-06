package com.example.helpdeskchatapp.domain.viewmodel

import androidx.lifecycle.viewModelScope
import com.example.helpdeskchatapp.domain.usecase.GetChatsUseCase
import com.example.helpdeskchatapp.ui.common.UiState
import com.example.helpdeskchatapp.ui.model.AdminState
import kotlinx.coroutines.launch

class AdminViewModel(
    private val getChatsUseCase: GetChatsUseCase = GetChatsUseCase()
) : BaseViewModel<AdminState>() {

    init {
        loadData()
    }

    override fun loadData() {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            try {
                val chats = getChatsUseCase()
                _uiState.value = UiState.Success(AdminState(chats = chats))
            } catch (e: Exception) {
                _uiState.value = UiState.Error(e.message ?: "Failed to load chats")
            }
        }
    }
}
