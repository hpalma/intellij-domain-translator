package org.hugopalma.domaintranslator.editor

import com.intellij.codeInsight.hints.*
import com.intellij.codeInsight.hints.presentation.BasePresentation
import com.intellij.codeInsight.hints.presentation.PresentationFactory
import com.intellij.lang.java.JavaLanguage
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.editor.DefaultLanguageHighlighterColors
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.openapi.editor.markup.TextAttributes
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.util.startOffset
import org.hugopalma.domaintranslator.settings.Settings
import java.awt.Graphics2D
import javax.swing.JPanel


@Suppress("UnstableApiUsage")
class HintProvider : InlayHintsProvider<NoSettings> {

    val MY_KEYWORD: TextAttributesKey = TextAttributesKey.createTextAttributesKey("MY_KEYWORD", DefaultLanguageHighlighterColors.KEYWORD)

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
            val translation = findTranslation(element) ?: return true

            val presentation = CustomTextInlayPresentation(translation, editor, element)

            sink.addBlockElement(
                element.startOffset,
                relatesToPrecedingText = false,
                showAbove = true,
                priority = -1,
                presentation = presentation
            )
            return true
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

private class CustomTextInlayPresentation(private val text: String, private val editor: Editor, private val element: PsiElement) : BasePresentation() {
    override val width: Int = editor.component.getFontMetrics(editor.component.font).stringWidth(text)
    override val height: Int = editor.component.getFontMetrics(editor.component.font).height - 5

    override fun paint(g: Graphics2D, attributes: TextAttributes) {
        val indent = editor.offsetToPoint2D(element.startOffset).x.toInt()
        val presentation = PresentationFactory(editor).inset(PresentationFactory(editor).text(text), indent)

        presentation.paint(g, editor.colorsScheme.getAttributes(DefaultLanguageHighlighterColors.INLAY_TEXT_WITHOUT_BACKGROUND))
    }

    override fun toString(): String = text
}
