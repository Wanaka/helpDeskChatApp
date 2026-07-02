package haag.your.next.developer.domain.usecase

import haag.your.next.developer.data.interfaces.UserRepository
import javax.inject.Inject

class LogoutUseCase @Inject constructor(
    private val repository: UserRepository
) : ProducerUseCase<Result<Unit>>() {
    override suspend operator fun invoke(): Result<Unit> {
        return repository.logout()
    }
}
