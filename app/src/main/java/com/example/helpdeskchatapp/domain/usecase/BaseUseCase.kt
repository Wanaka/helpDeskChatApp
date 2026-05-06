package com.example.helpdeskchatapp.domain.usecase

/**
 * Base UseCase with input parameters and a result.
 */
abstract class UseCase<in P, out R> {
    abstract suspend operator fun invoke(params: P): R
}

/**
 * Base UseCase with no input parameters and a result.
 */
abstract class NoParamUseCase<out R> {
    abstract suspend operator fun invoke(): R
}
