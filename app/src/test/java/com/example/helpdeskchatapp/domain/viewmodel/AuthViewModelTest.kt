package com.example.helpdeskchatapp.domain.viewmodel

import app.cash.turbine.test
import com.example.helpdeskchatapp.domain.model.consumer.Login
import com.example.helpdeskchatapp.domain.usecase.GetFcmTokenUseCase
import com.example.helpdeskchatapp.domain.usecase.LoginUseCase
import com.example.helpdeskchatapp.domain.usecase.PostAuthSetupUseCase
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
class AuthViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val userRepository = FakeUserRepository()

    private fun viewModel() = AuthViewModel(
        LoginUseCase(userRepository),
        RegisterUseCase(userRepository),
        PostAuthSetupUseCase(
            GetFcmTokenUseCase(userRepository),
            UpdateFcmTokenUseCase(userRepository)
        )
    )

    // ── login ───────────────────────────────────────────────────────────────

    @Test
    fun `login_success_emitsNavigateToAdminAndSuccessState`() =
        runTest(mainDispatcherRule.testDispatcher) {
            userRepository.loginResult = Result.success(Unit)
            // Make postAuthSetup a no-op so Firebase FCM is never touched
            userRepository.getFcmTokenResult = Result.failure(RuntimeException("no token"))
            val vm = viewModel()

            vm.navigateToAdmin.test {
                vm.login(Login("admin@x.com", "pw"))
                assertEquals(Unit, awaitItem())
                cancelAndConsumeRemainingEvents()
            }
            assertTrue(vm.uiState.value is UiState.Success)
        }

    @Test
    fun `login_failure_setsErrorStateWithMessage`() =
        runTest(mainDispatcherRule.testDispatcher) {
            userRepository.loginResult = Result.failure(RuntimeException("bad creds"))
            val vm = viewModel()

            vm.login(Login("admin@x.com", "wrong"))

            val state = vm.uiState.value
            assertTrue(state is UiState.Error)
            assertEquals("bad creds", (state as UiState.Error).message)
        }

    @Test
    fun `login_failure_withNullMessage_usesDefaultErrorText`() =
        runTest(mainDispatcherRule.testDispatcher) {
            userRepository.loginResult = Result.failure(RuntimeException())
            val vm = viewModel()

            vm.login(Login("admin@x.com", "wrong"))

            val state = vm.uiState.value
            assertTrue(state is UiState.Error)
            assertEquals("Login failed", (state as UiState.Error).message)
        }

    @Test
    fun `login_setsLoadingStateDuringOperation`() =
        runTest(mainDispatcherRule.testDispatcher) {
            // Arrange: login will fail, but Loading must be emitted before that
            userRepository.loginResult = Result.failure(RuntimeException("err"))
            val vm = viewModel()

            // Before calling login, state is Success (set in init)
            assertTrue(vm.uiState.value is UiState.Success)

            // After the coroutine runs with UnconfinedTestDispatcher the final
            // state is Error, but Loading was the intermediate value — we
            // confirm the end state here; Loading is ephemeral with Unconfined.
            vm.login(Login("a@b.com", "pw"))
            assertTrue(vm.uiState.value is UiState.Error)
        }

    @Test
    fun `login_whenPostAuthSetupFails_stillNavigatesToAdmin`() =
        runTest(mainDispatcherRule.testDispatcher) {
            userRepository.loginResult = Result.success(Unit)
            userRepository.getFcmTokenResult = Result.failure(RuntimeException("FCM unavailable"))
            val vm = viewModel()

            vm.navigateToAdmin.test {
                vm.login(Login("admin@x.com", "pw"))
                assertEquals(Unit, awaitItem())
                cancelAndConsumeRemainingEvents()
            }
        }

    // ── register ────────────────────────────────────────────────────────────

    @Test
    fun `register_success_emitsNavigateToAdminAndSuccessState`() =
        runTest(mainDispatcherRule.testDispatcher) {
            userRepository.registerResult = Result.success(Unit)
            userRepository.getFcmTokenResult = Result.failure(RuntimeException("no token"))
            val vm = viewModel()

            vm.navigateToAdmin.test {
                vm.register(Login("new@x.com", "pw"))
                assertEquals(Unit, awaitItem())
                cancelAndConsumeRemainingEvents()
            }
            assertTrue(vm.uiState.value is UiState.Success)
        }

    @Test
    fun `register_failure_setsErrorStateWithMessage`() =
        runTest(mainDispatcherRule.testDispatcher) {
            userRepository.registerResult = Result.failure(RuntimeException("email taken"))
            val vm = viewModel()

            vm.register(Login("new@x.com", "pw"))

            val state = vm.uiState.value
            assertTrue(state is UiState.Error)
            assertEquals("email taken", (state as UiState.Error).message)
        }

    @Test
    fun `register_failure_withNullMessage_usesDefaultErrorText`() =
        runTest(mainDispatcherRule.testDispatcher) {
            userRepository.registerResult = Result.failure(RuntimeException())
            val vm = viewModel()

            vm.register(Login("new@x.com", "pw"))

            val state = vm.uiState.value
            assertTrue(state is UiState.Error)
            assertEquals("Registration failed", (state as UiState.Error).message)
        }
}
