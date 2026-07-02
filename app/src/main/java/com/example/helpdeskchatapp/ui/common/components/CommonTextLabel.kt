package haag.your.next.developer.ui.common.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight

enum class TextLabelStyle { Label, Title, TitleSmall, Subtitle, HeroTitle }

@Composable
fun CommonTextLabel(text: String, style: TextLabelStyle = TextLabelStyle.Label) {
    when (style) {
        TextLabelStyle.Label -> Text(
            text = text,
            style = MaterialTheme.typography.labelLarge
        )
        TextLabelStyle.Title -> Text(
            text = text,
            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.fillMaxWidth()
        )
        TextLabelStyle.TitleSmall -> Text(
            text = text,
            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.fillMaxWidth()
        )
        TextLabelStyle.Subtitle -> Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.fillMaxWidth()
        )
        TextLabelStyle.HeroTitle -> Text(
            text = text,
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onPrimary
        )
    }
}
