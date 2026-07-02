package haag.your.next.developer.data.interfaces

import haag.your.next.developer.domain.model.producer.ChatMessageViewEntity
import haag.your.next.developer.domain.model.consumer.Message
import kotlinx.coroutines.flow.Flow

interface ChatRepository {
    fun getMessages(conversationId: String): Flow<List<ChatMessageViewEntity>>
    suspend fun sendMessage(message: Message): Result<String>
    suspend fun getAdminName(conversationId: String): Result<String>
}
