package haag.your.next.developer.domain.usecase

import haag.your.next.developer.data.interfaces.AdminRepository
import haag.your.next.developer.domain.model.producer.ChatViewEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetChatsUseCase @Inject constructor(
    private val repository: AdminRepository
) : FlowUseCase<String, List<ChatViewEntity>>() {
    override fun invoke(params: String): Flow<List<ChatViewEntity>> {
        return repository.getChats(params)
    }
}
