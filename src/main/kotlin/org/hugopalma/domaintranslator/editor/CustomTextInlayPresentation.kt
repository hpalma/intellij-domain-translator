package org.hugopalma.domaintranslator.editor

import com.intellij.codeInsight.hints.presentation.BasePresentation
import com.intellij.codeInsight.hints.presentation.InlayTextMetricsStorage
import com.intellij.ide.ui.AntialiasingType
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.markup.TextAttributes
import com.intellij.psi.PsiElement
import com.intellij.psi.util.startOffset
import com.intellij.ui.JBColor
import java.awt.Graphics2D
import java.awt.RenderingHints

@Suppress("UnstableApiUsage")
class CustomTextInlayPresentation(
    private val metricsStorage: InlayTextMetricsStorage,
    private val text: String,
    private val editor: Editor,
    private val element: PsiElement
) : BasePresentation() {
    override val width: Int = getMetrics().getStringWidth(text)
    override val height: Int = getMetrics().fontHeight

    private fun getMetrics() = metricsStorage.getFontMetrics(false)

    override fun paint(g: Graphics2D, attributes: TextAttributes) {
        val savedHint = g.getRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING)

        customizeText(attributes)

        val indent = editor.offsetToPoint2D(element.startOffset).x.toInt()

        try {
            val metrics = getMetrics()
            g.font = getMetrics().font
            g.color = attributes.foregroundColor
            g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, AntialiasingType.getKeyForCurrentScope(false))

            g.drawString(text, indent, metrics.fontBaseline)
        } finally {
            g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, savedHint)
        }
    }

    private fun customizeText(textAttributes: TextAttributes) {
        textAttributes.foregroundColor = JBColor.BLUE
    }

    override fun toString(): String = text
}
