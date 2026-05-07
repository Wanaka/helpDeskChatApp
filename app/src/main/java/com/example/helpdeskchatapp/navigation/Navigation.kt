package com.example.helpdeskchatapp.navigation

import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.runtime.Composable
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import com.example.helpdeskchatapp.domain.viewmodel.MainViewModel
import com.example.helpdeskchatapp.ui.admin.AdminRoute
import com.example.helpdeskchatapp.ui.chat.ChatRoute
import com.example.helpdeskchatapp.ui.login.LoginRoute
import com.example.helpdeskchatapp.ui.register.RegisterRoute
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel

@Composable
fun AppNavigation(
    viewModel: MainViewModel = hiltViewModel()
) {
    val backStack = rememberNavBackStack(viewModel.getInitialRoute())

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
                    onBack = { backStack.removeLastOrNull() }
                )
            }
        }
    )
}

