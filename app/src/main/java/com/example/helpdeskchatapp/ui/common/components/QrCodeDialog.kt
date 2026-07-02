package com.example.helpdeskchatapp.ui.common.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import com.example.helpdeskchatapp.R
import com.example.helpdeskchatapp.theme.AndroidGreen
import com.example.helpdeskchatapp.theme.Dimens
import com.example.helpdeskchatapp.util.Deeplink
import com.example.helpdeskchatapp.ui.common.QRCodeGenerator

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QrCodeDialog(
    adminId: String,
    onDismiss: () -> Unit
) {
    val deepLink = Deeplink.getChatDeeplink(adminId)
    val context = LocalContext.current

    val qrBitmap = remember(deepLink) {
        QRCodeGenerator.generate(
            context = context,
            content = deepLink
        )
    }

    BasicAlertDialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(Dimens.dp16),
            color = Color.White,
            tonalElevation = Dimens.dp8
        ) {
            Column(
                modifier = Modifier
                    .background(AndroidGreen.copy(alpha = 0.7f))
                    .padding(Dimens.dp16),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = stringResource(R.string.qr_dialog_name),
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onPrimary
                )
                Spacer(modifier = Modifier.height(Dimens.dp8))
                Text(
                    text = stringResource(R.string.qr_dialog_title),
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.85f)
                )
                Spacer(modifier = Modifier.height(Dimens.dp16))
                qrBitmap?.let {
                    Image(
                        bitmap = it.asImageBitmap(),
                        contentDescription = stringResource(R.string.qr_dialog_content_description),
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(1f)
                    )
                }
                Spacer(modifier = Modifier.height(Dimens.dp12))
                Text(
                    text = stringResource(R.string.qr_dialog_subtitle),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.85f)
                )
            }
        }
    }
}
