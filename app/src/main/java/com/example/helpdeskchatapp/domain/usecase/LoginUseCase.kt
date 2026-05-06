package com.example.helpdeskchatapp.domain.usecase

import com.example.helpdeskchatapp.data.FakeUserRepository
import com.example.helpdeskchatapp.data.UserRepository
import com.example.helpdeskchatapp.domain.model.LoginParams

class LoginUseCase(
    private val repository: UserRepository = FakeUserRepository()
) : UseCase<LoginParams, Result<String>>() {

    override suspend fun invoke(params: LoginParams): Result<String> {
        return repository.login(params)
    }
}
