package com.example.helpdeskchatapp.domain.viewmodel

import androidx.lifecycle.viewModelScope
import androidx.navigation3.runtime.NavKey
import com.example.helpdeskchatapp.domain.usecase.GetCurrentUserUseCase
import com.example.helpdeskchatapp.domain.usecase.IsAnonymousUseCase
import com.example.helpdeskchatapp.domain.usecase.LogoutUseCase
import com.example.helpdeskchatapp.ui.common.UiState
import com.example.helpdeskchatapp.navigation.AdminRouteKey
import com.example.helpdeskchatapp.navigation.DeepLinkLoadingKey
import com.example.helpdeskchatapp.navigation.LoginRouteKey
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val getCurrentUserUseCase: GetCurrentUserUseCase,
    private val isAnonymousUseCase: IsAnonymousUseCase,
    private val logoutUseCase: LogoutUseCase
) : BaseViewModel() {

    private val _initialRoute = MutableStateFlow<NavKey?>(null)
    val initialRoute = _initialRoute.asStateFlow()

    private val _logoutEvent = MutableSharedFlow<Unit>(extraBufferCapacity = 1)
    val logoutEvent = _logoutEvent.asSharedFlow()

    init {
        loadData()
    }

    override fun loadData() {
        _uiState.value = UiState.Success
    }

    fun resolveInitialRoute(conversationId: String?) {
        viewModelScope.launch {
            _initialRoute.value = computeInitialRoute(conversationId)
        }
    }

    private suspend fun computeInitialRoute(conversationId: String?): NavKey {
        if (conversationId != null) return DeepLinkLoadingKey

        val userId = getCurrentUserUseCase()
        return if (userId != null) {
            if (isAnonymousUseCase()) {
                DeepLinkLoadingKey
            } else {
                AdminRouteKey
            }
        } else {
            LoginRouteKey
        }
    }
}
