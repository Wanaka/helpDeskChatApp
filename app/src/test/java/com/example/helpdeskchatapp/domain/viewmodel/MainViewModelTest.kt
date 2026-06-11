package com.example.helpdeskchatapp.domain.viewmodel

import com.example.helpdeskchatapp.domain.usecase.CreateChatUseCase
import com.example.helpdeskchatapp.domain.usecase.GetChatForUserUseCase
import com.example.helpdeskchatapp.domain.usecase.GetCurrentUserUseCase
import com.example.helpdeskchatapp.domain.usecase.GetUserNameUseCase
import com.example.helpdeskchatapp.domain.usecase.IsAnonymousUseCase
import com.example.helpdeskchatapp.domain.usecase.LoginAnonymouslyUseCase
import com.example.helpdeskchatapp.domain.usecase.LogoutUseCase
import com.example.helpdeskchatapp.domain.usecase.UpdateFcmTokenUseCase
import com.example.helpdeskchatapp.domain.usecase.UpdateUserNameUseCase
import com.example.helpdeskchatapp.navigation.AdminRouteKey
import com.example.helpdeskchatapp.navigation.DeepLinkLoadingKey
import com.example.helpdeskchatapp.navigation.LoginRouteKey
import io.mockk.every
import io.mockk.mockk
import org.junit.Assert.assertEquals
import org.junit.Test

class MainViewModelTest {

    private val getCurrentUserUseCase = mockk<GetCurrentUserUseCase>()
    private val isAnonymousUseCase = mockk<IsAnonymousUseCase>()
    private val logoutUseCase = mockk<LogoutUseCase>(relaxed = true)
    private val loginAnonymouslyUseCase = mockk<LoginAnonymouslyUseCase>(relaxed = true)
    private val createChatUseCase = mockk<CreateChatUseCase>(relaxed = true)
    private val getChatForUserUseCase = mockk<GetChatForUserUseCase>(relaxed = true)
    private val getUserNameUseCase = mockk<GetUserNameUseCase>(relaxed = true)
    private val updateUserNameUseCase = mockk<UpdateUserNameUseCase>(relaxed = true)
    private val updateFcmTokenUseCase = mockk<UpdateFcmTokenUseCase>(relaxed = true)

    private fun viewModel() = MainViewModel(
        getCurrentUserUseCase,
        isAnonymousUseCase,
        logoutUseCase,
        loginAnonymouslyUseCase,
        createChatUseCase,
        getChatForUserUseCase,
        getUserNameUseCase,
        updateUserNameUseCase,
        updateFcmTokenUseCase
    )

    @Test
    fun getInitialRoute_withConversationId_returnsDeepLinkLoading() {
        val route = viewModel().getInitialRoute(conversationId = "abc")

        assertEquals(DeepLinkLoadingKey, route)
    }

    @Test
    fun getInitialRoute_noLoggedInUser_returnsLogin() {
        every { getCurrentUserUseCase() } returns null

        val route = viewModel().getInitialRoute(conversationId = null)

        assertEquals(LoginRouteKey, route)
    }

    @Test
    fun getInitialRoute_anonymousUser_returnsDeepLinkLoading() {
        every { getCurrentUserUseCase() } returns "uid-1"
        every { isAnonymousUseCase() } returns true

        val route = viewModel().getInitialRoute(conversationId = null)

        assertEquals(DeepLinkLoadingKey, route)
    }

    @Test
    fun getInitialRoute_authenticatedAdmin_returnsAdmin() {
        every { getCurrentUserUseCase() } returns "uid-1"
        every { isAnonymousUseCase() } returns false

        val route = viewModel().getInitialRoute(conversationId = null)

        assertEquals(AdminRouteKey, route)
    }
}
