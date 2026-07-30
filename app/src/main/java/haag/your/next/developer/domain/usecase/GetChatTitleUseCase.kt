package haag.your.next.developer.domain.usecase

import haag.your.next.developer.domain.model.producer.UserNameViewEntity
import javax.inject.Inject

class GetChatTitleUseCase @Inject constructor(
    private val isAnonymousUseCase: IsAnonymousUseCase,
    private val getUserNameUseCase: GetUserNameUseCase,
    private val getAdminNameUseCase: GetAdminNameUseCase
) : UseCase<String, Result<UserNameViewEntity>>() {
    override suspend operator fun invoke(params: String): Result<UserNameViewEntity> {
        return if (isAnonymousUseCase()) {
            getAdminNameUseCase(params).map { UserNameViewEntity(name = it, company = "") }
        } else {
            getUserNameUseCase(params)
        }
    }
}
