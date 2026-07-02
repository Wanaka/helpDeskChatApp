package haag.your.next.developer.domain.viewmodel

import androidx.lifecycle.viewModelScope
import androidx.navigation3.runtime.NavKey
import haag.your.next.developer.domain.usecase.GetCurrentUserUseCase
import haag.your.next.developer.domain.usecase.IsAnonymousUseCase
import haag.your.next.developer.domain.usecase.LogoutUseCase
import haag.your.next.developer.ui.common.UiState
import haag.your.next.developer.navigation.AdminRouteKey
import haag.your.next.developer.navigation.DeepLinkLoadingKey
import haag.your.next.developer.navigation.LoginRouteKey
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
) : BaseViewModel() {

    private val _initialRoute = MutableStateFlow<NavKey?>(null)
    val initialRoute = _initialRoute.asStateFlow()


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
