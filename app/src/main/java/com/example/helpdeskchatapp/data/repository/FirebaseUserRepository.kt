package com.example.helpdeskchatapp.data.repository

import com.example.helpdeskchatapp.data.interfaces.UserRepository
import com.example.helpdeskchatapp.domain.model.LoginParams
import com.example.helpdeskchatapp.util.CurrentUserId
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class FirebaseUserRepository @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore,
) : UserRepository {
    
    override suspend fun login(params: LoginParams): Result<String> {
        return try {
            val result = auth.signInWithEmailAndPassword(params.username, params.password).await()
            Result.success("Welcome, ${result.user?.email}!")
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun register(params: LoginParams): Result<String> {
        return try {
            val result = auth.createUserWithEmailAndPassword(params.username, params.password).await()
            val user = result.user
            if (user != null) {
                // Create user document in Firestore
                val userData = mapOf(
                    "email" to params.username,
                    "name" to params.username.substringBefore("@") // Default name from email
                )
                firestore.collection("users").document(user.uid).set(userData).await()
            }
            Result.success("Account created for ${result.user?.email}!")
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun loginAnonymously(): Result<String> {
        return try {
            println(",,, Attempting anonymous login")
            val result = auth.signInAnonymously().await()
            Result.success("Logged in anonymously with ID: ${result.user?.uid}")
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun getCurrentUser(): String? {
        return auth.currentUser?.uid
    }

    override fun isAnonymous(): Boolean {
        return auth.currentUser?.isAnonymous ?: false
    }

    override fun logout() {
        auth.signOut()
        CurrentUserId.CURRENT_USER_ID = ""
    }
}