package org.hugopalma.domaintranslator.editor

import com.google.common.cache.Cache
import com.google.common.cache.CacheBuilder
import com.intellij.ide.highlighter.JavaFileType
import com.intellij.ide.plugins.PluginManagerCore
import com.intellij.lang.ecmascript6.psi.impl.ES6ClassImpl
import com.intellij.lang.javascript.JavaScriptFileType
import com.intellij.lang.javascript.TypeScriptFileType
import com.intellij.lang.javascript.psi.impl.JSFunctionImpl
import com.intellij.lang.javascript.psi.impl.JSReferenceExpressionImpl
import com.intellij.lang.javascript.psi.impl.JSVariableImpl
import com.intellij.openapi.fileTypes.FileType
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiIdentifier
import com.intellij.psi.PsiImportStatement
import com.intellij.psi.PsiPackageStatement
import com.intellij.psi.impl.source.tree.LeafPsiElement
import com.intellij.psi.impl.source.tree.PsiWhiteSpaceImpl
import org.hugopalma.domaintranslator.dictionary.DictionaryService
import org.jetbrains.kotlin.idea.KotlinFileType
import org.jetbrains.kotlin.psi.*
import org.jetbrains.vuejs.lang.html.VueFileType
import java.util.concurrent.TimeUnit

private val SUPPORTED_LANGUAGES: Collection<*> = listOf(
    JavaFileType::class,
    if (isKotlinEnabled()) KotlinFileType::class else null,
    if (isJavascriptEnabled()) JavaScriptFileType::class else null,
    if (isJavascriptEnabled()) TypeScriptFileType::class else null,
    if (isVueEnabled()) VueFileType::class else null,
)

private var enabledPluginsCache: Cache<String, Boolean>? = null

private fun getOrCreateCache(): Cache<String, Boolean> {
    return enabledPluginsCache ?: enabledPluginsCache ?: CacheBuilder.newBuilder()
        .expireAfterWrite(1, TimeUnit.HOURS)
        .build<String, Boolean>()
        .also { enabledPluginsCache = it }
}

fun isSupported(languageFileType: FileType): Boolean {
    return SUPPORTED_LANGUAGES.any { it == languageFileType::class }
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
    return getOrCreateCache().getIfPresent("org.jetbrains.kotlin") ?: run {
        val enabled = PluginManagerCore.loadedPlugins.find { it.pluginId.idString == "org.jetbrains.kotlin" } != null
        getOrCreateCache().put("org.jetbrains.kotlin", enabled)

        return enabled
    }
}

private fun isVueEnabled(): Boolean {
    return getOrCreateCache().getIfPresent("org.jetbrains.plugins.vue") ?: run {
        val enabled = PluginManagerCore.loadedPlugins.find { it.pluginId.idString == "org.jetbrains.plugins.vue" } != null
        getOrCreateCache().put("org.jetbrains.plugins.vue", enabled)

        return enabled
    }
}

private fun isSupportedJavascriptElement(element: PsiElement?): Boolean {
    if (!isJavascriptEnabled()) return false

    return element != null &&
            element is LeafPsiElement &&
            element !is PsiWhiteSpaceImpl &&
            (element.parent is ES6ClassImpl ||
                    element.parent is JSFunctionImpl<*> ||
                    element.parent is JSReferenceExpressionImpl ||
                    element.parent is JSVariableImpl<*, *>
                    )
}

private fun isJavascriptEnabled(): Boolean {
    return getOrCreateCache().getIfPresent("JavaScript") ?: run {
        val enabled = PluginManagerCore.loadedPlugins.find { it.pluginId.idString == "JavaScript" } != null
        getOrCreateCache().put("JavaScript", enabled)

        return enabled
    }
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
