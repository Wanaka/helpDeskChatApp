package com.example.helpdeskchatapp.ui.common.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.helpdeskchatapp.ui.model.ListRowEntity

@Composable
fun StandardListRow(entity: ListRowEntity) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = entity.onClick)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (entity.showLeftIcon && entity.leftIcon != null) {
            Icon(
                imageVector = entity.leftIcon,
                contentDescription = null,
                modifier = Modifier.size(40.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.width(16.dp))
        }

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = entity.title,
                style = MaterialTheme.typography.titleMedium
            )
            entity.subtitle?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 1
                )
            }
        }

        if (entity.showRightIcon && entity.rightIcon != null) {
            Icon(
                imageVector = entity.rightIcon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.outline
            )
        }
    }
}
