package com.example.helpdeskchatapp.domain.usecase

import com.example.helpdeskchatapp.data.interfaces.UserRepository
import javax.inject.Inject

class UpdateUserNameUseCase @Inject constructor(
    private val repository: UserRepository
) : UseCase<String, Result<Unit>>() {
    override suspend fun invoke(params: String): Result<Unit> {
        return repository.updateUserName(params)
    }
}
