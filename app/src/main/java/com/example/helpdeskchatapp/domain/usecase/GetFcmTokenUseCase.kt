package com.example.helpdeskchatapp.domain.usecase

import com.example.helpdeskchatapp.data.interfaces.UserRepository
import javax.inject.Inject

class GetFcmTokenUseCase @Inject constructor(
    private val repository: UserRepository
) : ProducerUseCase<Result<String>>() {
    override suspend fun invoke(): Result<String> = repository.getFcmToken()
}
