package haag.your.next.developer.data.model

data class ChatMessageResponse(
    val id: String,
    val text: String,
    val senderId: String,
    val timestamp: Long
)