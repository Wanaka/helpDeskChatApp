package haag.your.next.developer.domain.usecase

import javax.inject.Inject

class PostAuthSetupUseCase @Inject constructor(
    private val syncFcmTokenUseCase: SyncFcmTokenUseCase
) : ActionUseCase() {
    override suspend operator fun invoke() {
        syncFcmTokenUseCase()
    }
}
