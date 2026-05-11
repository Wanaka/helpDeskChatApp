package com.example.helpdeskchatapp.data.repository

import com.example.helpdeskchatapp.data.interfaces.AdminRepository
import com.example.helpdeskchatapp.domain.model.ChatViewEntity
import com.example.helpdeskchatapp.util.CurrentUserId
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
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

    override suspend fun createChat(adminId: String, userId: String, senderName: String): Result<String> {
        return try {
            val adminName = getAdminName(adminId)
            
            val chatData = mapOf(
                "adminId" to adminId,
                "userId" to userId,
                "senderName" to senderName,
                "adminName" to adminName,
                "lastMessage" to "New chat started",
                "timestamp" to Timestamp.now()
            )
            
            val docRef = firestore.collection("conversations").add(chatData).await()
            Result.success(docRef.id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getAdminName(adminId: String): String {
        return try {
            val doc = firestore.collection("users").document(adminId).get().await()
            doc.getString("name") ?: "Admin"
        } catch (e: Exception) {
            "Admin"
        }
    }

    override suspend fun getChatForUser(userId: String): Result<String?> {
        return try {
            val snapshot = firestore.collection("conversations")
                .whereEqualTo("userId", userId)
                .get()
                .await()
            
            // If there are multiple, sort them locally to avoid needing a composite index in Firestore
            val lastChat = snapshot.documents
                .sortedByDescending { it.getTimestamp("timestamp") }
                .firstOrNull()
            
            Result.success(lastChat?.id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
