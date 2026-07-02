package haag.your.next.developer.domain.model.producer

data class ChatViewEntity(
    val id: String,
    val sender: String,
    val company: String,
    val message: String,
    val adminName: String,
    val userId: String,
    val lastMessageTimestamp: Long? = null
)
