package haag.your.next.developer.domain.usecase

import haag.your.next.developer.domain.model.consumer.CreateChat
import haag.your.next.developer.domain.model.consumer.UserName
import javax.inject.Inject

class SubmitUserNameUseCase @Inject constructor(
    private val getNameUpdateContextUseCase: GetNameUpdateContextUseCase,
    private val updateUserNameUseCase: UpdateUserNameUseCase,
    private val startChatUseCase: StartChatUseCase,
    private val getChatForUserUseCase: GetChatForUserUseCase
) : UseCase<UserName, Result<String?>>() {

    override suspend operator fun invoke(params: UserName): Result<String?> {
        val (userId, adminId) = getNameUpdateContextUseCase()
        userId ?: return Result.failure(IllegalStateException("User not authenticated"))

        updateUserNameUseCase(params).getOrElse { return Result.failure(it) }

        return if (adminId != null) {
            startChatUseCase(CreateChat(adminId = adminId, userId = userId, senderName = params.name))
        } else {
            getChatForUserUseCase(userId)
        }
    }
}
