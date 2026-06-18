package com.example.helpdeskchatapp.domain.usecase

import kotlinx.coroutines.flow.Flow

/**
 * Base UseCase with input parameters and a result.
 */
abstract class UseCase<in P, out R> {
    abstract suspend operator fun invoke(params: P): R
}

/**
 * Base UseCase for continuous streams. invoke is non-suspending because it returns a cold
 * Flow — the caller suspends only when collecting, not when subscribing.
 */
abstract class FlowUseCase<in P, R> {
    abstract operator fun invoke(params: P): Flow<R>
}

/**
 * Base UseCase that produces a value with no input parameters.
 */
abstract class ProducerUseCase<out R> {
    abstract suspend operator fun invoke(): R
}

/**
 * Base UseCase that consumes input parameters with no result.
 */
abstract class ConsumerUseCase<in P> {
    abstract suspend operator fun invoke(params: P)
}

/**
 * Base UseCase with no input parameters and no result — a pure side-effect operation.
 * Use this for fire-and-forget actions such as logout, cache invalidation, or analytics flushes
 * where there is nothing to pass in and nothing meaningful to return.
 */
abstract class ActionUseCase {
    abstract suspend operator fun invoke()
}
