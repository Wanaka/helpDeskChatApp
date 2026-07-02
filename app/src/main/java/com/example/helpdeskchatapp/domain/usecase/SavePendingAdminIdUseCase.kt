package com.example.helpdeskchatapp.domain.usecase

import com.example.helpdeskchatapp.data.repository.PendingAdminIdRepository
import javax.inject.Inject

class SavePendingAdminIdUseCase @Inject constructor(
    private val repository: PendingAdminIdRepository
) : ConsumerUseCase<String>() {
    override suspend operator fun invoke(params: String) {
        repository.save(params)
    }
}
