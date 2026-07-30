package haag.your.next.developer.domain.usecase

import javax.inject.Inject

data class ChatSessionData(
    val userId: String?,
    val isAnonymous: Boolean
)

class InitChatSessionUseCase @Inject constructor(
    private val getCurrentUserUseCase: GetCurrentUserUseCase,
    private val isAnonymousUseCase: IsAnonymousUseCase
) : ProducerUseCase<ChatSessionData>() {
    override suspend operator fun invoke() = ChatSessionData(
        userId = getCurrentUserUseCase(),
        isAnonymous = isAnonymousUseCase()
    )
}
