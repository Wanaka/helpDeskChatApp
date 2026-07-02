package haag.your.next.developer.data.repository

import haag.your.next.developer.domain.model.producer.UserNameViewEntity
import haag.your.next.developer.util.failedTask
import haag.your.next.developer.util.succeededTask
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

class FirestoreAdminRepositoryTest {

    private val firestore = mockk<FirebaseFirestore>()
    private val collection = mockk<CollectionReference>()
    private val document = mockk<DocumentReference>()

    private fun repository() = FirestoreAdminRepository(firestore)

    private fun stubUserDocument(documentId: String, snapshot: DocumentSnapshot) {
        every { firestore.collection("users") } returns collection
        every { collection.document(documentId) } returns document
        every { document.get() } returns succeededTask(snapshot)
    }

    @Test
    fun `getUserName_whenDocumentExists_returnsNameAndCompany`() = runTest {
        val snapshot = mockk<DocumentSnapshot> {
            every { getString("name") } returns "Bob"
            every { getString("company") } returns "Acme"
        }
        stubUserDocument("u1", snapshot)

        val result = repository().getUserName("u1")

        assertTrue(result.isSuccess)
        assertEquals(UserNameViewEntity(name = "Bob", company = "Acme"), result.getOrNull())
    }

    @Test
    fun `getUserName_whenFieldsMissing_defaultsToEmptyStrings`() = runTest {
        val snapshot = mockk<DocumentSnapshot> {
            every { getString("name") } returns null
            every { getString("company") } returns null
        }
        stubUserDocument("u1", snapshot)

        val result = repository().getUserName("u1")

        assertTrue(result.isSuccess)
        assertEquals(UserNameViewEntity(name = "", company = ""), result.getOrNull())
    }

    @Test
    fun `getUserName_whenFirestoreThrows_returnsFailure`() = runTest {
        every { firestore.collection("users") } returns collection
        every { collection.document("u1") } returns document
        every { document.get() } returns failedTask(RuntimeException("offline"))

        val result = repository().getUserName("u1")

        assertTrue(result.isFailure)
    }
}
