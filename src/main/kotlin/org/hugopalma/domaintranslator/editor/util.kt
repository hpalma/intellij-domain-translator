package org.hugopalma.domaintranslator.editor

import com.intellij.lang.java.JavaLanguage
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiIdentifier
import com.intellij.psi.PsiImportStatement
import com.intellij.psi.PsiPackageStatement
import org.hugopalma.domaintranslator.dictionary.DictionaryService

fun findTranslation(element: PsiElement?): String? {
    return if (
        element != null &&
        element.language.`is`(JavaLanguage.INSTANCE) &&
        element is PsiIdentifier &&
        element.parent.parent !is PsiImportStatement &&
        element.parent.parent.parent !is PsiImportStatement &&
        element.parent.parent !is PsiPackageStatement
    ) {
        val psiFile = element.containingFile
        val project = psiFile.project
        val dictionary = project.getService(DictionaryService::class.java).getDictionary(element) ?: return null
        val translation = dictionary.translate(element.text)

        translation
    } else {
        null
    }
}
