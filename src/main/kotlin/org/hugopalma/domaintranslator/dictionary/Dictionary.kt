package org.hugopalma.domaintranslator.dictionary

import ai.grazie.utils.isUppercase
import io.ktor.util.*

class Dictionary(values: Map<String, String>) {

    private val dictionary: Map<String, String> = values.toMap()

    val timestamp = System.currentTimeMillis()

    fun translate(text: String): String? {
        val lowercaseToTranslate = text.toLowerCasePreservingASCIIRules()

        // is exact text in dictionary?
        if (dictionary.containsKey(lowercaseToTranslate)) {
            return formatTranslation(text, dictionary[lowercaseToTranslate]!!)
        }

        // check if the text contains words in the dictionary
        dictionary.forEach {
            if (lowercaseToTranslate.contains(it.key)) {
                val startIndex = lowercaseToTranslate.indexOf(it.key)

                val toTranslate = text.substring(startIndex, startIndex + it.key.length)
                val translated = formatTranslation(toTranslate, dictionary[it.key]!!)

                if (translated != null) {
                    return text.replace(it.key, translated, true)
                }
            }
        }

        return null
    }

    fun contains(text: String): Boolean {
        return dictionary.containsKey(text.toLowerCasePreservingASCIIRules())
    }

    private fun formatTranslation(original: String, translation: String): String? {
        if (original.equals(translation, ignoreCase = true)) {
            return null;
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
}
