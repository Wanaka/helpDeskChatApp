package haag.your.next.developer.domain.model.consumer

data class Message(
    val message: String?,
    val conversationId: String = "",
    val senderId: String = ""
)
