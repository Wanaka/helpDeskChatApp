package com.example.helpdeskchatapp.domain.usecase

import com.example.helpdeskchatapp.data.interfaces.UserRepository
import com.example.helpdeskchatapp.domain.model.consumer.UserName
import javax.inject.Inject

class UpdateUserNameUseCase @Inject constructor(
    private val repository: UserRepository
) : UseCase<UserName, Result<Unit>>() {
    override suspend fun invoke(params: UserName): Result<Unit> {
        return repository.updateUserName(params)
    }
}
