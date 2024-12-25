package org.hugopalma.domaintranslator.settings

import com.intellij.ui.components.JBCheckBox
import com.intellij.ui.components.JBLabel
import com.intellij.ui.components.JBTextField
import com.intellij.util.ui.FormBuilder
import com.intellij.util.ui.JBFont
import com.intellij.util.ui.JBUI
import com.intellij.util.ui.UIUtil
import okio.Path
import javax.swing.JComponent
import javax.swing.JPanel
import javax.swing.plaf.LabelUI

class SettingsComponent {
    private var mainPanel: JPanel? = null
    private val showInlays = JBCheckBox("Display inlays", true)
    private val dictionayFile = JBTextField()

    init {
        mainPanel = FormBuilder.createFormBuilder()
            .addLabeledComponent(JBLabel("Dictionary file:"), dictionayFile, 1, false)
            .addComponent(CommentLabel("Path to dictionary .csv file relative to module root. Defaults to \$MODULE_ROOT${Path.DIRECTORY_SEPARATOR}dictionary.csv"))
            .addVerticalGap(UIUtil.DEFAULT_VGAP)
            .addComponent(showInlays, 1)
            .addComponent(CommentLabel("Should the inline domain translation be displayed"))
            .addComponentFillVertically(JPanel(), 0)
            .panel
    }

    fun getPanel(): JPanel? {
        return mainPanel
    }

    fun getPreferredFocusedComponent(): JComponent {
        return showInlays
    }

    fun getDictionaryFile():String {
        return dictionayFile.text
    }

    fun setDictionaryFile(dictionaryFile: String?) {
        dictionayFile.text = dictionaryFile
    }

    fun showInlays() :Boolean {
        return showInlays.isSelected
    }

    fun setShowInlays(value: Boolean) {
        showInlays.isSelected = value
    }
}

class CommentLabel(text: String) : JBLabel(text) {
    init {
        foreground = JBUI.CurrentTheme.ContextHelp.FOREGROUND
    }

    override fun setUI(ui: LabelUI) {
        super.setUI(ui)
        font = JBFont.medium()
    }
}
