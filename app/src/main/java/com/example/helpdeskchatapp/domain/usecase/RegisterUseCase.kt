package com.example.helpdeskchatapp.domain.usecase

import com.example.helpdeskchatapp.data.interfaces.UserRepository
import com.example.helpdeskchatapp.domain.model.LoginParams
import javax.inject.Inject

class RegisterUseCase @Inject constructor(
    private val repository: UserRepository
) : UseCase<LoginParams, Result<String>>() {

    override suspend fun invoke(params: LoginParams): Result<String> {
        return repository.register(params)
    }
}
