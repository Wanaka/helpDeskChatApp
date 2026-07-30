package haag.your.next.developer.domain.usecase

import haag.your.next.developer.domain.model.consumer.CreateChat
import javax.inject.Inject

sealed class ExistingChatResult {
    object ShowNameOverlay : ExistingChatResult()
    object NotApplicable : ExistingChatResult()
    data class NavigateToChat(val chatId: String) : ExistingChatResult()
    object NoChatFound : ExistingChatResult()
}

class FindExistingChatUseCase @Inject constructor(
    private val getCurrentUserUseCase: GetCurrentUserUseCase,
    private val isAnonymousUseCase: IsAnonymousUseCase,
    private val getUserNameUseCase: GetUserNameUseCase,
    private val getChatForUserUseCase: GetChatForUserUseCase,
    private val getPendingAdminIdUseCase: GetPendingAdminIdUseCase,
    private val startChatUseCase: StartChatUseCase
) : ProducerUseCase<Result<ExistingChatResult>>() {

    override suspend operator fun invoke(): Result<ExistingChatResult> {
        val userId = getCurrentUserUseCase()
        if (userId == null || !isAnonymousUseCase()) return Result.success(ExistingChatResult.NotApplicable)

        val userNameEntity = getUserNameUseCase(userId).getOrElse { return Result.failure(it) }
        val userName = userNameEntity.name

        if (userName.isEmpty()) return Result.success(ExistingChatResult.ShowNameOverlay)

        return getChatForUserUseCase(userId).mapCatching { chatId ->
            if (chatId != null) {
                ExistingChatResult.NavigateToChat(chatId)
            } else {
                val adminId = getPendingAdminIdUseCase()
                if (adminId != null) {
                    startChatUseCase(CreateChat(adminId = adminId, userId = userId, senderName = userName))
                        .map { ExistingChatResult.NavigateToChat(it) }
                        .getOrElse { throw it }
                } else {
                    ExistingChatResult.NoChatFound
                }
            }
        }
    }
}
