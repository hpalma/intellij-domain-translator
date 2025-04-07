package org.hugopalma.domaintranslator.dictionary

import ai.grazie.utils.isUppercase
import io.ktor.util.*

typealias KeyExpander = (String) -> String

class Dictionary(values: Map<String, String>, val timestamp: Long) {

    private val dictionary: Map<String, String> = values.toMap()

    fun translate(text: String): String? {
        val translatableKeys = translatableKeys(text)

        for (translatableKey in translatableKeys) {
            // is exact text in dictionary?
            if (dictionary.containsKey(translatableKey)) {
                return formatTranslation(text, dictionary[translatableKey]!!)
            }

            // check if the text contains words in the dictionary
            dictionary.forEach {
                if (translatableKey.contains(it.key)) {
                    val startIndex = translatableKey.indexOf(it.key)

                    val toTranslate = text.substring(startIndex, startIndex + it.key.length)
                    val translated = formatTranslation(toTranslate, dictionary[it.key]!!)

                    if (translated != null) {
                        return text.replace(it.key, translated, true)
                    }
                }
            }
        }

        return null
    }

    private fun translatableKeys(text: String): Collection<String> {
        val lowercaseText = text.toLowerCasePreservingASCIIRules()

        val expanders = listOf(germanUmlauts)

        return applyExpanders(lowercaseText, expanders) + text
    }

    private fun applyExpanders(input: String, keyExpanders: List<KeyExpander>): List<String> {
        return keyExpanders.map { expander ->
            expander(input)
        }
    }

    private fun formatTranslation(original: String, translation: String): String? {
        if (original.equals(translation, ignoreCase = true)) {
            return null
        }

        if (original.isUppercase()) {
            return translation.uppercase().replace(" ", "_")
        }

        return if (original.first().isUpperCase()) {
            toCamelCase(translation)
        } else {
            toCamelCase(translation, true)
        }
    }

    private fun toCamelCase(sentence: String, lowercase: Boolean = false): String {
        val transformed = sentence.split(" ") // Split the sentence by spaces
            .joinToString("") { word ->
                word.replaceFirstChar {
                    if (it.isLowerCase()) it.titlecase() else it.toString()
                }
            }

        if (lowercase) {
            return transformed.replaceFirstChar {
                it.lowercase()
            }
        }

        return transformed
    }

    private val germanUmlauts: KeyExpander = { s ->
        s.replace("ae", "ä").replace("oe", "ö").replace("ue", "ü").replace("ss", "ß")
    }
}
