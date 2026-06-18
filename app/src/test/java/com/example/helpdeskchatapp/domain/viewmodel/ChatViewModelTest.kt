package com.example.helpdeskchatapp.domain.viewmodel

import app.cash.turbine.test
import com.example.helpdeskchatapp.domain.model.consumer.Message
import com.example.helpdeskchatapp.domain.model.producer.UserNameViewEntity
import com.example.helpdeskchatapp.domain.usecase.GetChatMessagesUseCase
import com.example.helpdeskchatapp.domain.usecase.GetCurrentUserUseCase
import com.example.helpdeskchatapp.domain.usecase.GetUserNameUseCase
import com.example.helpdeskchatapp.domain.usecase.IsAnonymousUseCase
import com.example.helpdeskchatapp.domain.usecase.SendMessageUseCase
import com.example.helpdeskchatapp.fakes.FakeAdminRepository
import com.example.helpdeskchatapp.fakes.FakeChatRepository
import com.example.helpdeskchatapp.fakes.FakeUserRepository
import com.example.helpdeskchatapp.util.MainDispatcherRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ChatViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val chatRepository = FakeChatRepository()
    private val userRepository = FakeUserRepository()
    private val adminRepository = FakeAdminRepository()

    private fun viewModel() = ChatViewModel(
        GetChatMessagesUseCase(chatRepository),
        SendMessageUseCase(chatRepository),
        IsAnonymousUseCase(userRepository),
        GetUserNameUseCase(adminRepository),
        GetCurrentUserUseCase(userRepository)
    )

    @Test
    fun `sendMessage_delegatesToUseCaseWithMessageText`() =
        runTest(mainDispatcherRule.testDispatcher) {
            chatRepository.sendMessageResult = Result.success("msg-id")
            val vm = viewModel()

            vm.sendMessage(Message(message = "hello"))

            assertEquals("hello", chatRepository.sentMessages.single().message)
        }

    @Test
    fun `sendMessage_failure_emitsToast`() =
        runTest(mainDispatcherRule.testDispatcher) {
            chatRepository.sendMessageResult = Result.failure(RuntimeException("send failed"))
            val vm = viewModel()

            vm.toastEvent.test {
                vm.sendMessage(Message(message = "hello"))
                assertEquals("send failed", awaitItem())
                cancelAndConsumeRemainingEvents()
            }
        }

    @Test
    fun `initConversation_whenNotAnonymous_setsChatTitleFromUserName`() =
        runTest(mainDispatcherRule.testDispatcher) {
            userRepository.anonymous = false
            userRepository.currentUserId = "user-1"
            chatRepository.messages = emptyList()
            adminRepository.getUserNameResult =
                Result.success(UserNameViewEntity(name = "Bob", company = "Acme"))
            val vm = viewModel()

            vm.initConversation("conversation-1")

            assertEquals(UserNameViewEntity(name = "Bob", company = "Acme"), vm.chatTitle.value)
        }
}
