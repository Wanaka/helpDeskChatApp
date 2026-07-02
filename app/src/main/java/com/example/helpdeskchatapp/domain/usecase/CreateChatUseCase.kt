package haag.your.next.developer.domain.usecase

import haag.your.next.developer.data.interfaces.AdminRepository
import haag.your.next.developer.domain.model.consumer.CreateChat
import javax.inject.Inject

class CreateChatUseCase @Inject constructor(
    private val repository: AdminRepository
) : UseCase<CreateChat, Result<String>>() {
    override suspend fun invoke(params: CreateChat): Result<String> {
        return repository.createChat(params.adminId, params.userId, params.senderName)
    }
}
