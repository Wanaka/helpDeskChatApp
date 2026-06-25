package com.example.helpdeskchatapp.domain.viewmodel

import androidx.lifecycle.viewModelScope
import com.example.helpdeskchatapp.domain.model.consumer.Login
import com.example.helpdeskchatapp.domain.usecase.PostAuthSetupUseCase
import com.example.helpdeskchatapp.domain.usecase.RegisterUseCase
import com.example.helpdeskchatapp.ui.common.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val registerUseCase: RegisterUseCase,
    private val postAuthSetupUseCase: PostAuthSetupUseCase
) : BaseViewModel() {

    private val _navigateToAdmin = MutableSharedFlow<Unit>(extraBufferCapacity = 1)
    val navigateToAdmin = _navigateToAdmin.asSharedFlow()

    init {
        loadData()
    }

    override fun loadData() {
        _uiState.value = UiState.Success
    }

    fun register(params: Login) {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            registerUseCase(params).fold(
                onSuccess = {
                    postAuthSetupUseCase()
                        .onFailure { _toastEvent.emit("Failed to update notification token") }
                    _uiState.value = UiState.Success
                    _navigateToAdmin.emit(Unit)
                },
                onFailure = { error ->
                    _uiState.value = UiState.Error(error.message ?: "Registration failed")
                }
            )
        }
    }
}
