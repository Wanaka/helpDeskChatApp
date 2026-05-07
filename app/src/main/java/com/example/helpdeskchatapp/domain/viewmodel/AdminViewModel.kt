package com.example.helpdeskchatapp.domain.viewmodel

import androidx.lifecycle.viewModelScope
import com.example.helpdeskchatapp.domain.mapper.adminMapper
import com.example.helpdeskchatapp.domain.usecase.GetChatsUseCase
import com.example.helpdeskchatapp.data.interfaces.UserRepository
import com.example.helpdeskchatapp.ui.common.UiState
import com.example.helpdeskchatapp.ui.model.AdminState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AdminViewModel @Inject constructor(
    private val getChatsUseCase: GetChatsUseCase,
    private val userRepository: UserRepository
) : BaseViewModel<AdminState>() {

    init {
        loadData()
    }

    fun logout(onSuccess: () -> Unit) {
        userRepository.logout()
        onSuccess()
    }

    override fun loadData() {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            try {
                val chats = getChatsUseCase()
                _uiState.value = UiState.Success(AdminState(chats = chats.map { it.adminMapper() }))
            } catch (e: Exception) {
                _uiState.value = UiState.Error(e.message ?: "Failed to load chats")
            }
        }
    }
}
