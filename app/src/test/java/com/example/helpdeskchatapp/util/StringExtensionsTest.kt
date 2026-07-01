package com.example.helpdeskchatapp.util

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class StringExtensionsTest {

    @Test
    fun `toInitials_singleWord_returnsFirstLetterUppercase`() {
        assertEquals("A", "alice".toInitials())
    }

    @Test
    fun `toInitials_twoWords_returnsBothFirstLettersUppercase`() {
        assertEquals("AB", "alice bob".toInitials())
    }

    @Test
    fun `toInitials_moreThantTwoWords_returnsOnlyFirstTwoInitials`() {
        assertEquals("AB", "alice bob charlie".toInitials())
    }

    @Test
    fun `toInitials_alreadyUppercase_returnsSameInitials`() {
        assertEquals("AB", "Alice Bob".toInitials())
    }

    @Test
    fun `toInitials_mixedCase_uppercasesFirstLetter`() {
        assertEquals("AB", "alice Bob".toInitials())
    }

    @Test
    fun `toInitials_emptyString_returnsNull`() {
        assertNull("".toInitials())
    }

    @Test
    fun `toInitials_blankString_returnsNull`() {
        assertNull("   ".toInitials())
    }

    @Test
    fun `toInitials_multipleSpacesBetweenWords_filtersBlankTokens`() {
        assertEquals("AB", "alice  bob".toInitials())
    }
}
