package haag.your.next.developer.domain.usecase

import haag.your.next.developer.domain.model.consumer.CreateChat
import javax.inject.Inject

class StartChatUseCase @Inject constructor(
    private val createChatUseCase: CreateChatUseCase,
    private val clearPendingAdminIdUseCase: ClearPendingAdminIdUseCase
) : UseCase<CreateChat, Result<String>>() {

    override suspend operator fun invoke(params: CreateChat): Result<String> {
        return createChatUseCase(params)
            .onSuccess { clearPendingAdminIdUseCase() }
    }
}
