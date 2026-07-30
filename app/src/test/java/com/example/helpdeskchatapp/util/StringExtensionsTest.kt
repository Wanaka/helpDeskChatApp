package haag.your.next.developer.util

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class StringExtensionsTest {

    @Test
    fun toInitialsSingleWordReturnsFirstLetterUppercase() {
        assertEquals("A", "alice".toInitials())
    }

    @Test
    fun toInitialsTwoWordsReturnsBothFirstLettersUppercase() {
        assertEquals("AB", "alice bob".toInitials())
    }

    @Test
    fun toInitialsMoreThantTwoWordsReturnsOnlyFirstTwoInitials() {
        assertEquals("AB", "alice bob charlie".toInitials())
    }

    @Test
    fun toInitialsAlreadyUppercaseReturnsSameInitials() {
        assertEquals("AB", "Alice Bob".toInitials())
    }

    @Test
    fun toInitialsMixedCaseUppercasesFirstLetter() {
        assertEquals("AB", "alice Bob".toInitials())
    }

    @Test
    fun toInitialsEmptyStringReturnsNull() {
        assertNull("".toInitials())
    }

    @Test
    fun toInitialsBlankStringReturnsNull() {
        assertNull("   ".toInitials())
    }

    @Test
    fun toInitialsMultipleSpacesBetweenWordsFiltersBlankTokens() {
        assertEquals("AB", "alice  bob".toInitials())
    }
}
