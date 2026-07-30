package haag.your.next.developer.domain.viewmodel

import app.cash.turbine.test
import haag.your.next.developer.domain.model.consumer.UserName
import haag.your.next.developer.domain.model.producer.UserNameViewEntity
import haag.your.next.developer.data.repository.PendingAdminIdRepository
import io.mockk.every
import io.mockk.mockk
import haag.your.next.developer.domain.usecase.ClearPendingAdminIdUseCase
import haag.your.next.developer.domain.usecase.CreateChatUseCase
import haag.your.next.developer.domain.usecase.FindExistingChatUseCase
import haag.your.next.developer.domain.usecase.GetChatForUserUseCase
import haag.your.next.developer.domain.usecase.GetCurrentUserUseCase
import haag.your.next.developer.domain.usecase.GetFcmTokenUseCase
import haag.your.next.developer.domain.usecase.GetNameUpdateContextUseCase
import haag.your.next.developer.domain.usecase.GetPendingAdminIdUseCase
import haag.your.next.developer.domain.usecase.GetUserNameUseCase
import haag.your.next.developer.domain.usecase.IsAnonymousUseCase
import haag.your.next.developer.domain.usecase.LoginAnonymouslyUseCase
import haag.your.next.developer.domain.usecase.LogoutUseCase
import haag.your.next.developer.domain.usecase.PrepareDeepLinkSessionUseCase
import haag.your.next.developer.domain.usecase.SavePendingAdminIdUseCase
import haag.your.next.developer.domain.usecase.StartChatUseCase
import haag.your.next.developer.domain.usecase.SubmitUserNameUseCase
import haag.your.next.developer.domain.usecase.SyncFcmTokenUseCase
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

    private fun viewModel(): DeepLinkViewModel {
        val getCurrentUserUseCase = GetCurrentUserUseCase(userRepository)
        val isAnonymousUseCase = IsAnonymousUseCase(userRepository)
        val getUserNameUseCase = GetUserNameUseCase(adminRepository)
        val syncFcmTokenUseCase = SyncFcmTokenUseCase(
            GetFcmTokenUseCase(userRepository),
            UpdateFcmTokenUseCase(userRepository)
        )
        val getPendingAdminIdUseCase = GetPendingAdminIdUseCase(pendingAdminIdRepository)
        val clearPendingAdminIdUseCase = ClearPendingAdminIdUseCase(pendingAdminIdRepository)
        val savePendingAdminIdUseCase = SavePendingAdminIdUseCase(pendingAdminIdRepository)
        val createChatUseCase = CreateChatUseCase(adminRepository)
        val startChatUseCase = StartChatUseCase(createChatUseCase, clearPendingAdminIdUseCase)
        val getChatForUserUseCase = GetChatForUserUseCase(adminRepository)

        return DeepLinkViewModel(
            LogoutUseCase(userRepository),
            PrepareDeepLinkSessionUseCase(
                getCurrentUserUseCase,
                LoginAnonymouslyUseCase(userRepository),
                syncFcmTokenUseCase,
                getUserNameUseCase
            ),
            startChatUseCase,
            SubmitUserNameUseCase(
                GetNameUpdateContextUseCase(getCurrentUserUseCase, getPendingAdminIdUseCase),
                UpdateUserNameUseCase(userRepository),
                startChatUseCase,
                getChatForUserUseCase
            ),
            FindExistingChatUseCase(
                getCurrentUserUseCase,
                isAnonymousUseCase,
                getUserNameUseCase,
                getChatForUserUseCase,
                getPendingAdminIdUseCase,
                startChatUseCase
            ),
            savePendingAdminIdUseCase
        )
    }

    @Test
    fun handleDeepLinkWhenUserHasNameEmitsNavigateToChat() =
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
    fun handleDeepLinkWhenUserHasNoNameShowsNameOverlay() =
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
    fun handleDeepLinkWhenGetUserNameFailsEmitsLogoutEvent() =
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
    fun updateNameSuccessHidesOverlayAndNavigatesToChat() =
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
    fun findExistingChatWhenChatExistsEmitsNavigateToChat() =
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
    fun findExistingChatWhenGetChatFailsEmitsLogoutEvent() =
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
