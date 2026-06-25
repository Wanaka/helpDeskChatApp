package com.example.helpdeskchatapp.data.repository

import com.example.helpdeskchatapp.data.interfaces.ChatRepository
import com.example.helpdeskchatapp.data.mapper.toDomain
import com.example.helpdeskchatapp.data.model.ChatMessageResponse
import com.example.helpdeskchatapp.domain.model.producer.ChatMessageViewEntity
import com.example.helpdeskchatapp.domain.model.consumer.Message
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class FirestoreChatRepository @Inject constructor(
    private val firestore: FirebaseFirestore
) : ChatRepository {

    override fun getMessages(conversationId: String): Flow<List<ChatMessageViewEntity>> = callbackFlow {
        val listener = firestore.collection("conversations")
            .document(conversationId)
            .collection("messages")
            .orderBy("timestamp", Query.Direction.ASCENDING)
            .addSnapshotListener { snapshot, error ->

                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }

                val messages = snapshot?.documents?.mapNotNull { doc ->
                    ChatMessageResponse(
                        id = doc.id,
                        text = doc.getString("messageText") ?: "",
                        senderId = doc.getString("senderId") ?: "",
                        timestamp = (doc.getTimestamp("timestamp") ?: Timestamp.now()).toDate().time
                    ).toDomain()
                } ?: emptyList()

                trySend(messages)
            }

        awaitClose {
            listener.remove()
        }
    }

    override suspend fun getAdminName(conversationId: String): Result<String> {
        return try {
            val doc = firestore.collection("conversations").document(conversationId).get().await()
            val adminName = doc.getString("adminName") ?: ""
            Result.success(adminName)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun sendMessage(message: Message): Result<String> {
        return try {
            val now = Timestamp.now()
            val conversationRef = firestore.collection("conversations").document(message.conversationId)
            conversationRef.collection("messages").add(
                mapOf(
                    "messageText" to message.message,
                    "senderId" to message.senderId,
                    "timestamp" to now
                )
            ).await()
            conversationRef.update(mapOf(
                "lastMessage" to message.message,
                "lastMessageTimestamp" to now
            )).await()
            Result.success("Message sent successfully")
        } catch (e: Exception) {
            Result.failure(Exception("Message not sent"))
        }
    }
}
