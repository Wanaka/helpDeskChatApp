package haag.your.next.developer.domain.usecase

import haag.your.next.developer.domain.model.consumer.CreateChat
import haag.your.next.developer.domain.model.consumer.UserName
import javax.inject.Inject

class SubmitUserNameUseCase @Inject constructor(
    private val getNameUpdateContextUseCase: GetNameUpdateContextUseCase,
    private val updateUserNameUseCase: UpdateUserNameUseCase,
    private val createChatUseCase: CreateChatUseCase,
    private val getChatForUserUseCase: GetChatForUserUseCase,
    private val clearPendingAdminIdUseCase: ClearPendingAdminIdUseCase
) : UseCase<UserName, Result<String?>>() {

    override suspend operator fun invoke(params: UserName): Result<String?> {
        val (userId, adminId) = getNameUpdateContextUseCase()
        userId ?: return Result.failure(IllegalStateException("User not authenticated"))

        updateUserNameUseCase(params).getOrElse { return Result.failure(it) }

        return if (adminId != null) {
            createChatUseCase(CreateChat(adminId = adminId, userId = userId, senderName = params.name))
                .onSuccess { clearPendingAdminIdUseCase() }
        } else {
            getChatForUserUseCase(userId)
        }
    }
}
