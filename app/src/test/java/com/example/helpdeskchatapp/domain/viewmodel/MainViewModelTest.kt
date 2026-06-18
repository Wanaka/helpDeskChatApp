package com.example.helpdeskchatapp.domain.viewmodel

import com.example.helpdeskchatapp.domain.usecase.GetCurrentUserUseCase
import com.example.helpdeskchatapp.domain.usecase.IsAnonymousUseCase
import com.example.helpdeskchatapp.fakes.FakeUserRepository
import com.example.helpdeskchatapp.navigation.AdminRouteKey
import com.example.helpdeskchatapp.navigation.DeepLinkLoadingKey
import com.example.helpdeskchatapp.navigation.LoginRouteKey
import com.example.helpdeskchatapp.util.MainDispatcherRule
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
