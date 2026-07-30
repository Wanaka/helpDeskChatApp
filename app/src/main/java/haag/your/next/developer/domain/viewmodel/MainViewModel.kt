package haag.your.next.developer.domain.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation3.runtime.NavKey
import haag.your.next.developer.domain.usecase.GetUserSessionUseCase
import haag.your.next.developer.domain.usecase.SavePendingAdminIdUseCase
import haag.your.next.developer.navigation.AdminRouteKey
import haag.your.next.developer.navigation.DeepLinkLoadingKey
import haag.your.next.developer.navigation.LoginRouteKey
import haag.your.next.developer.util.checkInstallReferrer
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    application: Application,
    private val getUserSessionUseCase: GetUserSessionUseCase,
    private val savePendingAdminIdUseCase: SavePendingAdminIdUseCase,
) : AndroidViewModel(application) {

    private val _initialRoute = MutableStateFlow<NavKey?>(null)
    val initialRoute = _initialRoute.asStateFlow()

    init {
        checkInstallReferrer(getApplication()) { adminId ->
            viewModelScope.launch { savePendingAdminIdUseCase(adminId) }
        }
    }

    fun resolveInitialRoute(conversationId: String?) {
        viewModelScope.launch {
            _initialRoute.value = computeInitialRoute(conversationId)
        }
    }

    private suspend fun computeInitialRoute(conversationId: String?): NavKey {
        if (conversationId != null) return DeepLinkLoadingKey
        val session = getUserSessionUseCase()
        return if (session.userId != null) {
            if (session.isAnonymous) DeepLinkLoadingKey else AdminRouteKey
        } else {
            LoginRouteKey
        }
    }
}
