package haag.your.next.developer.domain.usecase

import javax.inject.Inject

class SyncFcmTokenUseCase @Inject constructor(
    private val getFcmTokenUseCase: GetFcmTokenUseCase,
    private val updateFcmTokenUseCase: UpdateFcmTokenUseCase
) : ActionUseCase() {
    override suspend operator fun invoke() {
        getFcmTokenUseCase()
            .onSuccess { token -> updateFcmTokenUseCase(token) }
    }
}
