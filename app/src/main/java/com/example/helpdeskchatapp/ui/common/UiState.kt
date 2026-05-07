package com.example.helpdeskchatapp.ui.common

sealed class UiState<out T> {
    data object Loading : UiState<Nothing>()
    data class StaticSuccess<T>(val data: T) : UiState<T>()
    data object Success : UiState<Nothing>()
    data class Error(val message: String) : UiState<Nothing>()
}