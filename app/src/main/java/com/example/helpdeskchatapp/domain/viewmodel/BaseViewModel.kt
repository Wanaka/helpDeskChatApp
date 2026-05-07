package com.example.helpdeskchatapp.domain.viewmodel

import androidx.lifecycle.ViewModel
import com.example.helpdeskchatapp.ui.common.ToastState
import com.example.helpdeskchatapp.ui.common.UiState
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow

abstract class BaseViewModel<T> : ViewModel() {
    protected val _uiState = MutableStateFlow<UiState<T>>(UiState.Loading)
    val uiState: StateFlow<UiState<T>> = _uiState.asStateFlow()

    protected val _toastEvent = MutableSharedFlow<String>(extraBufferCapacity = 1)
    val toastEvent = _toastEvent.asSharedFlow()

// Trigger it

    abstract fun loadData()
}
