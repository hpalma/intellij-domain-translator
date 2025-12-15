package org.hugopalma.domaintranslator.dictionary

import io.ktor.util.*

typealias KeyExpander = (String) -> String

class Dictionary(values: Map<String, String>, val timestamp: Long) {

    private val dictionary: Map<String, String> = values.toMap()

    fun translate(text: String): String? {
        val translatableKeys = decomposeKeys(text)
        var translatedText = text

        for (translatableKey in translatableKeys) {
            val translation = findTranslation(translatableKey)

            // check which key is in dictionary if any
            if (translation != null) {
                val formatedTranslation = formatTranslation(translatableKey, translation)
                if (formatedTranslation != null) {
                    translatedText = replaceWithTranslation(translatedText, translatableKey, formatedTranslation)
                }
            }
        }

        return if (translatedText == text) {
            null
        } else {
            translatedText
        }
    }

    private fun replaceWithTranslation(text: String, translatableKey: String, translation: String): String {
        var translatedText = text

        // exact match of key in text?
        if (translatedText.contains(translatableKey)) {
            translatedText = translatedText.replace(translatableKey, translation)
        } else {
            // look for key without whitespaces (support for multiple words in camelcase)
            translatedText = translatedText.replace(translatableKey.replace(" ", ""), translation)

            // look for key replacing whitespaces with underscore(support for multiple words in uppercase)
            translatedText = translatedText.replace(translatableKey.replace(" ", "_"), translation)
        }

        return translatedText
    }

    private fun findTranslation(text: String): String? {
        val lowercaseKey = text.toLowerCasePreservingASCIIRules()
        val keyWithGermanUmlauts = germanUmlauts.invoke(lowercaseKey)

        return dictionary[lowercaseKey] ?: dictionary[keyWithGermanUmlauts]
    }

    private fun decomposeKeys(text: String): List<String> {
        val words = text.split(Regex("_|(?<=[a-z])(?=[A-Z])|(?<=[A-Z])(?=[A-Z][a-z])"))
            .filter { it.isNotEmpty() }

        return (words.generateMultiWordCombinations() + words).sortedByDescending { it.length }
    }

    private fun List<String>.generateMultiWordCombinations(): List<String> {
        return this.indices.flatMap { i ->
            (i + 1 until this.size).map { j ->
                this.subList(i, j + 1).joinToString(" ")
            }
        }
    }

    private fun formatTranslation(original: String, translation: String): String? {
        if (original.equals(translation, ignoreCase = true)) {
            return null
        }

        if (original.replace(" ", "").filter { it.isLetter() }.all { it.isUpperCase() }) {
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
