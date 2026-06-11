package com.example.helpdeskchatapp.domain.viewmodel

import app.cash.turbine.test
import com.example.helpdeskchatapp.domain.model.consumer.Login
import com.example.helpdeskchatapp.domain.usecase.GetCurrentUserUseCase
import com.example.helpdeskchatapp.domain.usecase.LoginUseCase
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
class LoginViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val userRepository = FakeUserRepository()

    private fun viewModel() = LoginViewModel(
        LoginUseCase(userRepository),
        GetCurrentUserUseCase(userRepository),
        UpdateFcmTokenUseCase(userRepository)
    )

    @Test
    fun login_success_emitsNavigateToAdminAndSuccessState() =
        runTest(mainDispatcherRule.testDispatcher) {
            userRepository.loginResult = Result.success(Unit)
            userRepository.currentUserId = null // skips the FCM/Firebase static branch
            val vm = viewModel()

            vm.navigateToAdmin.test {
                vm.login(Login("admin@x.com", "pw"))
                assertEquals(Unit, awaitItem())
                cancelAndConsumeRemainingEvents()
            }
            assertTrue(vm.uiState.value is UiState.Success)
        }

    @Test
    fun login_failure_setsErrorStateWithMessage() =
        runTest(mainDispatcherRule.testDispatcher) {
            userRepository.loginResult = Result.failure(RuntimeException("bad creds"))
            val vm = viewModel()

            vm.login(Login("admin@x.com", "wrong"))

            val state = vm.uiState.value
            assertTrue(state is UiState.Error)
            assertEquals("bad creds", (state as UiState.Error).message)
        }
}
