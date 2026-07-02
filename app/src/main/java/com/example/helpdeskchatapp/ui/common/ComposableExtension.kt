package haag.your.next.developer.ui.common

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import kotlinx.coroutines.delay

@Composable
fun composeContext() = LocalContext.current

@Composable
fun LazyListState.ScrollToBottomOnChange(itemCount: Int) {
    val imeVisible = WindowInsets.ime.getBottom(LocalDensity.current) > 0
    LaunchedEffect(itemCount, imeVisible) {
        if (itemCount > 0) {
            if (imeVisible) delay(300)
            animateScrollToItem(itemCount - 1)
        }
    }
}