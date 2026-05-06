package com.example.helpdeskchatapp.domain.viewmodel

import androidx.lifecycle.viewModelScope
import com.example.helpdeskchatapp.domain.model.LoginParams
import com.example.helpdeskchatapp.domain.usecase.LoginUseCase
import com.example.helpdeskchatapp.ui.common.UiState
import com.example.helpdeskchatapp.ui.model.LoginState
import kotlinx.coroutines.launch

class LoginViewModel(
    private val loginUseCase: LoginUseCase = LoginUseCase()
) : BaseViewModel<LoginState>() {

    init {
        loadData()
    }

    override fun loadData() {
        _uiState.value = UiState.Success(LoginState())
    }

    fun login(params: LoginParams) {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            
            val result = loginUseCase(LoginParams("a@s.se", "123456"))

            result.fold(
                onSuccess = { message ->
                    _uiState.value = UiState.Success(LoginState(loginResult = message))
                },
                onFailure = { error ->
                    _uiState.value = UiState.Error(error.message ?: "Login failed")
                }
            )
        }
    }
}
