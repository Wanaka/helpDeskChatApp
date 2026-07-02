package haag.your.next.developer.domain.model.consumer

data class CreateChat(
    val adminId: String,
    val userId: String,
    val senderName: String
)
