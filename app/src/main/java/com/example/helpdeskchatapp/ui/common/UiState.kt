package com.example.helpdeskchatapp.ui.common

// UiState is a pure loading/error/success signal — it carries no data payload.
// Screen data flows through dedicated StateFlow fields on each ViewModel.
// The generic parameter has been intentionally removed; parallel data StateFlows
// are the correct mechanism for typed screen data.
sealed class UiState {
    data object Loading : UiState()
    data object Success : UiState()
    data class Error(val message: String) : UiState()
}
