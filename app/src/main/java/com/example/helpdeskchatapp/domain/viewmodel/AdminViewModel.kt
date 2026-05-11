package com.example.helpdeskchatapp.domain.viewmodel

import androidx.lifecycle.viewModelScope
import com.example.helpdeskchatapp.domain.mapper.adminMapper
import com.example.helpdeskchatapp.domain.usecase.GetChatsUseCase
import com.example.helpdeskchatapp.domain.usecase.GetCurrentUserUseCase
import com.example.helpdeskchatapp.domain.usecase.LogoutUseCase
import com.example.helpdeskchatapp.ui.common.UiState
import com.example.helpdeskchatapp.ui.model.AdminState
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
) : BaseViewModel<AdminState>() {

    private val _chats = MutableStateFlow<List<ListRowEntity>>(emptyList())
    val chats = _chats.asStateFlow()

    init {
        loadData()
    }

    fun logout(onSuccess: () -> Unit) {
        viewModelScope.launch {
            logoutUseCase()
            onSuccess()
        }
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
