package haag.your.next.developer.domain.viewmodel

import haag.your.next.developer.domain.usecase.GetCurrentUserUseCase
import haag.your.next.developer.domain.usecase.IsAnonymousUseCase
import haag.your.next.developer.fakes.FakeUserRepository
import haag.your.next.developer.navigation.AdminRouteKey
import haag.your.next.developer.navigation.DeepLinkLoadingKey
import haag.your.next.developer.navigation.LoginRouteKey
import haag.your.next.developer.util.MainDispatcherRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class MainViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val userRepository = FakeUserRepository()

    private fun viewModel() = MainViewModel(
        GetCurrentUserUseCase(userRepository),
        IsAnonymousUseCase(userRepository)
    )

    @Test
    fun `resolveInitialRoute_withConversationId_setsDeepLinkLoading`() =
        runTest(mainDispatcherRule.testDispatcher) {
            val vm = viewModel()

            vm.resolveInitialRoute(conversationId = "abc")

            assertEquals(DeepLinkLoadingKey, vm.initialRoute.value)
        }

    @Test
    fun `resolveInitialRoute_noLoggedInUser_setsLogin`() =
        runTest(mainDispatcherRule.testDispatcher) {
            userRepository.currentUserId = null
            val vm = viewModel()

            vm.resolveInitialRoute(conversationId = null)

            assertEquals(LoginRouteKey, vm.initialRoute.value)
        }

    @Test
    fun `resolveInitialRoute_anonymousUser_setsDeepLinkLoading`() =
        runTest(mainDispatcherRule.testDispatcher) {
            userRepository.currentUserId = "uid-1"
            userRepository.anonymous = true
            val vm = viewModel()

            vm.resolveInitialRoute(conversationId = null)

            assertEquals(DeepLinkLoadingKey, vm.initialRoute.value)
        }

    @Test
    fun `resolveInitialRoute_authenticatedAdmin_setsAdmin`() =
        runTest(mainDispatcherRule.testDispatcher) {
            userRepository.currentUserId = "uid-1"
            userRepository.anonymous = false
            val vm = viewModel()

            vm.resolveInitialRoute(conversationId = null)

            assertEquals(AdminRouteKey, vm.initialRoute.value)
        }
}
