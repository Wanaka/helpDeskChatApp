package com.example.helpdeskchatapp.ui.common.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.OutlinedTextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation

@Composable
fun CommonInputTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    isPasswordField: Boolean = false
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { CommonTextLabel(label) },
        visualTransformation = if (isPasswordField) PasswordVisualTransformation() else VisualTransformation.None,
        modifier = Modifier.fillMaxWidth()
    )
}
