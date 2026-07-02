package com.example.helpdeskchatapp.ui.common.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.example.helpdeskchatapp.R
import com.example.helpdeskchatapp.domain.model.consumer.UserName
import com.example.helpdeskchatapp.theme.Dimens

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NameEntryDialog(
    onConfirm: (UserName) -> Unit,
    isAnonymous: Boolean = false
) {
    var name by remember { mutableStateOf("") }
    var company by remember { mutableStateOf(if (isAnonymous) "" else "_") }

    BasicAlertDialog(onDismissRequest = { }) {
        Surface(
            shape = RoundedCornerShape(Dimens.dp16),
            color = MaterialTheme.colorScheme.primary
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(Dimens.dp24))

                AvatarImage(size = Dimens.dp64, showBorder = true)

                Spacer(modifier = Modifier.height(Dimens.dp12))

                CommonTextLabel(
                    text = if (isAnonymous) stringResource(R.string.name_dialog_anon_hero) else stringResource(R.string.name_dialog_admin_hero),
                    style = TextLabelStyle.HeroTitle
                )

                Spacer(modifier = Modifier.height(Dimens.dp16))

                Surface(
                    shape = RoundedCornerShape(topStart = Dimens.dp16, topEnd = Dimens.dp16),
                    color = MaterialTheme.colorScheme.background,
                    border = BorderStroke(Dimens.dp1, MaterialTheme.colorScheme.onPrimary)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .verticalScroll(rememberScrollState())
                            .imePadding()
                            .padding(Dimens.dp24),
                        horizontalAlignment = Alignment.Start
                    ) {
                        CommonTextLabel(
                            text = if (isAnonymous) stringResource(R.string.name_dialog_anon_title) else stringResource(R.string.name_dialog_admin_title),
                            style = TextLabelStyle.TitleSmall
                        )

                        Spacer(modifier = Modifier.height(Dimens.dp4))

                        CommonTextLabel(
                            text = if (isAnonymous) stringResource(R.string.name_dialog_anon_subtitle) else stringResource(R.string.name_dialog_admin_subtitle),
                            style = TextLabelStyle.Subtitle
                        )

                        Spacer(modifier = Modifier.height(Dimens.dp24))

                        CommonInputTextField(
                            value = name,
                            onValueChange = { name = it },
                            label = stringResource(R.string.name_dialog_name_label)
                        )

                        if (isAnonymous) {
                            Spacer(modifier = Modifier.height(Dimens.dp16))
                            CommonInputTextField(
                                value = company,
                                onValueChange = { company = it },
                                label = stringResource(R.string.name_dialog_company_label)
                            )
                        }

                        Spacer(modifier = Modifier.height(Dimens.dp32))

                        PrimaryButton(
                            text = stringResource(R.string.name_dialog_button),
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
    }
}
