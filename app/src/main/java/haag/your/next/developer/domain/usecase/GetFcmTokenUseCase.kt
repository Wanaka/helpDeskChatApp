package haag.your.next.developer.domain.usecase

import haag.your.next.developer.data.interfaces.UserRepository
import javax.inject.Inject

class GetFcmTokenUseCase @Inject constructor(
    private val repository: UserRepository
) : ProducerUseCase<Result<String>>() {
    override suspend fun invoke(): Result<String> = repository.getFcmToken()
}
