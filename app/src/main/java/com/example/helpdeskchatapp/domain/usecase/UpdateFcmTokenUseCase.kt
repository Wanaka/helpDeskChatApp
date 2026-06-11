package com.example.helpdeskchatapp.domain.usecase

import com.example.helpdeskchatapp.data.interfaces.UserRepository
import javax.inject.Inject

class UpdateFcmTokenUseCase @Inject constructor(
    private val repository: UserRepository
) : ConsumerUseCase<String>() {
    override suspend fun invoke(params: String) {
        repository.updateFcmToken(params)
    }
}
