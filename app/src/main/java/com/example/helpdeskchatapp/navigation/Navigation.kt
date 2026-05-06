package com.example.helpdeskchatapp.navigation

import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.runtime.Composable
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import com.example.helpdeskchatapp.ui.admin.AdminRoute
import com.example.helpdeskchatapp.ui.chat.ChatRoute
import com.example.helpdeskchatapp.ui.login.LoginRoute

@Composable
fun AppNavigation() {
    val backStack = rememberNavBackStack(LoginRouteKey)

    NavDisplay(
        backStack = backStack,
        onBack = { backStack.removeLastOrNull() },
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
                    }
                )
            }

            entry<AdminRouteKey> {
                AdminRoute(
                    onNavigateToChat = { chatId ->
                        backStack.add(ChatRouteKey(chatId))
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

