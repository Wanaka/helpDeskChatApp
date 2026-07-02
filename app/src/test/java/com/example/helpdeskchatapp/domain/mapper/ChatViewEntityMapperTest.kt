package haag.your.next.developer.domain.mapper

import haag.your.next.developer.domain.model.producer.ChatViewEntity
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ChatViewEntityMapperTest {

    private fun entity(
        id: String = "conv-1",
        sender: String = "Alice",
        company: String = "Acme",
        message: String = "Hello",
        adminName: String = "Bob",
        userId: String = "u1",
        lastMessageTimestamp: Long? = null
    ) = ChatViewEntity(id, sender, company, message, adminName, userId, lastMessageTimestamp)

    // ── withBadges ───────────────────────────────────────────────────────────

    @Test
    fun `withBadges_mapsTitle_fromSenderField`() = runTest {
        val result = listOf(entity(sender = "Alice")).withBadges(null) { null }
        assertEquals("Alice", result.single().title)
    }

    @Test
    fun `withBadges_mapsSecondSubtitle_fromCompanyField`() = runTest {
        val result = listOf(entity(company = "Acme")).withBadges(null) { null }
        assertEquals("Acme", result.single().secondSubtitle)
    }

    @Test
    fun `withBadges_mapsThirdSubtitle_fromMessageField`() = runTest {
        val result = listOf(entity(message = "Hello world")).withBadges(null) { null }
        assertEquals("Hello world", result.single().thirdSubtitle)
    }

    @Test
    fun `withBadges_whenCompanyBlank_secondSubtitleIsNull`() = runTest {
        val result = listOf(entity(company = "")).withBadges(null) { null }
        assertNull(result.single().secondSubtitle)
    }

    @Test
    fun `withBadges_whenMessageExceeds40Chars_truncatesWithEllipsis`() = runTest {
        val longMessage = "A".repeat(50)
        val result = listOf(entity(message = longMessage)).withBadges(null) { null }
        val subtitle = result.single().thirdSubtitle!!
        assertTrue(subtitle.length <= 41) // 40 chars + ellipsis char
        assertTrue(subtitle.endsWith("…"))
    }

    @Test
    fun `withBadges_whenMessageExactly40Chars_noEllipsis`() = runTest {
        val message = "A".repeat(40)
        val result = listOf(entity(message = message)).withBadges(null) { null }
        assertFalse(result.single().thirdSubtitle!!.endsWith("…"))
    }

    // ── hasUnreadBadge ───────────────────────────────────────────────────────

    @Test
    fun `hasUnreadBadge_whenActiveConversation_returnsFalse`() {
        val e = entity(id = "conv-1", lastMessageTimestamp = 1000L)
        assertFalse(e.hasUnreadBadge(activeConversationId = "conv-1", lastRead = null))
    }

    @Test
    fun `hasUnreadBadge_whenNoTimestamp_returnsFalse`() {
        val e = entity(id = "conv-1", lastMessageTimestamp = null)
        assertFalse(e.hasUnreadBadge(activeConversationId = null, lastRead = null))
    }

    @Test
    fun `hasUnreadBadge_whenNeverRead_returnsTrue`() {
        val e = entity(id = "conv-1", lastMessageTimestamp = 1000L)
        assertTrue(e.hasUnreadBadge(activeConversationId = null, lastRead = null))
    }

    @Test
    fun `hasUnreadBadge_whenLastReadOlderThanTimestamp_returnsTrue`() {
        val e = entity(id = "conv-1", lastMessageTimestamp = 2000L)
        assertTrue(e.hasUnreadBadge(activeConversationId = null, lastRead = 1000L))
    }

    @Test
    fun `hasUnreadBadge_whenLastReadNewerThanTimestamp_returnsFalse`() {
        val e = entity(id = "conv-1", lastMessageTimestamp = 1000L)
        assertFalse(e.hasUnreadBadge(activeConversationId = null, lastRead = 2000L))
    }

    @Test
    fun `hasUnreadBadge_whenLastReadEqualsTimestamp_returnsFalse`() {
        val e = entity(id = "conv-1", lastMessageTimestamp = 1000L)
        assertFalse(e.hasUnreadBadge(activeConversationId = null, lastRead = 1000L))
    }

    // ── withBadges showBadge propagation ─────────────────────────────────────

    @Test
    fun `withBadges_setsShowBadge_whenHasUnread`() = runTest {
        val e = entity(id = "conv-1", lastMessageTimestamp = 2000L)
        val result = listOf(e).withBadges(activeConversationId = null) { 1000L }
        assertTrue(result.single().showBadge)
    }

    @Test
    fun `withBadges_clearsShowBadge_whenRead`() = runTest {
        val e = entity(id = "conv-1", lastMessageTimestamp = 1000L)
        val result = listOf(e).withBadges(activeConversationId = null) { 2000L }
        assertFalse(result.single().showBadge)
    }
}
