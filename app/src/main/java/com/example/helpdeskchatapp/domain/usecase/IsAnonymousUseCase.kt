package com.example.helpdeskchatapp.domain.usecase

import com.example.helpdeskchatapp.data.interfaces.UserRepository
import javax.inject.Inject

class IsAnonymousUseCase @Inject constructor(
    private val repository: UserRepository
) {
    operator fun invoke(): Boolean {
        return repository.isAnonymous()
    }
}
