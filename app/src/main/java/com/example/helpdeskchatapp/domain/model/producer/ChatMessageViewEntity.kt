package haag.your.next.developer.domain.model.producer

data class ChatMessageViewEntity(
    val id: String,
    val text: String,
    val senderId: String,
    val timestamp: Long
)
