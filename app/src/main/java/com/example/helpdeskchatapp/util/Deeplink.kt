package haag.your.next.developer.util

object Deeplink {
    private const val CHAT_DEEPLINK = "haag.your.next.developer://chat/"
    
    fun getChatDeeplink(adminId: String): String {
        return "$CHAT_DEEPLINK$adminId"
    }
}
