package com.example.helpdeskchatapp.domain.usecase

import com.example.helpdeskchatapp.data.repository.PendingAdminIdRepository
import javax.inject.Inject

class GetPendingAdminIdUseCase @Inject constructor(
    private val repository: PendingAdminIdRepository
) : ProducerUseCase<String?>() {
    override suspend operator fun invoke(): String? {
        return repository.get()
    }
}
