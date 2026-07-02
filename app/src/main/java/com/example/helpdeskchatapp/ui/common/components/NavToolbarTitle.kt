package com.example.helpdeskchatapp.ui.common.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import com.example.helpdeskchatapp.theme.Dimens

@Composable
fun NavToolbarTitle(
    title: String,
    subtitle: String? = null,
    avatarInitials: String? = null,
    avatarRes: Int? = null,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Dimens.dp10),
        modifier = Modifier.padding(start = Dimens.dp8)
    ) {
        avatarRes?.let {
            Image(
                painter = painterResource(it),
                contentDescription = null,
                modifier = Modifier
                    .size(Dimens.dp40)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.onPrimary)
            )
        } ?: avatarInitials?.let {
            InitialsAvatar(initials = it)
        }
        Column(horizontalAlignment = Alignment.Start) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium
            )
            subtitle?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.75f)
                )
            }
        }
    }
}
