package com.example.helpdeskchatapp.ui.common.components

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

@Composable
fun CommonTextLabel(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.labelLarge
    )
}
