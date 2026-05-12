package com.example.helpdeskchatapp.domain.usecase

import com.example.helpdeskchatapp.data.interfaces.UserRepository
import javax.inject.Inject

class UpdateUserNameUseCase @Inject constructor(
    private val repository: UserRepository
) : UseCase<Pair<String, String>, Result<Unit>>() {
    override suspend fun invoke(params: Pair<String, String>): Result<Unit> {
        return repository.updateUserName(params)
    }
}
