package haag.your.next.developer.domain.usecase

import javax.inject.Inject

class CheckAdminNameUseCase @Inject constructor(
    private val getCurrentUserUseCase: GetCurrentUserUseCase,
    private val getUserNameUseCase: GetUserNameUseCase
) : ProducerUseCase<Result<Boolean>>() {
    override suspend fun invoke(): Result<Boolean> {
        val adminId = getCurrentUserUseCase()
            ?: return Result.failure(IllegalStateException("User not authenticated"))
        return getUserNameUseCase(adminId).map { entity ->
            entity.name.isEmpty() || entity.name == "Admin"
        }
    }
}
