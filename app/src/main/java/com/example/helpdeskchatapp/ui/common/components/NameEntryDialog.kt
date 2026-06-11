package com.example.helpdeskchatapp.ui.common.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.helpdeskchatapp.domain.model.consumer.UserName

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NameEntryDialog(
    onConfirm: (UserName) -> Unit,
    isAnonymous: Boolean = false
) {
    var name by remember { mutableStateOf("") }
    var company by remember { mutableStateOf(if (isAnonymous) "" else "_") }

    BasicAlertDialog(
        onDismissRequest = { },
    ) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 4.dp,
            modifier = Modifier.padding(24.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp)
                    .fillMaxWidth()
            ) {
                CommonHeader("Welcome! You need to enter your name!")
                
                Spacer(modifier = Modifier.height(16.dp))
                
                CommonInputTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = "Your Name"
                )

                Spacer(modifier = Modifier.height(16.dp))

                if (isAnonymous)
                    CommonInputTextField(
                        value = company,
                        onValueChange = { company = it },
                        label = "Company name"
                    )

                Spacer(modifier = Modifier.height(24.dp))
                
                CommonButton(
                    text = "Start",
                    onClick = {
                        if (name.isNotBlank() && company.isNotBlank()) {
                            onConfirm(UserName(name, company))
                        }
                    }
                )
            }
        }
    }
}
