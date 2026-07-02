package haag.your.next.developer.domain.usecase

import haag.your.next.developer.data.interfaces.ChatRepository
import haag.your.next.developer.domain.model.producer.ChatMessageViewEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetChatMessagesUseCase @Inject constructor(
    private val repository: ChatRepository
) : FlowUseCase<String, List<ChatMessageViewEntity>>() {
    override fun invoke(params: String): Flow<List<ChatMessageViewEntity>> {
        return repository.getMessages(params)
    }
}
