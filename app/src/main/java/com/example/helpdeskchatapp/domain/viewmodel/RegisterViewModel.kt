package com.example.helpdeskchatapp.domain.viewmodel

import androidx.lifecycle.viewModelScope
import com.example.helpdeskchatapp.domain.model.LoginParams
import com.example.helpdeskchatapp.domain.usecase.GetCurrentUserUseCase
import com.example.helpdeskchatapp.domain.usecase.RegisterUseCase
import com.example.helpdeskchatapp.domain.usecase.UpdateFcmTokenUseCase
import com.example.helpdeskchatapp.util.CurrentUserId
import com.example.helpdeskchatapp.ui.common.UiState
import com.example.helpdeskchatapp.ui.model.LoginState
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val registerUseCase: RegisterUseCase,
    private val getCurrentUserUseCase: GetCurrentUserUseCase,
    private val updateFcmTokenUseCase: UpdateFcmTokenUseCase
) : BaseViewModel<LoginState>() {

    init {
        loadData()
    }

    override fun loadData() {
        _uiState.value = UiState.StaticSuccess(LoginState())
    }

    fun register(params: LoginParams) {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            
            val result = registerUseCase(params)

            result.fold(
                onSuccess = { message ->
                    getCurrentUserUseCase()?.let { uid ->
                        CurrentUserId.CURRENT_USER_ID = uid

                        viewModelScope.launch {
                            try {
                                val token = FirebaseMessaging.getInstance().token.await()
                                updateFcmTokenUseCase(token)
                            } catch (e: Exception) {
                            }
                        }
                    }
                    _uiState.value = UiState.StaticSuccess(LoginState(loginResult = message))
                },
                onFailure = { error ->
                    _uiState.value = UiState.Error(error.message ?: "Registration failed")
                }
            )
        }
    }
}
