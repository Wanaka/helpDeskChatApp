package haag.your.next.developer.data.repository

import haag.your.next.developer.data.interfaces.UserRepository
import haag.your.next.developer.domain.model.consumer.Login
import haag.your.next.developer.domain.model.consumer.UserName
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeoutOrNull
import javax.inject.Inject

class FirebaseUserRepository @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore,
    private val messaging: FirebaseMessaging,
) : UserRepository {
    
    override suspend fun login(params: Login): Result<Unit> {
        return try {
            auth.signInWithEmailAndPassword(params.email, params.password).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun register(params: Login): Result<Unit> {
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
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateUserName(params: UserName): Result<Unit> {
        return try {
            val uid = auth.currentUser?.uid ?: throw Exception("No user logged in")
            val userData = mapOf(
                "userId" to uid,
                "name" to params.name,
                "company" to params.company,
                "email" to (auth.currentUser?.email ?: "")
            )
            firestore.collection("users").document(uid)
                .set(userData, SetOptions.merge()).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun loginAnonymously(): Result<String> {
        return try {
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

    override suspend fun logout(): Result<Unit> {
        return try {
            val uid = auth.currentUser?.uid
            if (uid != null) {
                withContext(Dispatchers.IO) {
                    try {
                        // Delete token from device and clear from Firestore
                        messaging.deleteToken().await()

                        // Give it a timeout so it doesn't hang forever if offline
                        withTimeoutOrNull(2000) {
                            firestore.collection("users").document(uid)
                                .update("fcmToken", null).await()
                        }
                    } catch (e: Exception) {
                        // Ignore errors during token clearing — sign-out still proceeds
                    } finally {
                        auth.signOut()
                    }
                }
            } else {
                auth.signOut()
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getFcmToken(): Result<String> {
        return try {
            val token = messaging.token.await()
            Result.success(token)
        } catch (e: Exception) {
            Result.failure(e)
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