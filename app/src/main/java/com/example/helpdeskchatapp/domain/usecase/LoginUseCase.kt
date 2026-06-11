package com.example.helpdeskchatapp.domain.usecase

import com.example.helpdeskchatapp.data.interfaces.UserRepository
import com.example.helpdeskchatapp.domain.model.consumer.LoginParams
import javax.inject.Inject

class LoginUseCase @Inject constructor(
    private val repository: UserRepository
) : UseCase<LoginParams, Result<String>>() {

    override suspend fun invoke(params: LoginParams): Result<String> {
        return repository.login(params)
    }
}
