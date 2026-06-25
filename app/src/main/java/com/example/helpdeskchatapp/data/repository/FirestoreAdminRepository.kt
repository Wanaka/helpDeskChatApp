package com.example.helpdeskchatapp.data.repository

import com.example.helpdeskchatapp.data.interfaces.AdminRepository
import com.example.helpdeskchatapp.data.mapper.toDomain
import com.example.helpdeskchatapp.data.model.ChatResponse
import com.example.helpdeskchatapp.domain.model.producer.ChatViewEntity
import com.example.helpdeskchatapp.domain.model.producer.UserNameViewEntity
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

    override fun getChats(adminId: String): Flow<List<ChatViewEntity>> = callbackFlow {
        val listener = firestore.collection("conversations")
            .whereEqualTo("adminId", adminId)
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }

                val chats = snapshot?.documents?.map { doc ->
                    ChatResponse(
                        id = doc.id,
                        sender = doc.getString("senderName") ?: "Unknown",
                        company = doc.getString("senderCompany") ?: "",
                        message = doc.getString("lastMessage") ?: "New chat started",
                        adminName = doc.getString("adminName") ?: "",
                        userId = doc.getString("userId") ?: "",
                        lastMessageTimestamp = doc.getTimestamp("lastMessageTimestamp")?.toDate()?.time
                    ).toDomain()
                } ?: emptyList()
                trySend(chats)
            }

        awaitClose {
            listener.remove()
        }
    }

    override suspend fun createChat(adminId: String, userId: String, senderName: String): Result<String> {
        return try {
            val adminNameResult = getUserName(adminId)
            val adminName = adminNameResult.getOrElse { return Result.failure(it) }
            val senderUserResult = getUserName(userId)
            val senderCompany = senderUserResult.getOrElse { UserNameViewEntity("", "") }.company

            val chatData = mapOf(
                "adminId" to adminId,
                "userId" to userId,
                "senderName" to senderName,
                "senderCompany" to senderCompany,
                "adminName" to adminName.name,
                "lastMessage" to "New chat started",
                "timestamp" to Timestamp.now()
            )

            firestore.collection("conversations").document(userId).set(chatData).await()
            Result.success(userId)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getUserName(userId: String): Result<UserNameViewEntity> {
        return try {
            val doc = firestore.collection("users").document(userId).get().await()
            val name = doc.getString("name") ?: ""
            val company = doc.getString("company") ?: ""
            Result.success(UserNameViewEntity(name, company))
        } catch (e: Exception) {
            Result.failure(e)
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
