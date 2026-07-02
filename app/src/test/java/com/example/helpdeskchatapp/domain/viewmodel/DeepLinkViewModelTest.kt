package haag.your.next.developer.domain.viewmodel

import app.cash.turbine.test
import haag.your.next.developer.domain.model.consumer.UserName
import haag.your.next.developer.domain.model.producer.UserNameViewEntity
import haag.your.next.developer.data.repository.PendingAdminIdRepository
import io.mockk.every
import io.mockk.mockk
import haag.your.next.developer.domain.usecase.ClearPendingAdminIdUseCase
import haag.your.next.developer.domain.usecase.CreateChatUseCase
import haag.your.next.developer.domain.usecase.GetChatForUserUseCase
import haag.your.next.developer.domain.usecase.GetCurrentUserUseCase
import haag.your.next.developer.domain.usecase.GetFcmTokenUseCase
import haag.your.next.developer.domain.usecase.GetPendingAdminIdUseCase
import haag.your.next.developer.domain.usecase.GetUserNameUseCase
import haag.your.next.developer.domain.usecase.IsAnonymousUseCase
import haag.your.next.developer.domain.usecase.LoginAnonymouslyUseCase
import haag.your.next.developer.domain.usecase.LogoutUseCase
import haag.your.next.developer.domain.usecase.SavePendingAdminIdUseCase
import haag.your.next.developer.domain.usecase.UpdateFcmTokenUseCase
import haag.your.next.developer.domain.usecase.UpdateUserNameUseCase
import haag.your.next.developer.fakes.FakeAdminRepository
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
class DeepLinkViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val userRepository = FakeUserRepository()
    private val adminRepository = FakeAdminRepository()

    /**
     * MockK mock of the concrete PendingAdminIdRepository (no Android Context needed).
     * We maintain state manually so save/get/clear behave consistently across coroutine
     * boundaries within each test.
     */
    private val pendingAdminIdRepository: PendingAdminIdRepository = mockk<PendingAdminIdRepository>(relaxed = true).also { repo ->
        var stored: String? = null
        every { repo.save(ofType<String>()) } answers { stored = firstArg<String>() }
        every { repo.get() } answers { stored }
        every { repo.clear() } answers { stored = null }
    }

    private fun viewModel() = DeepLinkViewModel(
        GetCurrentUserUseCase(userRepository),
        IsAnonymousUseCase(userRepository),
        LogoutUseCase(userRepository),
        LoginAnonymouslyUseCase(userRepository),
        CreateChatUseCase(adminRepository),
        GetChatForUserUseCase(adminRepository),
        GetUserNameUseCase(adminRepository),
        UpdateUserNameUseCase(userRepository),
        GetFcmTokenUseCase(userRepository),
        UpdateFcmTokenUseCase(userRepository),
        SavePendingAdminIdUseCase(pendingAdminIdRepository),
        GetPendingAdminIdUseCase(pendingAdminIdRepository),
        ClearPendingAdminIdUseCase(pendingAdminIdRepository)
    )

    @Test
    fun `handleDeepLink_whenUserHasName_emitsNavigateToChat`() =
        runTest(mainDispatcherRule.testDispatcher) {
            userRepository.currentUserId = "user-1"
            userRepository.getFcmTokenResult = Result.failure(RuntimeException("no token"))
            adminRepository.getUserNameResult =
                Result.success(UserNameViewEntity(name = "Alice", company = "Acme"))
            adminRepository.createChatResult = Result.success("chat-id-1")
            val vm = viewModel()

            vm.navigateToChat.test {
                vm.handleDeepLink("admin-1")
                assertEquals("chat-id-1", awaitItem())
                cancelAndConsumeRemainingEvents()
            }
        }

    @Test
    fun `handleDeepLink_whenUserHasNoName_showsNameOverlay`() =
        runTest(mainDispatcherRule.testDispatcher) {
            userRepository.currentUserId = "user-1"
            userRepository.getFcmTokenResult = Result.failure(RuntimeException("no token"))
            adminRepository.getUserNameResult =
                Result.success(UserNameViewEntity(name = "", company = ""))
            val vm = viewModel()

            vm.handleDeepLink("admin-1")

            assertTrue(vm.showNameOverlay.value)
            assertTrue(vm.isAnonymous.value)
        }

    @Test
    fun `handleDeepLink_whenGetUserNameFails_emitsLogoutEvent`() =
        runTest(mainDispatcherRule.testDispatcher) {
            userRepository.currentUserId = "user-1"
            userRepository.getFcmTokenResult = Result.failure(RuntimeException("no token"))
            adminRepository.getUserNameResult = Result.failure(RuntimeException("network error"))
            val vm = viewModel()

            vm.logoutEvent.test {
                vm.handleDeepLink("admin-1")
                assertEquals(Unit, awaitItem())
                cancelAndConsumeRemainingEvents()
            }
        }

    @Test
    fun `updateName_success_hidesOverlayAndNavigatesToChat`() =
        runTest(mainDispatcherRule.testDispatcher) {
            userRepository.currentUserId = "user-1"
            userRepository.getFcmTokenResult = Result.failure(RuntimeException("no token"))
            userRepository.updateUserNameResult = Result.success(Unit)
            adminRepository.getUserNameResult =
                Result.success(UserNameViewEntity(name = "", company = ""))
            adminRepository.createChatResult = Result.success("chat-id-2")
            val vm = viewModel()
            // Trigger deep link first so pendingAdminId is set
            vm.handleDeepLink("admin-1")

            vm.navigateToChat.test {
                vm.updateName(UserName(name = "Bob", company = "Acme"))
                assertFalse(vm.showNameOverlay.value)
                assertEquals("chat-id-2", awaitItem())
                cancelAndConsumeRemainingEvents()
            }
        }

    @Test
    fun `findExistingChat_whenChatExists_emitsNavigateToChat`() =
        runTest(mainDispatcherRule.testDispatcher) {
            userRepository.currentUserId = "user-1"
            userRepository.anonymous = true
            adminRepository.getUserNameResult =
                Result.success(UserNameViewEntity(name = "Alice", company = "Acme"))
            adminRepository.chatForUserResult = Result.success("existing-chat")
            val vm = viewModel()

            vm.navigateToChat.test {
                vm.findExistingChat()
                assertEquals("existing-chat", awaitItem())
                cancelAndConsumeRemainingEvents()
            }
        }

    @Test
    fun `findExistingChat_whenGetChatFails_emitsLogoutEvent`() =
        runTest(mainDispatcherRule.testDispatcher) {
            userRepository.currentUserId = "user-1"
            userRepository.anonymous = true
            adminRepository.getUserNameResult =
                Result.success(UserNameViewEntity(name = "Alice", company = "Acme"))
            adminRepository.chatForUserResult = Result.failure(RuntimeException("network error"))
            val vm = viewModel()

            vm.logoutEvent.test {
                vm.findExistingChat()
                assertEquals(Unit, awaitItem())
                cancelAndConsumeRemainingEvents()
            }
        }
}
