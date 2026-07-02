package haag.your.next.developer.domain.usecase

import haag.your.next.developer.data.repository.PendingAdminIdRepository
import javax.inject.Inject

class GetPendingAdminIdUseCase @Inject constructor(
    private val repository: PendingAdminIdRepository
) : ProducerUseCase<String?>() {
    override suspend operator fun invoke(): String? {
        return repository.get()
    }
}
