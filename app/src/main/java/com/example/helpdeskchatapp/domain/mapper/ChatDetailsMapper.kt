package haag.your.next.developer.domain.mapper

import haag.your.next.developer.domain.model.producer.ChatMessageViewEntity
import haag.your.next.developer.ui.model.ListRowEntity

fun ChatMessageViewEntity.toListRowEntity(currentUserId: String): ListRowEntity {
    val isFromMe = senderId == currentUserId

    return ListRowEntity(
        id = id,
        title = text,
        isChatLayout = true,
        isFromMe = isFromMe,
    )
}
