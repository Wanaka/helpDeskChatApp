package com.example.helpdeskchatapp.fakes

import com.example.helpdeskchatapp.data.interfaces.AdminRepository
import com.example.helpdeskchatapp.data.interfaces.ChatRepository
import com.example.helpdeskchatapp.data.interfaces.ReadTimestampRepository
import com.example.helpdeskchatapp.data.interfaces.UserRepository
import com.example.helpdeskchatapp.domain.model.consumer.Login
import com.example.helpdeskchatapp.domain.model.consumer.Message
import com.example.helpdeskchatapp.domain.model.consumer.UserName
import com.example.helpdeskchatapp.domain.model.producer.ChatMessageViewEntity
import com.example.helpdeskchatapp.domain.model.producer.ChatViewEntity
import com.example.helpdeskchatapp.domain.model.producer.UserNameViewEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

/**
 * Hand-written fakes for the repository interfaces. Real use cases are constructed on top of
 * these in tests, so `Result` values are produced for real (avoids MockK's inline-value-class
 * boxing issue with `kotlin.Result`).
 */
class FakeUserRepository : UserRepository {
    var loginResult: Result<Unit> = Result.success(Unit)
    var registerResult: Result<Unit> = Result.success(Unit)
    var loginAnonymouslyResult: Result<String> = Result.success("anon-uid")
    var updateUserNameResult: Result<Unit> = Result.success(Unit)
    var getFcmTokenResult: Result<String> = Result.success("fake-fcm-token")
    var currentUserId: String? = null
    var anonymous: Boolean = false
    var logoutCalled: Boolean = false
    var updatedFcmToken: String? = null

    override suspend fun login(params: Login): Result<Unit> = loginResult
    override suspend fun register(params: Login): Result<Unit> = registerResult
    override suspend fun loginAnonymously(): Result<String> = loginAnonymouslyResult
    override suspend fun updateUserName(params: UserName): Result<Unit> = updateUserNameResult
    override suspend fun getFcmToken(): Result<String> = getFcmTokenResult
    override suspend fun updateFcmToken(token: String): Result<Unit> {
        updatedFcmToken = token
        return Result.success(Unit)
    }

    override fun getCurrentUser(): String? = currentUserId
    override fun isAnonymous(): Boolean = anonymous
    override suspend fun logout(): Result<Unit> {
        logoutCalled = true
        return Result.success(Unit)
    }
}

class FakeAdminRepository : AdminRepository {
    var chats: List<ChatViewEntity> = emptyList()
    var userNameViewEntity: UserNameViewEntity = UserNameViewEntity(name = "", company = "")
    var getUserNameResult: Result<UserNameViewEntity> = Result.success(userNameViewEntity)
    var createChatResult: Result<String> = Result.success("conversation-id")
    var chatForUserResult: Result<String?> = Result.success(null)

    override fun getChats(adminId: String): Flow<List<ChatViewEntity>> = flowOf(chats)
    override suspend fun createChat(
        adminId: String,
        userId: String,
        senderName: String
    ): Result<String> = createChatResult

    override suspend fun getUserName(userId: String): Result<UserNameViewEntity> = getUserNameResult
    override suspend fun getChatForUser(userId: String): Result<String?> = chatForUserResult
}

class FakeChatRepository : ChatRepository {
    var messages: List<ChatMessageViewEntity> = emptyList()
    var sendMessageResult: Result<String> = Result.success("message-id")
    var getAdminNameResult: Result<String> = Result.success("Admin")
    val sentMessages = mutableListOf<Message>()

    override fun getMessages(conversationId: String): Flow<List<ChatMessageViewEntity>> =
        flowOf(messages)

    override suspend fun sendMessage(message: Message): Result<String> {
        sentMessages.add(message)
        return sendMessageResult
    }

    override suspend fun getAdminName(conversationId: String): Result<String> = getAdminNameResult
}

class FakeReadTimestampRepository : ReadTimestampRepository {
    private val timestamps = mutableMapOf<String, Long>()
    var fixedTimestamp: Long? = null  // override what getLastRead returns when set

    override fun getLastRead(conversationId: String): Long? =
        fixedTimestamp ?: timestamps[conversationId]

    override fun saveLastRead(conversationId: String) {
        timestamps[conversationId] = System.currentTimeMillis()
    }
}
