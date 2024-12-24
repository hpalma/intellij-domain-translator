package org.hugopalma.domaintranslator.editor

import com.intellij.codeInsight.daemon.impl.HintRenderer
import com.intellij.openapi.editor.Inlay
import com.intellij.openapi.editor.markup.TextAttributes
import com.intellij.ui.JBColor
import java.awt.Graphics
import java.awt.Rectangle

class CustomHintRenderer(text: String?, private val indent: Int) : HintRenderer(text) {
    override fun paint(inlay: Inlay<*>, g: Graphics, r: Rectangle, textAttributes: TextAttributes) {
        r.x = indent
        r.y = r.y +2
        customizeText(inlay, textAttributes)

        super.paint(inlay, g, r, textAttributes)
    }

    private fun customizeText(inlay: Inlay<*>, textAttributes: TextAttributes) {
        val editor = inlay.editor
        val att = getTextAttributes(editor) ?: textAttributes
        att.foregroundColor = JBColor.BLUE
    }
}
