package haag.your.next.developer.data.mapper

import haag.your.next.developer.data.model.ChatMessageResponse
import haag.your.next.developer.domain.model.producer.ChatMessageViewEntity

fun ChatMessageResponse.toDomain(): ChatMessageViewEntity {
    return ChatMessageViewEntity(
        id = id,
        text = text,
        senderId = senderId,
        timestamp = timestamp,
    )
}
