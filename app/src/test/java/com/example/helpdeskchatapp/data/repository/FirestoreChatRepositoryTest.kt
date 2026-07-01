package com.example.helpdeskchatapp.data.repository

import com.example.helpdeskchatapp.domain.model.consumer.Message
import com.example.helpdeskchatapp.util.failedTask
import com.example.helpdeskchatapp.util.succeededTask
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class FirestoreChatRepositoryTest {

    private val firestore = mockk<FirebaseFirestore>()
    private val conversationsCollection = mockk<CollectionReference>()
    private val conversationDocument = mockk<DocumentReference>()
    private val messagesCollection = mockk<CollectionReference>()
    private val messagesDocument = mockk<DocumentReference>()

    private fun repository() = FirestoreChatRepository(firestore)

    private fun stubConversationDocument(conversationId: String) {
        every { firestore.collection("conversations") } returns conversationsCollection
        every { conversationsCollection.document(conversationId) } returns conversationDocument
    }

    // ── getAdminName ─────────────────────────────────────────────────────────

    @Test
    fun `getAdminName_whenFieldPresent_returnsAdminName`() = runTest {
        val snapshot = mockk<DocumentSnapshot> {
            every { getString("adminName") } returns "Alice Support"
        }
        stubConversationDocument("conv-1")
        every { conversationDocument.get() } returns succeededTask(snapshot)

        val result = repository().getAdminName("conv-1")

        assertTrue(result.isSuccess)
        assertEquals("Alice Support", result.getOrNull())
    }

    @Test
    fun `getAdminName_whenFieldMissing_returnsEmptyString`() = runTest {
        val snapshot = mockk<DocumentSnapshot> {
            every { getString("adminName") } returns null
        }
        stubConversationDocument("conv-1")
        every { conversationDocument.get() } returns succeededTask(snapshot)

        val result = repository().getAdminName("conv-1")

        assertTrue(result.isSuccess)
        assertEquals("", result.getOrNull())
    }

    @Test
    fun `getAdminName_whenFirestoreThrows_returnsFailure`() = runTest {
        stubConversationDocument("conv-1")
        every { conversationDocument.get() } returns failedTask(RuntimeException("offline"))

        val result = repository().getAdminName("conv-1")

        assertTrue(result.isFailure)
    }

    // ── sendMessage ──────────────────────────────────────────────────────────

    @Test
    fun `sendMessage_whenFirestoreSucceeds_returnsSuccess`() = runTest {
        stubConversationDocument("conv-1")
        every { conversationDocument.collection("messages") } returns messagesCollection
        every { messagesCollection.add(any()) } returns succeededTask(messagesDocument)
        every { conversationDocument.update(any<Map<String, Any>>()) } returns succeededTask(null)

        val message = Message(
            message = "Hello",
            senderId = "user-1",
            conversationId = "conv-1"
        )
        val result = repository().sendMessage(message)

        assertTrue(result.isSuccess)
    }

    @Test
    fun `sendMessage_whenAddFails_returnsFailure`() = runTest {
        stubConversationDocument("conv-1")
        every { conversationDocument.collection("messages") } returns messagesCollection
        every { messagesCollection.add(any()) } returns failedTask(RuntimeException("write denied"))

        val message = Message(
            message = "Hello",
            senderId = "user-1",
            conversationId = "conv-1"
        )
        val result = repository().sendMessage(message)

        assertTrue(result.isFailure)
    }

    @Test
    fun `sendMessage_whenUpdateFails_returnsFailure`() = runTest {
        stubConversationDocument("conv-1")
        every { conversationDocument.collection("messages") } returns messagesCollection
        every { messagesCollection.add(any()) } returns succeededTask(messagesDocument)
        every { conversationDocument.update(any<Map<String, Any>>()) } returns
            failedTask(RuntimeException("update denied"))

        val message = Message(
            message = "Hello",
            senderId = "user-1",
            conversationId = "conv-1"
        )
        val result = repository().sendMessage(message)

        assertTrue(result.isFailure)
    }
}
