package haag.your.next.developer.util

object Deeplink {
    private const val CHAT_DEEPLINK = "https://helpdeskchatapp.web.app/?adminId="
    
    fun getChatDeeplink(adminId: String): String {
        return "$CHAT_DEEPLINK$adminId"
    }
}
