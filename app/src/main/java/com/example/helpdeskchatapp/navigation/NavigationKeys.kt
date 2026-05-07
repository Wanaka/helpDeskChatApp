package com.example.helpdeskchatapp.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
data object LoginRouteKey : NavKey

@Serializable
data object RegisterRouteKey : NavKey

@Serializable
data object AdminRouteKey : NavKey

@Serializable
data class ChatRouteKey(val conversationId: String) : NavKey
