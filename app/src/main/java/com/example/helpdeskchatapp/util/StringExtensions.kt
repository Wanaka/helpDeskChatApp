package haag.your.next.developer.util

fun String.toInitials(): String? = split(" ")
    .filter { it.isNotBlank() }
    .take(2)
    .joinToString("") { it.first().uppercaseChar().toString() }
    .ifEmpty { null }
