package org.hugopalma.domaintranslator.editor

import com.intellij.ide.plugins.PluginManagerCore
import com.intellij.lang.Language
import com.intellij.lang.ecmascript6.psi.impl.ES6ClassImpl
import com.intellij.lang.java.JavaLanguage
import com.intellij.lang.javascript.JavascriptLanguage
import com.intellij.lang.javascript.dialects.ECMA6LanguageDialect
import com.intellij.lang.javascript.dialects.TypeScriptLanguageDialect
import com.intellij.lang.javascript.psi.impl.JSFunctionImpl
import com.intellij.lang.javascript.psi.impl.JSReferenceExpressionImpl
import com.intellij.openapi.extensions.PluginId
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiIdentifier
import com.intellij.psi.PsiImportStatement
import com.intellij.psi.PsiPackageStatement
import com.intellij.psi.impl.source.tree.LeafPsiElement
import com.intellij.psi.impl.source.tree.PsiWhiteSpaceImpl
import org.hugopalma.domaintranslator.dictionary.DictionaryService
import org.jetbrains.kotlin.idea.KotlinLanguage
import org.jetbrains.kotlin.psi.*

private val kotlinPluginId = PluginId.getId("org.jetbrains.kotlin")
private val javascriptPluginId = PluginId.getId("JavaScript")

private val SUPPORTED_LANGUAGES: Collection<Language?> = listOf(
    JavaLanguage.INSTANCE,
    if (isKotlinEnabled()) KotlinLanguage.INSTANCE else null,
    if (isJavascriptEnabled()) JavascriptLanguage.INSTANCE else null,
    if (isJavascriptEnabled()) TypeScriptLanguageDialect.getInstance() else null,
    if (isJavascriptEnabled()) ECMA6LanguageDialect.getInstance() else null
)

fun isSupported(language: Language): Boolean {
    return SUPPORTED_LANGUAGES.any({ it?.`is`(language) ?: false })
}

fun isSupportedElement(element: PsiElement?): Boolean {
    return isSupportedJavaElement(element) || isSupportedJavascriptElement(element) || isSupportedKotlinElement(element)
}

private fun isSupportedKotlinElement(element: PsiElement?): Boolean {
    if (!isKotlinEnabled()) return false

    return element != null &&
            (element is LeafPsiElement ||
                    element is KtTypeReference ||
                    element is KtSuperTypeCallEntry) &&
            (element.parent is KtProperty ||
                    element.parent is KtClass ||
                    element.parent is KtNamedFunction ||
                    element.parent is KtTypeReference ||
                    element.parent is KtSuperTypeList)
}

private fun isKotlinEnabled(): Boolean {
    val plugin = PluginManagerCore.getPlugin(kotlinPluginId)
    return plugin != null && plugin.isEnabled
}

private fun isSupportedJavascriptElement(element: PsiElement?): Boolean {
    if (!isJavascriptEnabled()) return false

    return element != null &&
            element is LeafPsiElement &&
            element !is PsiWhiteSpaceImpl &&
            (element.parent is ES6ClassImpl ||
                    element.parent is JSFunctionImpl<*> ||
                    element.parent is JSReferenceExpressionImpl
                    )
}

private fun isJavascriptEnabled(): Boolean {
    val plugin = PluginManagerCore.getPlugin(javascriptPluginId)
    return plugin != null && plugin.isEnabled
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

