package com.example.helpdeskchatapp.domain.viewmodel

import app.cash.turbine.test
import com.example.helpdeskchatapp.domain.model.consumer.UserName
import com.example.helpdeskchatapp.domain.model.producer.UserNameViewEntity
import com.example.helpdeskchatapp.domain.usecase.GetChatsUseCase
import com.example.helpdeskchatapp.domain.usecase.GetCurrentUserUseCase
import com.example.helpdeskchatapp.domain.usecase.GetLocalReadTimestampUseCase
import com.example.helpdeskchatapp.domain.usecase.GetUserNameUseCase
import com.example.helpdeskchatapp.domain.usecase.LogoutUseCase
import com.example.helpdeskchatapp.domain.usecase.UpdateUserNameUseCase
import com.example.helpdeskchatapp.fakes.FakeAdminRepository
import com.example.helpdeskchatapp.fakes.FakeReadTimestampRepository
import com.example.helpdeskchatapp.fakes.FakeUserRepository
import com.example.helpdeskchatapp.util.MainDispatcherRule
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
