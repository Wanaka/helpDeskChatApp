package haag.your.next.developer.data.interfaces

import haag.your.next.developer.domain.model.producer.ChatViewEntity
import haag.your.next.developer.domain.model.producer.UserNameViewEntity
import kotlinx.coroutines.flow.Flow

interface AdminRepository {
    fun getChats(adminId: String): Flow<List<ChatViewEntity>>
    suspend fun createChat(adminId: String, userId: String, senderName: String): Result<String>
    suspend fun getUserName(userId: String): Result<UserNameViewEntity>
    suspend fun getChatForUser(userId: String): Result<String?>
}
