package haag.your.next.developer

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import haag.your.next.developer.domain.usecase.SavePendingAdminIdUseCase
import haag.your.next.developer.navigation.AppNavigation
import haag.your.next.developer.theme.MyApplicationTheme
import haag.your.next.developer.util.checkInstallReferrer
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject lateinit var savePendingAdminIdUseCase: SavePendingAdminIdUseCase

    private var conversationIdState by mutableStateOf<String?>(null)

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            Log.d("MainActivity", "Notification permission granted")
        } else {
            Log.d("MainActivity", "Notification permission denied")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        askNotificationPermission()
        checkInstallReferrer(this, savePendingAdminIdUseCase)

        conversationIdState = getConversationIdFromIntent(intent)

        setContent {
            MyApplicationTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppNavigation(conversationId = conversationIdState)
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        conversationIdState = getConversationIdFromIntent(intent)
    }

    private fun askNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) !=
                PackageManager.PERMISSION_GRANTED
            ) {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }

    private fun getConversationIdFromIntent(intent: Intent?): String? {
        return if (intent?.action == Intent.ACTION_VIEW) {
            intent.data?.lastPathSegment
        } else {
            null
        }
    }
}
