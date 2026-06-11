package com.example.helpdeskchatapp.domain.usecase

/**
 * Base UseCase with input parameters and a result.
 */
abstract class UseCase<in P, out R> {
    abstract suspend operator fun invoke(params: P): R
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
