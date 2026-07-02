package haag.your.next.developer.ui.common.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import haag.your.next.developer.theme.Dimens
import haag.your.next.developer.ui.model.ListRowEntity
import haag.your.next.developer.util.toInitials

@Composable
fun StandardListRow(
    entity: ListRowEntity,
    onClick: () -> Unit = {}
) = with(entity) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(Dimens.dp16),
        verticalAlignment = Alignment.CenterVertically
    ) {
        title.toInitials()?.let {
            InitialsAvatar(initials = it)
            Spacer(modifier = Modifier.width(Dimens.dp16))
        }

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium
            )
            secondSubtitle?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.outline,
                    maxLines = 1
                )
            }
            thirdSubtitle?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Black,
                    maxLines = 1
                )
            }
        }

        if (showBadge) UnreadBadge()

        rightIcon?.let {
            Icon(
                imageVector = it,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.outline
            )
        }
    }
}
