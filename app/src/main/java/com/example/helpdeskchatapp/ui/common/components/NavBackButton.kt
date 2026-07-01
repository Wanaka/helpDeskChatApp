package com.example.helpdeskchatapp.ui.common.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import com.example.helpdeskchatapp.theme.Dimens

@Composable
fun NavBackButton(onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .padding(start = Dimens.dp16)
            .size(Dimens.dp40)
            .clip(RoundedCornerShape(Dimens.dp10))
            .background(MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.15f))
    ) {
        IconButton(onClick = onClick) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back"
            )
        }
    }
}
