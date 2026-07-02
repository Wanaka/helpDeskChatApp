package haag.your.next.developer.data.repository

import android.content.Context
import haag.your.next.developer.data.interfaces.ReadTimestampRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import androidx.core.content.edit

class LocalReadTimestampRepository @Inject constructor(
    @ApplicationContext context: Context
) : ReadTimestampRepository {

    private val prefs = context.getSharedPreferences("chat_read_timestamps", Context.MODE_PRIVATE)

    override fun getLastRead(conversationId: String): Long? {
        val value = prefs.getLong(conversationId, -1L)
        return if (value == -1L) null else value
    }

    override fun saveLastRead(conversationId: String) {
        prefs.edit { putLong(conversationId, System.currentTimeMillis()) }
    }
}
