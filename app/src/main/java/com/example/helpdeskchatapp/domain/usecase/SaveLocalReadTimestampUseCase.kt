package com.example.helpdeskchatapp.domain.usecase

import com.example.helpdeskchatapp.data.interfaces.ReadTimestampRepository
import javax.inject.Inject

class SaveLocalReadTimestampUseCase @Inject constructor(
    private val repository: ReadTimestampRepository
) : ConsumerUseCase<String>() {
    override suspend fun invoke(params: String) = repository.saveLastRead(params)
}
