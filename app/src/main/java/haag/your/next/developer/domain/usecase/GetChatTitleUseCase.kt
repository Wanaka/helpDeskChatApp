package haag.your.next.developer.domain.usecase

import haag.your.next.developer.domain.model.producer.UserNameViewEntity
import javax.inject.Inject

class GetChatTitleUseCase @Inject constructor(
    private val isAnonymousUseCase: IsAnonymousUseCase,
    private val getUserNameUseCase: GetUserNameUseCase,
    private val getAdminNameUseCase: GetAdminNameUseCase
) {
    suspend operator fun invoke(conversationId: String): Result<UserNameViewEntity> {
        return if (isAnonymousUseCase()) {
            getAdminNameUseCase(conversationId)
                .map { UserNameViewEntity(name = it, company = "") }
        } else {
            getUserNameUseCase(conversationId)
        }
    }
}
