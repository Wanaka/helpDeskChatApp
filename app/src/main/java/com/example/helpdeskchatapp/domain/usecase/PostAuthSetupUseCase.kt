package haag.your.next.developer.domain.usecase

import javax.inject.Inject

class PostAuthSetupUseCase @Inject constructor(
    private val getFcmTokenUseCase: GetFcmTokenUseCase,
    private val updateFcmTokenUseCase: UpdateFcmTokenUseCase
) : ProducerUseCase<Result<Unit>>() {
    override suspend fun invoke(): Result<Unit> {
        val token = getFcmTokenUseCase().getOrElse { return Result.failure(it) }
        return updateFcmTokenUseCase(token)
    }
}
