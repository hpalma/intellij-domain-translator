package org.hugopalma.domaintranslator.editor

import com.intellij.lang.annotation.AnnotationHolder
import com.intellij.lang.annotation.Annotator
import com.intellij.lang.annotation.HighlightSeverity
import com.intellij.psi.PsiElement

class DocumentationProvider : Annotator {

    override fun annotate(element: PsiElement, holder: AnnotationHolder) {
        if (!isSupportedElement(element)) return
        val translation = findTranslation(element) ?: return

        holder.newSilentAnnotation(HighlightSeverity.INFORMATION)
            .tooltip("Translation: $translation")
            .create()
    }
}
