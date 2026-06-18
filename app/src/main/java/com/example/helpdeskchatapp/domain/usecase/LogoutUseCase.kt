package com.example.helpdeskchatapp.domain.usecase

import com.example.helpdeskchatapp.data.interfaces.UserRepository
import javax.inject.Inject

class LogoutUseCase @Inject constructor(
    private val repository: UserRepository
) : ProducerUseCase<Result<Unit>>() {
    override suspend operator fun invoke(): Result<Unit> {
        return repository.logout()
    }
}
