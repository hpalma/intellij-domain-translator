package org.hugopalma.domaintranslator.editor

import com.intellij.lang.annotation.AnnotationHolder
import com.intellij.lang.annotation.Annotator
import com.intellij.lang.annotation.HighlightSeverity
import com.intellij.lang.java.JavaLanguage
import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.openapi.editor.markup.EffectType
import com.intellij.openapi.editor.markup.TextAttributes
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiIdentifier
import com.intellij.ui.JBColor
import org.hugopalma.domaintranslator.dictionary.DictionaryService
import java.awt.Font

class EditorAnnotator : Annotator {
    override fun annotate(element: PsiElement, holder: AnnotationHolder) {
        if (element !is PsiIdentifier) {
            return
        }

        val psiFile = element.containingFile
        if (psiFile.language != JavaLanguage.INSTANCE) {
            return
        }

        val project = psiFile.project

        val dictionary = project.getService(DictionaryService::class.java).getDictionary(element) ?: return
        val translation = dictionary.translate(element.text)

        val ta = TextAttributesKey.createTextAttributesKey("domain.translation", TextAttributes(null, null, JBColor.CYAN, EffectType.BOLD_DOTTED_LINE, Font.PLAIN))

        if (translation != null) {
            holder.newAnnotation(HighlightSeverity.INFORMATION, "Domain Translation: " + translation)
                .range(element.getTextRange())
                .textAttributes(ta)
                .create()
        }
    }
}
