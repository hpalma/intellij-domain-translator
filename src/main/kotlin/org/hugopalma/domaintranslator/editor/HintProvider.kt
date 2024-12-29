package org.hugopalma.domaintranslator.editor

import com.intellij.codeInsight.hints.*
import com.intellij.codeInsight.hints.presentation.InlayTextMetricsStorage
import com.intellij.lang.java.JavaLanguage
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.editor.Editor
import com.intellij.psi.*
import com.intellij.psi.util.startOffset
import org.hugopalma.domaintranslator.dictionary.DictionaryService
import org.hugopalma.domaintranslator.settings.Settings
import javax.swing.JPanel

@Suppress("UnstableApiUsage")
class HintProvider : InlayHintsProvider<NoSettings> {

    override fun getCollectorFor(file: PsiFile, editor: Editor, settings: NoSettings, sink: InlayHintsSink): InlayHintsCollector? {
        val settingsState = ApplicationManager.getApplication().getService(Settings::class.java).state

        return when {
            !settingsState.showInlays -> null
            acceptsFile(file) -> Collector(editor)
            else -> null
        }
    }

    private class Collector(editor: Editor) : FactoryInlayHintsCollector(editor) {
        override fun collect(element: PsiElement, editor: Editor, sink: InlayHintsSink): Boolean {
            val translation = acceptsElement(element) ?: return true

            val presentation = CustomTextInlayPresentation(InlayTextMetricsStorage(editor), translation, editor, element)
            sink.addBlockElement(element.startOffset, false, true, -1, presentation)
            return true
        }

        private fun acceptsElement(element: PsiElement): String? {
            val validType = element is PsiIdentifier &&
                    element.parent.parent !is PsiImportStatement &&
                    element.parent.parent.parent !is PsiImportStatement &&
                    element.parent.parent !is PsiPackageStatement

            if (!validType) {
                return null
            }

            val psiFile = element.containingFile
            val project = psiFile.project
            val dictionary = project.getService(DictionaryService::class.java).getDictionary(element) ?: return null
            val translation = dictionary.translate(element.text)

            return translation
        }
    }

    private fun acceptsFile(file: PsiFile): Boolean = file.language == JavaLanguage.INSTANCE

    override fun createSettings() = NoSettings()

    override fun createConfigurable(settings: NoSettings): ImmediateConfigurable {
        return object : ImmediateConfigurable {
            override fun createComponent(listener: ChangeListener) = JPanel()
        }
    }

    override val key: SettingsKey<NoSettings> = SettingsKey("DomainTranslatorInlayProviderSettingsKey")
    override val name: String = "Domain Translator"
    override val previewText: String? = null
}
