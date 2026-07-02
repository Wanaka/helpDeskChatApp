package haag.your.next.developer.domain.usecase

import haag.your.next.developer.data.interfaces.ReadTimestampRepository
import javax.inject.Inject

class SaveLocalReadTimestampUseCase @Inject constructor(
    private val repository: ReadTimestampRepository
) : ConsumerUseCase<String>() {
    override suspend fun invoke(params: String) = repository.saveLastRead(params)
}
