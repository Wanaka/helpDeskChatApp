package haag.your.next.developer.domain.usecase

import javax.inject.Inject

data class DeepLinkSession(val userId: String, val userName: String)

class PrepareDeepLinkSessionUseCase @Inject constructor(
    private val getCurrentUserUseCase: GetCurrentUserUseCase,
    private val loginAnonymouslyUseCase: LoginAnonymouslyUseCase,
    private val syncFcmTokenUseCase: SyncFcmTokenUseCase,
    private val getUserNameUseCase: GetUserNameUseCase
) : ProducerUseCase<Result<DeepLinkSession>>() {

    override suspend operator fun invoke(): Result<DeepLinkSession> {
        val userId = getCurrentUserUseCase()
            ?: loginAnonymouslyUseCase()
                .getOrElse { return Result.failure(it) }

        syncFcmTokenUseCase()

        return getUserNameUseCase(userId)
            .map { DeepLinkSession(userId, it.name) }
    }
}
