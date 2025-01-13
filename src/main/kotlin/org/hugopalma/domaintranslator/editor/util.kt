package org.hugopalma.domaintranslator.editor

import com.intellij.lang.Language
import com.intellij.lang.ecmascript6.psi.impl.ES6ClassImpl
import com.intellij.lang.java.JavaLanguage
import com.intellij.lang.javascript.JavascriptLanguage
import com.intellij.lang.javascript.dialects.ECMA6LanguageDialect
import com.intellij.lang.javascript.dialects.TypeScriptLanguageDialect
import com.intellij.lang.javascript.psi.impl.JSFunctionImpl
import com.intellij.lang.javascript.psi.impl.JSReferenceExpressionImpl
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiIdentifier
import com.intellij.psi.PsiImportStatement
import com.intellij.psi.PsiPackageStatement
import com.intellij.psi.impl.source.tree.LeafPsiElement
import com.intellij.psi.impl.source.tree.PsiWhiteSpaceImpl
import org.hugopalma.domaintranslator.dictionary.DictionaryService

private val SUPPORTED_LANGUAGES: Collection<Language> = listOf(
    JavaLanguage.INSTANCE,
    JavascriptLanguage.INSTANCE,
    TypeScriptLanguageDialect.getInstance(),
    ECMA6LanguageDialect.getInstance()
)

fun isSupported(language: Language): Boolean {
    return SUPPORTED_LANGUAGES.any({ it.`is`(language) })
}

fun isSupportedElement(element: PsiElement?): Boolean {
    return isSupportedJavaElement(element) || isSupportedJavascriptElement(element)
}

private fun isSupportedJavascriptElement(element: PsiElement?): Boolean {
    return element != null &&
            element is LeafPsiElement &&
            element !is PsiWhiteSpaceImpl &&
            (element.parent is ES6ClassImpl ||
                    element.parent is JSFunctionImpl<*> ||
                    element.parent is JSReferenceExpressionImpl
                    )
}

private fun isSupportedJavaElement(element: PsiElement?): Boolean {
    return element != null &&
            element is PsiIdentifier &&
            element.parent.parent !is PsiImportStatement &&
            element.parent.parent.parent !is PsiImportStatement &&
            element.parent.parent !is PsiPackageStatement
}

fun findTranslation(element: PsiElement): String? {
    val psiFile = element.containingFile
    val project = psiFile.project
    val dictionary = project.getService(DictionaryService::class.java).getDictionary(element) ?: return null
    val translation = dictionary.translate(element.text)

    return translation
}

