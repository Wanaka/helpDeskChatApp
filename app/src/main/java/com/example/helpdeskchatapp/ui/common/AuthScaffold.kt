package com.example.helpdeskchatapp.ui.common

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import com.example.helpdeskchatapp.R
import com.example.helpdeskchatapp.theme.Dimens
import com.example.helpdeskchatapp.ui.common.components.CommonTextLabel
import com.example.helpdeskchatapp.ui.common.components.TextLabelStyle

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

            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(Dimens.dp100)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.onPrimary)
                    .border(BorderStroke(Dimens.dp4, MaterialTheme.colorScheme.onPrimary), CircleShape)
            ) {
                Image(
                    painter = painterResource(R.drawable.avatar_qr),
                    contentDescription = null,
                    modifier = Modifier
                        .size(Dimens.dp100)
                        .clip(CircleShape)
                )
            }

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
