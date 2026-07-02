package haag.your.next.developer.data.mapper

import haag.your.next.developer.data.model.ChatResponse
import haag.your.next.developer.domain.model.producer.ChatViewEntity

fun ChatResponse.toDomain(): ChatViewEntity {
    return ChatViewEntity(
        id = id,
        sender = sender,
        company = company,
        message = message,
        adminName = adminName,
        userId = userId,
        lastMessageTimestamp = lastMessageTimestamp
    )
}
