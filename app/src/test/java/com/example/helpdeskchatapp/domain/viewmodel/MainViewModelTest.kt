package haag.your.next.developer.domain.viewmodel

import android.app.Application
import haag.your.next.developer.data.repository.PendingAdminIdRepository
import haag.your.next.developer.domain.usecase.GetCurrentUserUseCase
import haag.your.next.developer.domain.usecase.GetUserSessionUseCase
import haag.your.next.developer.domain.usecase.IsAnonymousUseCase
import haag.your.next.developer.domain.usecase.SavePendingAdminIdUseCase
import haag.your.next.developer.fakes.FakeUserRepository
import haag.your.next.developer.navigation.AdminRouteKey
import haag.your.next.developer.navigation.DeepLinkLoadingKey
import haag.your.next.developer.navigation.LoginRouteKey
import haag.your.next.developer.util.MainDispatcherRule
import haag.your.next.developer.util.checkInstallReferrer
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.runs
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class MainViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val userRepository = FakeUserRepository()

    /** Stub Application so checkInstallReferrer does not NPE on a real Context. */
    private val application: Application = mockk(relaxed = true)

    private val pendingAdminIdRepository: PendingAdminIdRepository =
        mockk(relaxed = true)

    @Before
    fun stubInstallReferrer() {
        // checkInstallReferrer calls Android framework classes (Log, Intent) that are
        // not available in JVM unit tests. Stub the top-level function to a no-op so
        // MainViewModel.init does not crash.
        mockkStatic("haag.your.next.developer.util.InstallReferrerHelperKt")
        every { checkInstallReferrer(any(), any()) } just runs
    }

    private fun viewModel() = MainViewModel(
        application,
        GetUserSessionUseCase(
            GetCurrentUserUseCase(userRepository),
            IsAnonymousUseCase(userRepository)
        ),
        SavePendingAdminIdUseCase(pendingAdminIdRepository)
    )

    @Test
    fun resolveInitialRouteWithConversationIdSetsDeepLinkLoading() =
        runTest(mainDispatcherRule.testDispatcher) {
            val vm = viewModel()

            vm.resolveInitialRoute(conversationId = "abc")

            assertEquals(DeepLinkLoadingKey, vm.initialRoute.value)
        }

    @Test
    fun resolveInitialRouteNoLoggedInUserSetsLogin() =
        runTest(mainDispatcherRule.testDispatcher) {
            userRepository.currentUserId = null
            val vm = viewModel()

            vm.resolveInitialRoute(conversationId = null)

            assertEquals(LoginRouteKey, vm.initialRoute.value)
        }

    @Test
    fun resolveInitialRouteAnonymousUserSetsDeepLinkLoading() =
        runTest(mainDispatcherRule.testDispatcher) {
            userRepository.currentUserId = "uid-1"
            userRepository.anonymous = true
            val vm = viewModel()

            vm.resolveInitialRoute(conversationId = null)

            assertEquals(DeepLinkLoadingKey, vm.initialRoute.value)
        }

    @Test
    fun resolveInitialRouteAuthenticatedAdminSetsAdmin() =
        runTest(mainDispatcherRule.testDispatcher) {
            userRepository.currentUserId = "uid-1"
            userRepository.anonymous = false
            val vm = viewModel()

            vm.resolveInitialRoute(conversationId = null)

            assertEquals(AdminRouteKey, vm.initialRoute.value)
        }
}
