package haag.your.next.developer.domain.viewmodel

import androidx.lifecycle.ViewModel
import haag.your.next.developer.ui.common.UiState
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow

abstract class BaseViewModel : ViewModel() {
    protected val _uiState = MutableStateFlow<UiState>(UiState.Loading)
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    protected val _toastEvent = MutableSharedFlow<String>(extraBufferCapacity = 1)
    val toastEvent = _toastEvent.asSharedFlow()

// Trigger it

    open fun loadData() {}
}
