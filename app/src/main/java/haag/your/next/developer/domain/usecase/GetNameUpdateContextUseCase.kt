package haag.your.next.developer.domain.usecase

import javax.inject.Inject

data class NameUpdateContext(
    val userId: String?,
    val pendingAdminId: String?
)

class GetNameUpdateContextUseCase @Inject constructor(
    private val getCurrentUserUseCase: GetCurrentUserUseCase,
    private val getPendingAdminIdUseCase: GetPendingAdminIdUseCase
) : ProducerUseCase<NameUpdateContext>() {
    override suspend operator fun invoke() = NameUpdateContext(
        userId = getCurrentUserUseCase(),
        pendingAdminId = getPendingAdminIdUseCase()
    )
}
