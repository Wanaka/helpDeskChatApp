package com.example.helpdeskchatapp.domain.usecase

import com.example.helpdeskchatapp.data.repository.PendingAdminIdRepository
import javax.inject.Inject

class ClearPendingAdminIdUseCase @Inject constructor(
    private val repository: PendingAdminIdRepository
) : ActionUseCase() {
    override suspend operator fun invoke() {
        repository.clear()
    }
}
