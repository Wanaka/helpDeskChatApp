package haag.your.next.developer.data.repository

import android.content.Context
import androidx.core.content.edit
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class PendingAdminIdRepository @Inject constructor(
    @ApplicationContext context: Context
) {
    private val prefs = context.getSharedPreferences("anonymous_onboarding", Context.MODE_PRIVATE)

    fun save(adminId: String) {
        prefs.edit { putString("pending_admin_id", adminId) }
    }

    fun get(): String? = prefs.getString("pending_admin_id", null)

    fun clear() {
        prefs.edit { remove("pending_admin_id") }
    }
}
