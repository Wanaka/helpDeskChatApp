package com.example.helpdeskchatapp.domain.viewmodel

import app.cash.turbine.test
import com.example.helpdeskchatapp.domain.model.consumer.Login
import com.example.helpdeskchatapp.domain.usecase.GetCurrentUserUseCase
import com.example.helpdeskchatapp.domain.usecase.RegisterUseCase
import com.example.helpdeskchatapp.domain.usecase.UpdateFcmTokenUseCase
import com.example.helpdeskchatapp.fakes.FakeUserRepository
import com.example.helpdeskchatapp.ui.common.UiState
import com.example.helpdeskchatapp.util.MainDispatcherRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class RegisterViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val userRepository = FakeUserRepository()

    private fun viewModel() = RegisterViewModel(
        RegisterUseCase(userRepository),
        GetCurrentUserUseCase(userRepository),
        UpdateFcmTokenUseCase(userRepository)
    )

    @Test
    fun register_success_emitsNavigateToAdminAndSuccessState() =
        runTest(mainDispatcherRule.testDispatcher) {
            userRepository.registerResult = Result.success(Unit)
            userRepository.currentUserId = null
            val vm = viewModel()

            vm.navigateToAdmin.test {
                vm.register(Login("admin@x.com", "pw"))
                assertEquals(Unit, awaitItem())
                cancelAndConsumeRemainingEvents()
            }
            assertTrue(vm.uiState.value is UiState.Success)
        }

    @Test
    fun register_failure_setsErrorStateWithMessage() =
        runTest(mainDispatcherRule.testDispatcher) {
            userRepository.registerResult = Result.failure(RuntimeException("email taken"))
            val vm = viewModel()

            vm.register(Login("admin@x.com", "pw"))

            val state = vm.uiState.value
            assertTrue(state is UiState.Error)
            assertEquals("email taken", (state as UiState.Error).message)
        }
}
