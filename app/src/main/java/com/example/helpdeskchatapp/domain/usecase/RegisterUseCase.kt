package haag.your.next.developer.domain.usecase

import haag.your.next.developer.data.interfaces.UserRepository
import haag.your.next.developer.domain.model.consumer.Login
import javax.inject.Inject

class RegisterUseCase @Inject constructor(
    private val repository: UserRepository
) : UseCase<Login, Result<Unit>>() {

    override suspend fun invoke(params: Login): Result<Unit> {
        return repository.register(params)
    }
}
