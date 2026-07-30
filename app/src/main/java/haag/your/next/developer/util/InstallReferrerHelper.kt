package haag.your.next.developer.util

import android.content.Context
import com.android.installreferrer.api.InstallReferrerClient
import com.android.installreferrer.api.InstallReferrerStateListener

fun checkInstallReferrer(context: Context, onAdminIdFound: (String) -> Unit) {
    val client = InstallReferrerClient.newBuilder(context).build()
    client.startConnection(object : InstallReferrerStateListener {
        override fun onInstallReferrerSetupFinished(responseCode: Int) {
            if (responseCode == InstallReferrerClient.InstallReferrerResponse.OK) {
                val referrer = client.installReferrer.installReferrer
                val adminId = referrer
                    .split("&")
                    .firstOrNull { it.startsWith("adminId=") }
                    ?.removePrefix("adminId=")
                if (!adminId.isNullOrBlank()) onAdminIdFound(adminId)
            }
            client.endConnection()
        }

        override fun onInstallReferrerServiceDisconnected() = Unit
    })
}
