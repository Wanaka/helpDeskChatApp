package haag.your.next.developer.ui.register

import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import haag.your.next.developer.R
import haag.your.next.developer.domain.model.consumer.Login
import haag.your.next.developer.domain.viewmodel.AuthViewModel
import haag.your.next.developer.theme.Dimens
import haag.your.next.developer.theme.MyApplicationTheme
import haag.your.next.developer.ui.common.AuthScaffold
import haag.your.next.developer.ui.common.components.AccountLinkRow
import haag.your.next.developer.ui.common.components.CommonInputTextField
import haag.your.next.developer.ui.common.components.CommonTextLabel
import haag.your.next.developer.ui.common.components.PrimaryButton
import haag.your.next.developer.ui.common.components.TextLabelStyle

@Composable
fun RegisterRoute(
    onNavigateToAdmin: () -> Unit,
    onNavigateToLogin: () -> Unit,
    viewModel: AuthViewModel = hiltViewModel()
) {

    LaunchedEffect(viewModel) {
        viewModel.navigateToAdmin.collect { onNavigateToAdmin() }
    }

    AuthScaffold(heroTitle = stringResource(R.string.login_hero_title)) {
        RegisterScreen(
            onRegister = viewModel::register,
            onNavigateToLogin = onNavigateToLogin
        )
    }
}

@Composable
fun RegisterScreen(
    onRegister: (Login) -> Unit,
    onNavigateToLogin: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .imePadding()
            .padding(horizontal = Dimens.dp24, vertical = Dimens.dp32),
        horizontalAlignment = Alignment.Start
    ) {
        CommonTextLabel(text = stringResource(R.string.register_title), style = TextLabelStyle.Title)

        Spacer(modifier = Modifier.height(Dimens.dp4))

        CommonTextLabel(text = stringResource(R.string.register_subtitle), style = TextLabelStyle.Subtitle)

        Spacer(modifier = Modifier.height(Dimens.dp32))

        CommonInputTextField(
            value = email,
            onValueChange = { email = it },
            label = stringResource(R.string.register_email_label),
        )

        Spacer(modifier = Modifier.height(Dimens.dp16))

        CommonInputTextField(
            value = password,
            onValueChange = { password = it },
            label = stringResource(R.string.register_password_label),
            isPasswordField = true
        )

        Spacer(modifier = Modifier.height(Dimens.dp40))

        PrimaryButton(
            text = stringResource(R.string.register_button),
            onClick = { onRegister(Login(email.trim(), password.trim())) }
        )

        Spacer(modifier = Modifier.height(Dimens.dp16))

        AccountLinkRow(
            label = stringResource(R.string.register_has_account),
            actionText = stringResource(R.string.register_login_action),
            onAction = onNavigateToLogin
        )
    }
}

@Preview(showBackground = true)
@Composable
fun RegisterScreenPreview() {
    MyApplicationTheme {
        RegisterScreen(
            onRegister = {},
            onNavigateToLogin = {}
        )
    }
}
