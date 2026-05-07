package com.example.helpdeskchatapp.data.repository

import com.example.helpdeskchatapp.data.interfaces.UserRepository
import com.example.helpdeskchatapp.domain.model.LoginParams
import com.example.helpdeskchatapp.util.CurrentUserId
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class FirebaseUserRepository @Inject constructor(
    private val auth: FirebaseAuth
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
            Result.success("Account created for ${result.user?.email}!")
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun getCurrentUser(): String? {
        return auth.currentUser?.uid
    }

    override fun logout() {
        auth.signOut()
        CurrentUserId.CURRENT_USER_ID = ""
    }
}