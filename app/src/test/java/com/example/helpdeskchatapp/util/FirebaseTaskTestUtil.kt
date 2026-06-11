package com.example.helpdeskchatapp.util

import com.google.android.gms.tasks.Task
import io.mockk.every
import io.mockk.mockk

/**
 * A completed [Task] that resolves to [value] when awaited via
 * `kotlinx.coroutines.tasks.await()` — lets repository tests stub Firebase calls
 * synchronously without a real Task or emulator.
 */
fun <T> succeededTask(value: T): Task<T> = mockk {
    every { isComplete } returns true
    every { isCanceled } returns false
    every { exception } returns null
    every { result } returns value
}

/** A completed [Task] that throws [error] when awaited. */
fun <T> failedTask(error: Exception): Task<T> = mockk {
    every { isComplete } returns true
    every { isCanceled } returns false
    every { exception } returns error
}
