package haag.your.next.developer.domain.viewmodel

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import haag.your.next.developer.domain.mapper.withBadges
import haag.your.next.developer.domain.model.consumer.UserName
import haag.your.next.developer.domain.usecase.CheckAdminNameUseCase
import haag.your.next.developer.domain.usecase.GetAdminChatsUseCase
import haag.your.next.developer.domain.usecase.GetLocalReadTimestampUseCase
import haag.your.next.developer.domain.usecase.LogoutUseCase
import haag.your.next.developer.domain.usecase.UpdateUserNameUseCase
import haag.your.next.developer.ui.common.ActiveChatTracker
import haag.your.next.developer.ui.common.UiState
import haag.your.next.developer.ui.model.ListRowEntity
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AdminViewModel @Inject constructor(
    private val getAdminChatsUseCase: GetAdminChatsUseCase,
    private val logoutUseCase: LogoutUseCase,
    private val checkAdminNameUseCase: CheckAdminNameUseCase,
    private val updateUserNameUseCase: UpdateUserNameUseCase,
    private val getLocalReadTimestampUseCase: GetLocalReadTimestampUseCase
) : BaseViewModel() {

    private val _chats = MutableStateFlow<List<ListRowEntity>>(emptyList())
    val chats = _chats.asStateFlow()

    private val _adminId = MutableStateFlow("")
    val adminId = _adminId.asStateFlow()

    private val _showNameOverlay = MutableStateFlow(false)
    val showNameOverlay = _showNameOverlay.asStateFlow()

    private val _logoutEvent = MutableSharedFlow<Unit>(extraBufferCapacity = 1)
    val logoutEvent = _logoutEvent.asSharedFlow()

    init {
        checkAdminName()
        loadData()
    }

    private fun checkAdminName() {
        viewModelScope.launch {
            checkAdminNameUseCase()
                .onSuccess { shouldShow ->
                    if (shouldShow) _showNameOverlay.value = true
                }
                .onFailure {
                    _toastEvent.emit("Failed to load admin name")
                }
        }
    }

    fun updateName(data: UserName) {
        viewModelScope.launch {
            updateUserNameUseCase(data).onSuccess {
                _showNameOverlay.value = false
            }.onFailure {
                _toastEvent.emit("Failed to update name")
            }
        }
    }

    fun removeBadgeWhenChatOpens(conversationId: String) {
        _chats.value = _chats.value.map { entity ->
            if (entity.id == conversationId) entity.copy(showBadge = false) else entity
        }
    }

    fun logout() {
        viewModelScope.launch {
            logoutUseCase()
                .onFailure { _toastEvent.emit("Logout failed: ${it.message}") }
            _logoutEvent.emit(Unit)
        }
    }

    override fun loadData() {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            getAdminChatsUseCase()
                .onFailure { _uiState.value = UiState.Error(it.message ?: "User not authenticated") }
                .onSuccess { data ->
                    _adminId.value = data.adminId
                    try {
                        data.chats.collect { chats ->
                            _chats.value = chats.withBadges(
                                activeConversationId = ActiveChatTracker.currentConversationId,
                                getLastRead = getLocalReadTimestampUseCase::invoke
                            )
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
}
