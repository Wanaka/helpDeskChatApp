package haag.your.next.developer.domain.viewmodel

import androidx.lifecycle.viewModelScope
import haag.your.next.developer.domain.model.consumer.Login
import haag.your.next.developer.domain.usecase.LoginUseCase
import haag.your.next.developer.domain.usecase.PostAuthSetupUseCase
import haag.your.next.developer.domain.usecase.RegisterUseCase
import haag.your.next.developer.ui.common.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase,
    private val registerUseCase: RegisterUseCase,
    private val postAuthSetupUseCase: PostAuthSetupUseCase
) : BaseViewModel() {

    private val _navigateToAdmin = MutableSharedFlow<Unit>(extraBufferCapacity = 1)
    val navigateToAdmin = _navigateToAdmin.asSharedFlow()

    init {
        _uiState.value = UiState.Success
    }

    fun login(params: Login) = authenticate(params, isRegister = false)

    fun register(params: Login) = authenticate(params, isRegister = true)

    private fun authenticate(params: Login, isRegister: Boolean) {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            val result = if (isRegister) registerUseCase(params) else loginUseCase(params)
            result.fold(
                onSuccess = {
                    postAuthSetupUseCase()
                        .onFailure { _toastEvent.emit("Failed to update notification token") }
                    _uiState.value = UiState.Success
                    _navigateToAdmin.emit(Unit)
                },
                onFailure = { error ->
                    _uiState.value = UiState.Error(error.message ?: if (isRegister) "Registration failed" else "Login failed")
                }
            )
        }
    }
}
