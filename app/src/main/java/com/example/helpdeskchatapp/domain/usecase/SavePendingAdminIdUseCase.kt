package haag.your.next.developer.domain.usecase

import haag.your.next.developer.data.repository.PendingAdminIdRepository
import javax.inject.Inject

class SavePendingAdminIdUseCase @Inject constructor(
    private val repository: PendingAdminIdRepository
) : ConsumerUseCase<String>() {
    override suspend operator fun invoke(params: String) {
        repository.save(params)
    }
}
