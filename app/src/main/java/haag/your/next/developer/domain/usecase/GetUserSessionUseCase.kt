package haag.your.next.developer.domain.usecase

import javax.inject.Inject

data class UserSessionState(
    val userId: String?,
    val isAnonymous: Boolean
)

class GetUserSessionUseCase @Inject constructor(
    private val getCurrentUserUseCase: GetCurrentUserUseCase,
    private val isAnonymousUseCase: IsAnonymousUseCase
) : ProducerUseCase<UserSessionState>() {
    override suspend operator fun invoke() = UserSessionState(
        userId = getCurrentUserUseCase(),
        isAnonymous = isAnonymousUseCase()
    )
}
