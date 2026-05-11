package com.example.helpdeskchatapp.domain.usecase

import com.example.helpdeskchatapp.data.interfaces.UserRepository
import javax.inject.Inject

class LoginAnonymouslyUseCase @Inject constructor(
    private val repository: UserRepository
) : NoParamUseCase<Result<String>>() {

    override suspend fun invoke(): Result<String> {
        return repository.loginAnonymously()
    }
}
