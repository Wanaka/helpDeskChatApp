package com.example.helpdeskchatapp.ui.common.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.helpdeskchatapp.ui.model.ListRowEntity

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState

@Composable
fun CommonLazyColumn(
    items: List<ListRowEntity>,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(0.dp),
    showDividers: Boolean = true,
    verticalArrangement: Arrangement.Vertical = Arrangement.Top,
    state: LazyListState = rememberLazyListState()
) {
    LazyColumn(
        modifier = modifier,
        contentPadding = contentPadding,
        verticalArrangement = verticalArrangement,
        state = state
    ) {
        items(items) { item ->
            if (item.isChatLayout) {
                ChatBubbleRow(item)
            } else {
                StandardListRow(item)
            }
            if (showDividers && !item.isChatLayout) {
                HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
            }
        }
    }
}