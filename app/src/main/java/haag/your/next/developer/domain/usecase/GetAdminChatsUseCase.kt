package haag.your.next.developer.domain.usecase

import haag.your.next.developer.domain.model.producer.ChatViewEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

data class AdminChatsData(
    val adminId: String,
    val chats: Flow<List<ChatViewEntity>>
)

class GetAdminChatsUseCase @Inject constructor(
    private val getCurrentUserUseCase: GetCurrentUserUseCase,
    private val getChatsUseCase: GetChatsUseCase
) : ProducerUseCase<Result<AdminChatsData>>() {

    override suspend operator fun invoke(): Result<AdminChatsData> {
        val adminId = getCurrentUserUseCase()
            ?: return Result.failure(IllegalStateException("User not authenticated"))
        return Result.success(AdminChatsData(adminId, getChatsUseCase(adminId)))
    }
}
