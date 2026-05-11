package com.example.helpdeskchatapp.data.repository

import com.example.helpdeskchatapp.data.interfaces.AdminRepository
import com.example.helpdeskchatapp.domain.model.ChatViewEntity
import com.example.helpdeskchatapp.util.CurrentUserId
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject

class FirestoreAdminRepository @Inject constructor(
    private val firestore: FirebaseFirestore
) : AdminRepository {

    override fun getChats(): Flow<List<ChatViewEntity>> = callbackFlow {
        val adminId = CurrentUserId.CURRENT_USER_ID
        val listener = firestore.collection("conversations")
            .whereEqualTo("adminId", adminId)
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }

                val chats = snapshot?.documents?.mapNotNull { doc ->
                    ChatViewEntity(
                        id = doc.id,
                        sender = doc.getString("senderName") ?: "Unknown",
                        message = doc.getString("lastMessage") ?: "",
                        adminName = doc.getString("adminName") ?: "",
                        userId = doc.getString("userId") ?: ""
                    )
                } ?: emptyList()

                trySend(chats)
            }

        awaitClose {
            listener.remove()
        }
    }
}
