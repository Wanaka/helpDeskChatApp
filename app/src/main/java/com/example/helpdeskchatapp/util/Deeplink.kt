package com.example.helpdeskchatapp.util

object Deeplink {
    private const val CHAT_DEEPLINK = "com.example.helpdeskchatapp://chat/"
    
    fun getChatDeeplink(adminId: String): String {
        return "$CHAT_DEEPLINK$adminId"
    }
}
