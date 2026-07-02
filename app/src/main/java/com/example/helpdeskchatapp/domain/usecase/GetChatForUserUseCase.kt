package haag.your.next.developer.domain.usecase

import haag.your.next.developer.data.interfaces.AdminRepository
import javax.inject.Inject

class GetChatForUserUseCase @Inject constructor(
    private val repository: AdminRepository
) : UseCase<String, Result<String?>>() {
    override suspend fun invoke(params: String): Result<String?> {
        return repository.getChatForUser(params)
    }
}
