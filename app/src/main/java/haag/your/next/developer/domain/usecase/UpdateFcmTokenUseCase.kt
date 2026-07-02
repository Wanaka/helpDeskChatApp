package haag.your.next.developer.domain.usecase

import haag.your.next.developer.data.interfaces.UserRepository
import javax.inject.Inject

class UpdateFcmTokenUseCase @Inject constructor(
    private val repository: UserRepository
) : UseCase<String, Result<Unit>>() {
    override suspend fun invoke(params: String): Result<Unit> {
        return repository.updateFcmToken(params)
    }
}
