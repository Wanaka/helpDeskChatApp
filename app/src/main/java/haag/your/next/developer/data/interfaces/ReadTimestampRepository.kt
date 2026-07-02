package haag.your.next.developer.data.interfaces

interface ReadTimestampRepository {
    fun getLastRead(conversationId: String): Long?
    fun saveLastRead(conversationId: String)
}
