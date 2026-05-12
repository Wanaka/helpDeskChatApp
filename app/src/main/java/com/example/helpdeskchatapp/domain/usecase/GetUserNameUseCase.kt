package com.example.helpdeskchatapp.domain.usecase

import com.example.helpdeskchatapp.data.interfaces.AdminRepository
import javax.inject.Inject

class GetUserNameUseCase @Inject constructor(
    private val repository: AdminRepository
) : UseCase<String, String>() {
    override suspend fun invoke(params: String): String {
        return repository.getUserName(params)
    }
}
