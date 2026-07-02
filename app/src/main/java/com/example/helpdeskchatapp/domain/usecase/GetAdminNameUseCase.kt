package haag.your.next.developer.domain.usecase

import haag.your.next.developer.data.interfaces.ChatRepository
import javax.inject.Inject

class GetAdminNameUseCase @Inject constructor(
    private val chatRepository: ChatRepository
) : UseCase<String, Result<String>>() {
    override suspend fun invoke(params: String): Result<String> =
        chatRepository.getAdminName(params)
}
