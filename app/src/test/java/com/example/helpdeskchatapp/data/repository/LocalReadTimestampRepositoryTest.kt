package com.example.helpdeskchatapp.data.repository

import android.content.Context
import android.content.SharedPreferences
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test

class LocalReadTimestampRepositoryTest {

    private val prefs = mockk<SharedPreferences>(relaxed = true)
    private val editor = mockk<SharedPreferences.Editor>(relaxed = true)
    private val context = mockk<Context>()

    private lateinit var repository: LocalReadTimestampRepository

    @Before
    fun setUp() {
        every { context.getSharedPreferences("chat_read_timestamps", Context.MODE_PRIVATE) } returns prefs
        every { prefs.edit() } returns editor
        every { editor.putLong(any(), any()) } returns editor
        repository = LocalReadTimestampRepository(context)
    }

    // ── getLastRead ──────────────────────────────────────────────────────────

    @Test
    fun `getLastRead_whenTimestampStored_returnsStoredValue`() {
        every { prefs.getLong("conv-1", -1L) } returns 1_000_000L

        val result = repository.getLastRead("conv-1")

        assertEquals(1_000_000L, result)
    }

    @Test
    fun `getLastRead_whenNoTimestampStored_returnsNull`() {
        every { prefs.getLong("conv-1", -1L) } returns -1L

        val result = repository.getLastRead("conv-1")

        assertNull(result)
    }

    // ── saveLastRead ─────────────────────────────────────────────────────────

    @Test
    fun `saveLastRead_writesCurrentTimeToPrefsForConversationId`() {
        repository.saveLastRead("conv-1")

        verify { editor.putLong(eq("conv-1"), any()) }
    }
}
