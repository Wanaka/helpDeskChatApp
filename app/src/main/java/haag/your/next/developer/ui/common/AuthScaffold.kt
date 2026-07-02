package haag.your.next.developer.ui.common

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import haag.your.next.developer.theme.Dimens
import haag.your.next.developer.ui.common.components.AvatarImage
import haag.your.next.developer.ui.common.components.CommonTextLabel
import haag.your.next.developer.ui.common.components.TextLabelStyle

@Composable
fun AuthScaffold(
    heroTitle: String,
    content: @Composable () -> Unit
) {
    Scaffold(
        containerColor = MaterialTheme.colorScheme.primary
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = paddingValues.calculateTopPadding())
                .statusBarsPadding(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(Dimens.dp24))

            AvatarImage(size = Dimens.dp100, showBorder = true)

            Spacer(modifier = Modifier.height(Dimens.dp12))

            CommonTextLabel(text = heroTitle, style = TextLabelStyle.HeroTitle)

            Spacer(modifier = Modifier.height(Dimens.dp24))

            Surface(
                modifier = Modifier.fillMaxSize(),
                shape = RoundedCornerShape(topStart = Dimens.dp25, topEnd = Dimens.dp25),
                color = MaterialTheme.colorScheme.background,
                border = BorderStroke(Dimens.dp1, MaterialTheme.colorScheme.onPrimary)
            ) {
                content()
            }
        }
    }
}
