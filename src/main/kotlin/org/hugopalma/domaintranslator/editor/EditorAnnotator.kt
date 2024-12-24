package org.hugopalma.domaintranslator.editor

import com.intellij.codeInsight.daemon.impl.HintRenderer
import com.intellij.lang.annotation.AnnotationHolder
import com.intellij.lang.annotation.Annotator
import com.intellij.lang.annotation.HighlightSeverity
import com.intellij.lang.java.JavaLanguage
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.InlayProperties
import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.openapi.editor.markup.EffectType
import com.intellij.openapi.editor.markup.TextAttributes
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiIdentifier
import com.intellij.psi.PsiImportStatement
import com.intellij.psi.PsiPackageStatement
import com.intellij.ui.JBColor
import com.intellij.util.ui.UIUtil
import org.hugopalma.domaintranslator.dictionary.DictionaryService
import java.awt.Font

class EditorAnnotator : Annotator {
    override fun annotate(element: PsiElement, holder: AnnotationHolder) {
        if (
            element !is PsiIdentifier ||
            element.parent.parent is PsiImportStatement ||
            element.parent.parent.parent is PsiImportStatement ||
            element.parent.parent is PsiPackageStatement
        ) {
            return
        }

        val psiFile = element.containingFile
        if (psiFile.language != JavaLanguage.INSTANCE) {
            return
        }

        val virtualFile = psiFile.virtualFile ?: return
        val project = psiFile.project

        val dictionary = project.getService(DictionaryService::class.java).getDictionary(element) ?: return
        val translation = dictionary.translate(element.text)
        val editor: Editor? = FileEditorManager.getInstance(project).getSelectedEditor(virtualFile)?.let {
            FileEditorManager.getInstance(project).selectedTextEditor
        }

        val inlayModel = editor!!.inlayModel

        val ta = TextAttributesKey.createTextAttributesKey("domain.translation", TextAttributes(null, null, JBColor.CYAN, EffectType.BOLD_DOTTED_LINE, Font.PLAIN))

        if (translation != null) {
            holder.newAnnotation(HighlightSeverity.INFORMATION, "Domain Translation: " + translation)
                .range(element.getTextRange())
                .textAttributes(ta)
                .create()

            val ip = InlayProperties().apply {
                showAbove(false)
                relatesToPrecedingText(false)
                priority(1000)
                disableSoftWrapping(false)
            }

            UIUtil.invokeLaterIfNeeded(
                kotlinx.coroutines.Runnable {
                    val indent = editor.offsetToPoint2D(element.getTextRange().startOffset).x.toInt()
                    val inlays = inlayModel.getInlineElementsInRange(element.getTextRange().startOffset, element.getTextRange().endOffset)

                    if (inlays.isNotEmpty()) {
                        inlays.forEach { it.dispose() }
                    }
                    inlayModel.addInlineElement<HintRenderer>(element.getTextRange().startOffset, ip, CustomHintRenderer("($translation)", indent))
                })
        } else {
            val inlays = inlayModel.getInlineElementsInRange(element.getTextRange().startOffset, element.getTextRange().endOffset)

            if (inlays.isEmpty()) {
                return
            }

            UIUtil.invokeLaterIfNeeded(
                kotlinx.coroutines.Runnable {
                    inlays.forEach { it.dispose() }
                })
        }
    }
}
