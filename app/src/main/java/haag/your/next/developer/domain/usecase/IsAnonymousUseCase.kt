package haag.your.next.developer.domain.usecase

import haag.your.next.developer.data.interfaces.UserRepository
import javax.inject.Inject

class IsAnonymousUseCase @Inject constructor(
    private val repository: UserRepository
) : ProducerUseCase<Boolean>() {
    override suspend operator fun invoke(): Boolean {
        return repository.isAnonymous()
    }
}
