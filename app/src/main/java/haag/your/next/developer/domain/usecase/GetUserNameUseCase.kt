package haag.your.next.developer.domain.usecase

import haag.your.next.developer.data.interfaces.AdminRepository
import haag.your.next.developer.domain.model.producer.UserNameViewEntity
import javax.inject.Inject

class GetUserNameUseCase @Inject constructor(
    private val repository: AdminRepository
) : UseCase<String, Result<UserNameViewEntity>>() {
    override suspend fun invoke(params: String): Result<UserNameViewEntity> {
        return repository.getUserName(params)
    }
}
