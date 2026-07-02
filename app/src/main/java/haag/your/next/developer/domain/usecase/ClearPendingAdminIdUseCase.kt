package haag.your.next.developer.domain.usecase

import haag.your.next.developer.data.repository.PendingAdminIdRepository
import javax.inject.Inject

class ClearPendingAdminIdUseCase @Inject constructor(
    private val repository: PendingAdminIdRepository
) : ActionUseCase() {
    override suspend operator fun invoke() {
        repository.clear()
    }
}
