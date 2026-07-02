package haag.your.next.developer.domain.usecase

import haag.your.next.developer.data.interfaces.UserRepository
import javax.inject.Inject

class GetCurrentUserUseCase @Inject constructor(
    private val repository: UserRepository
) : ProducerUseCase<String?>() {
    override suspend operator fun invoke(): String? {
        return repository.getCurrentUser()
    }
}
