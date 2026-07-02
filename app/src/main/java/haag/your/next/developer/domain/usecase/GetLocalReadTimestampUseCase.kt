package haag.your.next.developer.domain.usecase

import haag.your.next.developer.data.interfaces.ReadTimestampRepository
import javax.inject.Inject

class GetLocalReadTimestampUseCase @Inject constructor(
    private val repository: ReadTimestampRepository
) : UseCase<String, Long?>() {
    override suspend fun invoke(params: String): Long? = repository.getLastRead(params)
}
