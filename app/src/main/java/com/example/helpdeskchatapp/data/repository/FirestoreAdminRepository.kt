package com.example.helpdeskchatapp.data.repository

import com.example.helpdeskchatapp.data.interfaces.AdminRepository
import com.example.helpdeskchatapp.domain.model.ChatViewEntity
import com.example.helpdeskchatapp.util.CurrentUserId
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class FirestoreAdminRepository @Inject constructor(
    private val firestore: FirebaseFirestore
) : AdminRepository {

    override suspend fun getChats(): List<ChatViewEntity> {
        return try {
            val adminId = CurrentUserId.CURRENT_USER_ID
            val snapshot = firestore.collection("conversations")
                .whereEqualTo("adminId", adminId)
                .orderBy("timestamp", com.google.firebase.firestore.Query.Direction.DESCENDING)
                .get()
                .await()

            println(",,, getChats:  "+ snapshot.documents)
            snapshot.documents.mapNotNull { doc ->
                ChatViewEntity(
                    id = doc.id,
                    sender = doc.getString("senderName") ?: "Unknown",
                    message = doc.getString("lastMessage") ?: "",
                    adminName = doc.getString("adminName") ?: "",
                    userId = doc.getString("userId") ?: ""
                )
            }
        } catch (e: Exception) {
            println(",,, getChats:  empytList "+ e.message)
            emptyList()

        }
    }
}
