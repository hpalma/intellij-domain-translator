package org.hugopalma.domaintranslator.dictionary

import ai.grazie.utils.isUppercase
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
                    if (translatedText.contains(translatableKey)) {
                        translatedText = translatedText.replace(translatableKey, formatedTranslation)
                    } else {
                        translatedText = translatedText.replace(translatableKey.replace(" ", ""), formatedTranslation)
                        translatedText = translatedText.replace(translatableKey.replace(" ", "_"), formatedTranslation)
                    }
                }
            }
        }

        return if (translatedText.equals(text)) {
            null
        } else {
            translatedText
        }
    }

    private fun findTranslation(text: String): String? {
        val lowercaseKey = text.toLowerCasePreservingASCIIRules()
        val keyWithGermanUmlauts = germanUmlauts.invoke(lowercaseKey)

        val translation = dictionary[lowercaseKey]
        return if (translation == null) {
            dictionary[keyWithGermanUmlauts]
        } else {
            translation
        }
    }

    private fun decomposeKeys(text: String): List<String> {
        val words = text.split(Regex("_|(?<=[a-z])(?=[A-Z])|(?<=[A-Z])(?=[A-Z][a-z])"))
            .filter { it.isNotEmpty() }

        return (words.generateMultiWordCombinations() + words).sortedByDescending { it.length }
    }

    fun List<String>.generateMultiWordCombinations(): List<String> {
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

        if (original.replace(" ", "").isUppercase()) {
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
