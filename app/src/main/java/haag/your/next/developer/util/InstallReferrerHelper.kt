package haag.your.next.developer.util

import android.content.Context
import com.android.installreferrer.api.InstallReferrerClient
import com.android.installreferrer.api.InstallReferrerStateListener
import haag.your.next.developer.domain.usecase.SavePendingAdminIdUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

fun checkInstallReferrer(
    context: Context,
    savePendingAdminIdUseCase: SavePendingAdminIdUseCase
) {
    val client = InstallReferrerClient.newBuilder(context).build()
    client.startConnection(object : InstallReferrerStateListener {
        override fun onInstallReferrerSetupFinished(responseCode: Int) {
            if (responseCode == InstallReferrerClient.InstallReferrerResponse.OK) {
                val referrer = client.installReferrer.installReferrer
                // referrer looks like "adminId=ABC123"
                val adminId = referrer
                    .split("&")
                    .firstOrNull { it.startsWith("adminId=") }
                    ?.removePrefix("adminId=")

                if (!adminId.isNullOrBlank()) {
                    CoroutineScope(Dispatchers.IO).launch {
                        savePendingAdminIdUseCase(adminId)
                    }
                }
            }
            client.endConnection()
        }

        override fun onInstallReferrerServiceDisconnected() = Unit
    })
}
