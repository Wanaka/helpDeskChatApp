package com.example.helpdeskchatapp.domain.usecase

import com.example.helpdeskchatapp.data.interfaces.UserRepository
import com.example.helpdeskchatapp.domain.model.consumer.Login
import javax.inject.Inject

class RegisterUseCase @Inject constructor(
    private val repository: UserRepository
) : UseCase<Login, Result<Unit>>() {

    override suspend fun invoke(params: Login): Result<Unit> {
        return repository.register(params)
    }
}
