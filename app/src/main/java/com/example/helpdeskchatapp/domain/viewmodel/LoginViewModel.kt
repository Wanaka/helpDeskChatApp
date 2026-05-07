package com.example.helpdeskchatapp.domain.viewmodel

import androidx.lifecycle.viewModelScope
import com.example.helpdeskchatapp.domain.model.LoginParams
import com.example.helpdeskchatapp.domain.usecase.LoginUseCase
import com.example.helpdeskchatapp.data.interfaces.UserRepository
import com.example.helpdeskchatapp.util.CurrentUserId
import com.example.helpdeskchatapp.ui.common.UiState
import com.example.helpdeskchatapp.ui.model.LoginState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase,
    private val userRepository: UserRepository
) : BaseViewModel<LoginState>() {

    init {
        loadData()
    }

    override fun loadData() {
        _uiState.value = UiState.StaticSuccess(LoginState())
    }

    fun login(params: LoginParams) {
        viewModelScope.launch {
            _uiState.value = UiState.Loading

            val result = loginUseCase(params)

            result.fold(
                onSuccess = { message ->
                    userRepository.getCurrentUser()?.let { uid ->
                        CurrentUserId.CURRENT_USER_ID = uid
                    }
                    _uiState.value = UiState.StaticSuccess(LoginState(loginResult = message))
                },
                onFailure = { error ->
                    _uiState.value = UiState.Error(error.message ?: "Login failed")
                }
            )
        }
    }
}
