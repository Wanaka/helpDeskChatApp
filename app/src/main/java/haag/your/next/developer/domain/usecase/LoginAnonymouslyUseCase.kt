package haag.your.next.developer.domain.usecase

import haag.your.next.developer.data.interfaces.UserRepository
import javax.inject.Inject

class LoginAnonymouslyUseCase @Inject constructor(
    private val repository: UserRepository
) : ProducerUseCase<Result<String>>() {

    override suspend fun invoke(): Result<String> {
        return repository.loginAnonymously()
    }
}
