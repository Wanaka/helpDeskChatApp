package com.example.helpdeskchatapp.domain.usecase

import com.example.helpdeskchatapp.data.interfaces.ReadTimestampRepository
import javax.inject.Inject

class GetLocalReadTimestampUseCase @Inject constructor(
    private val repository: ReadTimestampRepository
) : UseCase<String, Long?>() {
    override suspend fun invoke(params: String): Long? = repository.getLastRead(params)
}
