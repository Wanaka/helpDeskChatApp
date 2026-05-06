package com.example.helpdeskchatapp.ui.common.components

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun CommonHeader(
    text: String,
    subext: String? = null,
    modifier: Modifier = Modifier
) {
    Text(
        text = text,
        style = MaterialTheme.typography.headlineSmall,
        modifier = modifier
    )

    subext?.let {
        Text(
            text = it,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier
        )
    }

}
