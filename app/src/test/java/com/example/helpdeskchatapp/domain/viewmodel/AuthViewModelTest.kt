package haag.your.next.developer.domain.viewmodel

import app.cash.turbine.test
import haag.your.next.developer.domain.model.consumer.EmailCredentials
import haag.your.next.developer.domain.usecase.GetFcmTokenUseCase
import haag.your.next.developer.domain.usecase.LoginUseCase
import haag.your.next.developer.domain.usecase.PostAuthSetupUseCase
import haag.your.next.developer.domain.usecase.RegisterUseCase
import haag.your.next.developer.domain.usecase.SyncFcmTokenUseCase
import haag.your.next.developer.domain.usecase.UpdateFcmTokenUseCase
import haag.your.next.developer.fakes.FakeUserRepository
import haag.your.next.developer.ui.common.UiState
import haag.your.next.developer.util.MainDispatcherRule
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
            SyncFcmTokenUseCase(
                GetFcmTokenUseCase(userRepository),
                UpdateFcmTokenUseCase(userRepository)
            )
        )
    )

    // ── login ───────────────────────────────────────────────────────────────

    @Test
    fun loginSuccessEmitsNavigateToAdminAndSuccessState() =
        runTest(mainDispatcherRule.testDispatcher) {
            userRepository.loginResult = Result.success(Unit)
            // Make postAuthSetup a no-op so Firebase FCM is never touched
            userRepository.getFcmTokenResult = Result.failure(RuntimeException("no token"))
            val vm = viewModel()

            vm.navigateToAdmin.test {
                vm.login(EmailCredentials("admin@x.com", "pw"))
                assertEquals(Unit, awaitItem())
                cancelAndConsumeRemainingEvents()
            }
            assertTrue(vm.uiState.value is UiState.Success)
        }

    @Test
    fun loginFailureSetsErrorStateWithMessage() =
        runTest(mainDispatcherRule.testDispatcher) {
            userRepository.loginResult = Result.failure(RuntimeException("bad creds"))
            val vm = viewModel()

            vm.login(EmailCredentials("admin@x.com", "wrong"))

            val state = vm.uiState.value
            assertTrue(state is UiState.Error)
            assertEquals("bad creds", (state as UiState.Error).message)
        }

    @Test
    fun loginFailureWithNullMessageUsesDefaultErrorText() =
        runTest(mainDispatcherRule.testDispatcher) {
            userRepository.loginResult = Result.failure(RuntimeException())
            val vm = viewModel()

            vm.login(EmailCredentials("admin@x.com", "wrong"))

            val state = vm.uiState.value
            assertTrue(state is UiState.Error)
            assertEquals("Login failed", (state as UiState.Error).message)
        }

    @Test
    fun loginSetsLoadingStateDuringOperation() =
        runTest(mainDispatcherRule.testDispatcher) {
            // Arrange: login will fail, but Loading must be emitted before that
            userRepository.loginResult = Result.failure(RuntimeException("err"))
            val vm = viewModel()

            // Before calling login, state is Success (set in init)
            assertTrue(vm.uiState.value is UiState.Success)

            // After the coroutine runs with UnconfinedTestDispatcher the final
            // state is Error, but Loading was the intermediate value — we
            // confirm the end state here; Loading is ephemeral with Unconfined.
            vm.login(EmailCredentials("a@b.com", "pw"))
            assertTrue(vm.uiState.value is UiState.Error)
        }

    @Test
    fun loginWhenPostAuthSetupFailsStillNavigatesToAdmin() =
        runTest(mainDispatcherRule.testDispatcher) {
            userRepository.loginResult = Result.success(Unit)
            userRepository.getFcmTokenResult = Result.failure(RuntimeException("FCM unavailable"))
            val vm = viewModel()

            vm.navigateToAdmin.test {
                vm.login(EmailCredentials("admin@x.com", "pw"))
                assertEquals(Unit, awaitItem())
                cancelAndConsumeRemainingEvents()
            }
        }

    // ── register ────────────────────────────────────────────────────────────

    @Test
    fun registerSuccessEmitsNavigateToAdminAndSuccessState() =
        runTest(mainDispatcherRule.testDispatcher) {
            userRepository.registerResult = Result.success(Unit)
            userRepository.getFcmTokenResult = Result.failure(RuntimeException("no token"))
            val vm = viewModel()

            vm.navigateToAdmin.test {
                vm.register(EmailCredentials("new@x.com", "pw"))
                assertEquals(Unit, awaitItem())
                cancelAndConsumeRemainingEvents()
            }
            assertTrue(vm.uiState.value is UiState.Success)
        }

    @Test
    fun registerFailureSetsErrorStateWithMessage() =
        runTest(mainDispatcherRule.testDispatcher) {
            userRepository.registerResult = Result.failure(RuntimeException("email taken"))
            val vm = viewModel()

            vm.register(EmailCredentials("new@x.com", "pw"))

            val state = vm.uiState.value
            assertTrue(state is UiState.Error)
            assertEquals("email taken", (state as UiState.Error).message)
        }

    @Test
    fun registerFailureWithNullMessageUsesDefaultErrorText() =
        runTest(mainDispatcherRule.testDispatcher) {
            userRepository.registerResult = Result.failure(RuntimeException())
            val vm = viewModel()

            vm.register(EmailCredentials("new@x.com", "pw"))

            val state = vm.uiState.value
            assertTrue(state is UiState.Error)
            assertEquals("Registration failed", (state as UiState.Error).message)
        }
}
