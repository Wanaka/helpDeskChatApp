package haag.your.next.developer.domain.usecase

import haag.your.next.developer.data.interfaces.UserRepository
import haag.your.next.developer.domain.model.consumer.UserName
import javax.inject.Inject

class UpdateUserNameUseCase @Inject constructor(
    private val repository: UserRepository
) : UseCase<UserName, Result<Unit>>() {
    override suspend fun invoke(params: UserName): Result<Unit> {
        return repository.updateUserName(params)
    }
}
