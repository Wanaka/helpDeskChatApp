package com.example.helpdeskchatapp.ui.common

sealed class ToastState<out T> {
    data object Idle : ToastState<Nothing>()
    data class Error(val message: String) : ToastState<Nothing>()
}