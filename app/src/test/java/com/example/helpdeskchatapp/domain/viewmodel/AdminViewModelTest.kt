package haag.your.next.developer.domain.viewmodel

import app.cash.turbine.test
import haag.your.next.developer.domain.model.consumer.UserName
import haag.your.next.developer.domain.model.producer.UserNameViewEntity
import haag.your.next.developer.domain.usecase.GetChatsUseCase
import haag.your.next.developer.domain.usecase.GetCurrentUserUseCase
import haag.your.next.developer.domain.usecase.GetLocalReadTimestampUseCase
import haag.your.next.developer.domain.usecase.GetUserNameUseCase
import haag.your.next.developer.domain.usecase.LogoutUseCase
import haag.your.next.developer.domain.usecase.UpdateUserNameUseCase
import haag.your.next.developer.fakes.FakeAdminRepository
import haag.your.next.developer.fakes.FakeReadTimestampRepository
import haag.your.next.developer.fakes.FakeUserRepository
import haag.your.next.developer.util.MainDispatcherRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class AdminViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val adminRepository = FakeAdminRepository()
    private val userRepository = FakeUserRepository()
    private val timestampRepository = FakeReadTimestampRepository()

    // init {} runs checkAdminName() + loadData(); configure fakes before constructing.
    private fun viewModel(): AdminViewModel {
        // AdminViewModel.init calls getCurrentUserUseCase() — provide a user id
        userRepository.currentUserId = "admin-uid"
        return AdminViewModel(
            GetChatsUseCase(adminRepository),
            LogoutUseCase(userRepository),
            GetUserNameUseCase(adminRepository),
            UpdateUserNameUseCase(userRepository),
            GetCurrentUserUseCase(userRepository),
            GetLocalReadTimestampUseCase(timestampRepository)
        )
    }

    @Test
    fun `init_whenStoredNameIsBlank_showsNameOverlay`() =
        runTest(mainDispatcherRule.testDispatcher) {
            adminRepository.getUserNameResult =
                Result.success(UserNameViewEntity(name = "", company = ""))

            val vm = viewModel()

            assertTrue(vm.showNameOverlay.value)
        }

    @Test
    fun `init_whenStoredNameIsDefaultAdmin_showsNameOverlay`() =
        runTest(mainDispatcherRule.testDispatcher) {
            adminRepository.getUserNameResult =
                Result.success(UserNameViewEntity(name = "Admin", company = ""))

            val vm = viewModel()

            assertTrue(vm.showNameOverlay.value)
        }

    @Test
    fun `init_whenStoredNameIsSet_doesNotShowOverlay`() =
        runTest(mainDispatcherRule.testDispatcher) {
            adminRepository.getUserNameResult =
                Result.success(UserNameViewEntity(name = "Bob", company = "Acme"))

            val vm = viewModel()

            assertFalse(vm.showNameOverlay.value)
        }

    @Test
    fun `updateName_success_hidesOverlay`() =
        runTest(mainDispatcherRule.testDispatcher) {
            adminRepository.getUserNameResult =
                Result.success(UserNameViewEntity(name = "", company = ""))
            userRepository.updateUserNameResult = Result.success(Unit)
            val vm = viewModel()

            vm.updateName(UserName(name = "Bob", company = "Acme"))

            assertFalse(vm.showNameOverlay.value)
        }

    @Test
    fun `updateName_failure_emitsToast`() =
        runTest(mainDispatcherRule.testDispatcher) {
            adminRepository.getUserNameResult =
                Result.success(UserNameViewEntity(name = "Bob", company = "Acme"))
            userRepository.updateUserNameResult = Result.failure(RuntimeException("nope"))
            val vm = viewModel()

            vm.toastEvent.test {
                vm.updateName(UserName(name = "Bob", company = "Acme"))
                assertEquals("Failed to update name", awaitItem())
                cancelAndConsumeRemainingEvents()
            }
        }

    @Test
    fun `logout_invokesUseCaseAndEmitsLogoutEvent`() =
        runTest(mainDispatcherRule.testDispatcher) {
            adminRepository.getUserNameResult =
                Result.success(UserNameViewEntity(name = "Bob", company = "Acme"))
            val vm = viewModel()

            vm.logoutEvent.test {
                vm.logout()
                assertEquals(Unit, awaitItem())
                cancelAndConsumeRemainingEvents()
            }
            assertTrue(userRepository.logoutCalled)
        }
}
