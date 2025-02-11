@file:Suppress("UnstableApiUsage")

package org.hugopalma.domaintranslator.editor

import com.intellij.codeInsight.hints.*
import com.intellij.codeInsight.hints.presentation.BasePresentation
import com.intellij.codeInsight.hints.presentation.PresentationFactory
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.diagnostic.thisLogger
import com.intellij.openapi.editor.DefaultLanguageHighlighterColors
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.markup.TextAttributes
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.util.startOffset
import io.ktor.util.*
import org.hugopalma.domaintranslator.settings.Settings
import java.awt.Graphics2D
import javax.swing.JPanel
import kotlin.math.roundToInt

class HintProvider : InlayHintsProvider<NoSettings> {

    override fun getCollectorFor(file: PsiFile, editor: Editor, settings: NoSettings, sink: InlayHintsSink): InlayHintsCollector? {
        val settingsState = ApplicationManager.getApplication().getService(Settings::class.java).state

        thisLogger().warn("File type is ${file.fileType}")

        return when {
            !settingsState.showInlays -> null
            isSupported(file.fileType) -> Collector(editor)
            else -> null
        }
    }

    private class Collector(editor: Editor) : FactoryInlayHintsCollector(editor) {
        override fun collect(element: PsiElement, editor: Editor, sink: InlayHintsSink): Boolean {
            if (!isSupportedElement(element)) return true
            val translation = findTranslation(element) ?: return true

            val settingsState = ApplicationManager.getApplication().getService(Settings::class.java).state
            if (settingsState.wordsToHideInlays.contains(element.text.toLowerCasePreservingASCIIRules())) return true

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

internal class CustomTextInlayPresentation(private val text: String, private val editor: Editor, private val element: PsiElement) : BasePresentation() {
    override val width: Int = editor.component.getFontMetrics(editor.component.font).stringWidth(text)
    override val height: Int = (editor.component.getFontMetrics(editor.component.font).height * 0.75).roundToInt()

    override fun paint(g: Graphics2D, attributes: TextAttributes) {
        val indent = editor.offsetToPoint2D(element.startOffset).x.toInt()
        val presentation = PresentationFactory(editor).inset(PresentationFactory(editor).text(text), indent)

        presentation.paint(g, editor.colorsScheme.getAttributes(DefaultLanguageHighlighterColors.INLAY_TEXT_WITHOUT_BACKGROUND))
    }

    override fun toString(): String = text
}
