package haag.your.next.developer.data.model

data class ChatResponse(
    val id: String,
    val sender: String,
    val message: String,
    val adminName: String,
    val company: String,
    val userId: String,
    val lastMessageTimestamp: Long? = null
)
