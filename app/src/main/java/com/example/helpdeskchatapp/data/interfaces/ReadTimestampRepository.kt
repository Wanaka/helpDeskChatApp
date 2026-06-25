package com.example.helpdeskchatapp.data.interfaces

interface ReadTimestampRepository {
    fun getLastRead(conversationId: String): Long?
    fun saveLastRead(conversationId: String)
}
