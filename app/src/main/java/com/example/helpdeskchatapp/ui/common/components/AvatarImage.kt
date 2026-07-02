package haag.your.next.developer.ui.common.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import haag.your.next.developer.R
import haag.your.next.developer.theme.Dimens

@Composable
fun AvatarImage(
    size: Dp = Dimens.dp100,
    showBorder: Boolean = false,
    modifier: Modifier = Modifier
) {
    val borderModifier = if (showBorder) {
        modifier
            .size(size)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.onPrimary)
            .border(BorderStroke(Dimens.dp4, MaterialTheme.colorScheme.onPrimary), CircleShape)
    } else {
        modifier
            .size(size)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.onPrimary)
    }

    Image(
        painter = painterResource(R.drawable.avatar_qr),
        contentDescription = null,
        modifier = borderModifier
    )
}
