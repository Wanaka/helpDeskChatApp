package haag.your.next.developer.domain.usecase

import haag.your.next.developer.data.interfaces.ChatRepository
import haag.your.next.developer.domain.model.consumer.Message
import javax.inject.Inject

class SendMessageUseCase @Inject constructor(
    private val repository: ChatRepository
) : UseCase<Message, Result<String>>() {
    override suspend fun invoke(params: Message): Result<String> {
        return repository.sendMessage(params)
    }
}
