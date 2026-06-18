package com.example.helpdeskchatapp.domain.usecase

import com.example.helpdeskchatapp.data.interfaces.AdminRepository
import com.example.helpdeskchatapp.domain.model.producer.UserNameViewEntity
import javax.inject.Inject

class GetUserNameUseCase @Inject constructor(
    private val repository: AdminRepository
) : UseCase<String, Result<UserNameViewEntity>>() {
    override suspend fun invoke(params: String): Result<UserNameViewEntity> {
        return repository.getUserName(params)
    }
}
