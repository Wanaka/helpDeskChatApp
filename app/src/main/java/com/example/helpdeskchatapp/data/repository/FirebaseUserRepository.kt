package com.example.helpdeskchatapp.data.repository

import com.example.helpdeskchatapp.data.interfaces.UserRepository
import com.example.helpdeskchatapp.domain.model.LoginParams
import com.example.helpdeskchatapp.util.CurrentUserId
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withTimeoutOrNull
import javax.inject.Inject

class FirebaseUserRepository @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore,
) : UserRepository {
    
    override suspend fun login(params: LoginParams): Result<String> {
        return try {
            val result = auth.signInWithEmailAndPassword(params.email, params.password).await()
            Result.success("Welcome, ${result.user?.email}!")
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun register(params: LoginParams): Result<String> {
        return try {
            val result = auth.createUserWithEmailAndPassword(params.email, params.password).await()
            val user = result.user
            if (user != null) {
                val userData = mapOf(
                    "userId" to user.uid,
                    "email" to params.email,
                    "name" to "" 
                )
                firestore.collection("users").document(user.uid).set(userData).await()
            }
            Result.success("Account created for ${result.user?.email}!")
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateUserName(params: Pair<String, String>): Result<Unit> {
        return try {
            val uid = auth.currentUser?.uid ?: throw Exception("No user logged in")
            val userRef = firestore.collection("users").document(uid)
            val doc = userRef.get().await()
            
            if (doc.exists()) {
                userRef.update("name", params.first).await()
            } else {
                val userData = mapOf(
                    "userId" to uid,
                    "name" to params.first,
                    "company" to params.second,
                    "email" to (auth.currentUser?.email ?: "")
                )
                userRef.set(userData).await()
            }
            Result.success(Unit)
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
        val uid = auth.currentUser?.uid
        CurrentUserId.CURRENT_USER_ID = ""
        
        if (uid != null) {
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    // Delete token from device and clear from Firestore
                    FirebaseMessaging.getInstance().deleteToken().await()

                    // Give it a timeout so it doesn't hang forever if offline
                    withTimeoutOrNull(2000) {
                        firestore.collection("users").document(uid)
                            .update("fcmToken", null).await()
                    }
                } catch (e: Exception) {
                    // Ignore errors during token clearing
                } finally {
                    auth.signOut()
                }
            }
        } else {
            auth.signOut()
        }
    }

    override suspend fun updateFcmToken(token: String): Result<Unit> {
        return try {
            val uid = auth.currentUser?.uid ?: return Result.success(Unit)
            // Use set with merge so it works even if the document doesn't exist yet
            firestore.collection("users").document(uid)
                .set(mapOf("fcmToken" to token), SetOptions.merge()).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}