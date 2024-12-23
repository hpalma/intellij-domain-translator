package org.hugopalma.domaintranslator.dictionary

import io.ktor.util.*
import org.junit.Assert.assertEquals
import org.junit.Test

class DictionaryTest {

    @Test
    fun `exact word is translated`() {
        val dictionary = Dictionary(mapOf(Pair("somekey", "translation")))

        assertEquals("Translation", dictionary.translate("SomeKey"))
    }

    @Test
    fun `a word in the middle of the text is translated`() {
        val dictionary = Dictionary(mapOf(Pair("somekey", "translation")))

        assertEquals("settranslationafter", dictionary.translate("setSomeKeyAfter")?.toLowerCasePreservingASCIIRules())
    }

    @Test
    fun `camelcase is preserved`() {
        val dictionary = Dictionary(mapOf(Pair("somekey", "my translation")))

        assertEquals("setMyTranslationAfter", dictionary.translate("setSomeKeyAfter"))
        assertEquals("setmyTranslationAfter", dictionary.translate("setsomeKeyAfter"))
        assertEquals("MyTranslation", dictionary.translate("Somekey"))
    }

    @Test
    fun `snakecase is preserved`() {
        val dictionary = Dictionary(mapOf(Pair("somekey", "my translation")))

        assertEquals("FIRST_MY_TRANSLATION_AFTER", dictionary.translate("FIRST_SOMEKEY_AFTER"))
    }
}
