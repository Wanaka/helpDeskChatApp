package haag.your.next.developer.domain.viewmodel

import app.cash.turbine.test
import haag.your.next.developer.domain.model.consumer.Message
import haag.your.next.developer.domain.model.producer.UserNameViewEntity
import haag.your.next.developer.domain.usecase.GetAdminNameUseCase
import haag.your.next.developer.domain.usecase.GetChatMessagesUseCase
import haag.your.next.developer.domain.usecase.GetCurrentUserUseCase
import haag.your.next.developer.domain.usecase.GetUserNameUseCase
import haag.your.next.developer.domain.usecase.IsAnonymousUseCase
import haag.your.next.developer.domain.usecase.SaveLocalReadTimestampUseCase
import haag.your.next.developer.domain.usecase.SendMessageUseCase
import haag.your.next.developer.fakes.FakeAdminRepository
import haag.your.next.developer.fakes.FakeChatRepository
import haag.your.next.developer.fakes.FakeReadTimestampRepository
import haag.your.next.developer.fakes.FakeUserRepository
import haag.your.next.developer.util.MainDispatcherRule
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
    private val timestampRepository = FakeReadTimestampRepository()

    private fun viewModel() = ChatViewModel(
        GetChatMessagesUseCase(chatRepository),
        SendMessageUseCase(chatRepository),
        IsAnonymousUseCase(userRepository),
        GetUserNameUseCase(adminRepository),
        GetAdminNameUseCase(chatRepository),
        GetCurrentUserUseCase(userRepository),
        SaveLocalReadTimestampUseCase(timestampRepository)
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
