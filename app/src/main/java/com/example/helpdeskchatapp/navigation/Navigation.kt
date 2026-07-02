package com.example.helpdeskchatapp.navigation

import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.example.helpdeskchatapp.domain.viewmodel.DeepLinkViewModel
import com.example.helpdeskchatapp.domain.viewmodel.MainViewModel
import com.example.helpdeskchatapp.theme.Dimens
import com.example.helpdeskchatapp.ui.admin.AdminRoute
import com.example.helpdeskchatapp.ui.chat.ChatRoute
import com.example.helpdeskchatapp.ui.common.components.AvatarImage
import com.example.helpdeskchatapp.ui.common.components.NameEntryDialog
import com.example.helpdeskchatapp.ui.login.LoginRoute
import com.example.helpdeskchatapp.ui.register.RegisterRoute

@Composable
fun AppNavigation(
    conversationId: String? = null,
    mainViewModel: MainViewModel = hiltViewModel(),
    deepLinkViewModel: DeepLinkViewModel = hiltViewModel()
) {
    val initialRoute by mainViewModel.initialRoute.collectAsStateWithLifecycle()
    val showNameOverlay by deepLinkViewModel.showNameOverlay.collectAsStateWithLifecycle()
    val isAnonymous by deepLinkViewModel.isAnonymous.collectAsStateWithLifecycle()

    LaunchedEffect(conversationId) {
        mainViewModel.resolveInitialRoute(conversationId)
    }

    val resolvedRoute = initialRoute ?: return

    val backStack = rememberNavBackStack(resolvedRoute)

    if (showNameOverlay) {
        NameEntryDialog(onConfirm = deepLinkViewModel::updateName, isAnonymous = isAnonymous)
    }

    LaunchedEffect(conversationId) {
        if (conversationId != null) {
            deepLinkViewModel.handleDeepLink(conversationId)
        } else {
            deepLinkViewModel.findExistingChat()
        }
    }

    LaunchedEffect(Unit) {
        deepLinkViewModel.logoutEvent.collect {
            backStack.clear()
            backStack.add(LoginRouteKey)
        }
    }

    LaunchedEffect(Unit) {
        deepLinkViewModel.navigateToChat.collect { chatId ->
            val chatKey = ChatRouteKey(chatId)
            if (backStack.lastOrNull() != chatKey) {
                backStack.clear()
                backStack.add(chatKey)
            }
        }
    }

    NavDisplay(
        backStack = backStack,
        onBack = { backStack.removeLastOrNull() },
        entryDecorators = listOf(
            rememberSaveableStateHolderNavEntryDecorator(),
            rememberViewModelStoreNavEntryDecorator()
        ),
        transitionSpec = {
            slideInHorizontally(initialOffsetX = { it }) + fadeIn() togetherWith
                    slideOutHorizontally(targetOffsetX = { -it }) + fadeOut()
        },
        popTransitionSpec = {
            slideInHorizontally(initialOffsetX = { -it }) + fadeIn() togetherWith
                    slideOutHorizontally(targetOffsetX = { it }) + fadeOut()
        },
        entryProvider = entryProvider {
            entry<LoginRouteKey> {
                LoginRoute(
                    onNavigateToAdmin = {
                        backStack.add(AdminRouteKey)
                    },
                    onNavigateToRegister = {
                        backStack.add(RegisterRouteKey)
                    }
                )
            }

            entry<RegisterRouteKey> {
                RegisterRoute(
                    onNavigateToAdmin = {
                        backStack.add(AdminRouteKey)
                    },
                    onNavigateToLogin = {
                        backStack.removeLastOrNull()
                    }
                )
            }

            entry<AdminRouteKey> {
                AdminRoute(
                    onNavigateToChat = { chatId ->
                        backStack.add(ChatRouteKey(chatId))
                    },
                    onLogout = {
                        backStack.clear()
                        backStack.add(LoginRouteKey)
                    }
                )
            }

            entry<ChatRouteKey> { chatRoute ->
                ChatRoute(
                    conversationId = chatRoute.conversationId,
                    onBack = { backStack.removeLastOrNull() },
                    canNavigateBack = backStack.size > 1
                )
            }

            entry<DeepLinkLoadingKey> {
                Box(
                    modifier = Modifier
                        .fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    AvatarImage(size = Dimens.dp100)
                }
            }
        }
    )
}

