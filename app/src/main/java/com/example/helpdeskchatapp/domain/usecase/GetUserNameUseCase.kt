package com.example.helpdeskchatapp.domain.usecase

import com.example.helpdeskchatapp.data.interfaces.AdminRepository
import com.example.helpdeskchatapp.domain.model.consumer.UserName
import javax.inject.Inject

class GetUserNameUseCase @Inject constructor(
    private val repository: AdminRepository
) : UseCase<String, Result<UserName>>() {
    override suspend fun invoke(params: String): Result<UserName> {
        return repository.getUserName(params)
    }
}
