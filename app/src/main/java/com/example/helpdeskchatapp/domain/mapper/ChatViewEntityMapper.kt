package com.example.helpdeskchatapp.domain.mapper

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import com.example.helpdeskchatapp.domain.model.producer.ChatViewEntity
import com.example.helpdeskchatapp.ui.model.ListRowEntity

suspend fun List<ChatViewEntity>.withBadges(
    activeConversationId: String?,
    getLastRead: suspend (String) -> Long?
): List<ListRowEntity> = map { entity ->
    val lastRead = getLastRead(entity.id)
    entity.toListRowEntity(entity.hasUnreadBadge(activeConversationId, lastRead))
}

fun ChatViewEntity.hasUnreadBadge(activeConversationId: String?, lastRead: Long?): Boolean =
    id != activeConversationId &&
            lastMessageTimestamp != null &&
            (lastRead == null || lastMessageTimestamp > lastRead)

private fun ChatViewEntity.toListRowEntity(hasUnreadMessage: Boolean = false): ListRowEntity {
    val lastMessage = message.take(40).let { if (message.length > 40) "$it…" else it }
    return ListRowEntity(
        id = id,
        title = sender,
        secondSubtitle = company.takeIf { it.isNotBlank() },
        thirdSubtitle = lastMessage,
        rightIcon = Icons.AutoMirrored.Filled.KeyboardArrowRight,
        showBadge = hasUnreadMessage
    )
}
