package com.example.helpdeskchatapp.data.repository

import com.example.helpdeskchatapp.domain.model.consumer.Login
import com.example.helpdeskchatapp.util.failedTask
import com.example.helpdeskchatapp.util.succeededTask
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessaging
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertTrue
import org.junit.Test

class FirebaseUserRepositoryTest {

    private val auth = mockk<FirebaseAuth>()
    private val firestore = mockk<FirebaseFirestore>(relaxed = true)
    private val messaging = mockk<FirebaseMessaging>(relaxed = true)

    private fun repository() = FirebaseUserRepository(auth, firestore, messaging)

    @Test
    fun `login_whenFirebaseSucceeds_returnsSuccess`() = runTest {
        every {
            auth.signInWithEmailAndPassword("admin@x.com", "pw")
        } returns succeededTask(mockk<AuthResult>())

        val result = repository().login(Login("admin@x.com", "pw"))

        assertTrue(result.isSuccess)
    }

    @Test
    fun `login_whenFirebaseThrows_returnsFailure`() = runTest {
        every {
            auth.signInWithEmailAndPassword(any(), any())
        } returns failedTask(RuntimeException("invalid credentials"))

        val result = repository().login(Login("admin@x.com", "wrong"))

        assertTrue(result.isFailure)
    }

    @Test
    fun `register_whenFirebaseSucceeds_returnsSuccess`() = runTest {
        val authResult = mockk<AuthResult> { every { user } returns null }
        every {
            auth.createUserWithEmailAndPassword("admin@x.com", "pw")
        } returns succeededTask(authResult)

        val result = repository().register(Login("admin@x.com", "pw"))

        assertTrue(result.isSuccess)
    }

    @Test
    fun `register_whenFirebaseThrows_returnsFailure`() = runTest {
        every {
            auth.createUserWithEmailAndPassword(any(), any())
        } returns failedTask(RuntimeException("email already in use"))

        val result = repository().register(Login("admin@x.com", "pw"))

        assertTrue(result.isFailure)
    }
}
