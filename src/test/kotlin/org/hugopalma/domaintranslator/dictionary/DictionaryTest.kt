package org.hugopalma.domaintranslator.dictionary

import io.ktor.util.*
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class DictionaryTest {

    @Test
    fun `exact word is translated`() {
        val dictionary = Dictionary(mapOf(Pair("somekey", "translation")), 0)

        assertEquals("Translation", dictionary.translate("Somekey"))
    }

    @Test
    fun `a word in the middle of the text is translated`() {
        val dictionary = Dictionary(mapOf(Pair("somekey", "translation")), 0)

        assertEquals("settranslationafter", dictionary.translate("setSomekeyAfter")?.toLowerCasePreservingASCIIRules())
    }

    @Test
    fun `camelcase is preserved`() {
        val dictionary = Dictionary(mapOf(Pair("somekey", "my translation")), 0)

        assertEquals("setMyTranslationAfter", dictionary.translate("setSomekeyAfter"))
        assertEquals("MyTranslation", dictionary.translate("Somekey"))
    }

    @Test
    fun `camelcase composition`() {
        val dictionary = Dictionary(mapOf(Pair("somekey", "sometranslation"), Pair("otherkey", "othertranslation")), 0)

        assertEquals("setSometranslationOthertranslationAfter", dictionary.translate("setSomekeyOtherkeyAfter"))
    }

    @Test
    fun `snakecase is preserved`() {
        val dictionary = Dictionary(mapOf(Pair("somekey", "my translation")), 0)

        assertEquals("FIRST_MY_TRANSLATION_AFTER", dictionary.translate("FIRST_SOMEKEY_AFTER"))
    }

    @Test
    fun `if translation is same as original`() {
        val dictionary = Dictionary(mapOf(Pair("word", "word")), 0)

        assertNull(dictionary.translate("word"))
    }

    @Test
    fun `german umlauts`() {
        val dictionary = Dictionary(mapOf(Pair("stückzahl", "quantity"), Pair("straße", "road"), Pair("anlagedauer", "investment period")), 0)

        assertEquals("Quantity", dictionary.translate("Stueckzahl"))
        assertEquals("quantity", dictionary.translate("stückzahl"))
        assertEquals("road", dictionary.translate("strasse"))
        assertEquals("investmentPeriod", dictionary.translate("anlagedauer"))
    }
}
