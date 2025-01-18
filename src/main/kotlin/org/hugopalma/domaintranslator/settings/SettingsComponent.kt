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
    private var hideInlaysPanel: JPanel
    private val showInlays = JBCheckBox("Display inlays", true)
    private val dictionaryFile = JBTextField()
    private val hideInlays = JBTextField()

    init {
        hideInlaysPanel = FormBuilder.createFormBuilder()
            .addLabeledComponent("Words to hide in inlay:", hideInlays, 1, false)
            .addComponent(CommentLabel("List of words that won't be translated in inlay separated by comma"))
            .panel

        showInlays.addChangeListener { hideInlaysPanel.isVisible = showInlays.isSelected }

        mainPanel = FormBuilder.createFormBuilder()
            .addLabeledComponent("Dictionary file:", dictionaryFile, 1, false)
            .addComponent(CommentLabel("Path to dictionary .csv file relative to module root. Defaults to \$MODULE_ROOT${Path.DIRECTORY_SEPARATOR}dictionary.csv"))
            .addVerticalGap(UIUtil.DEFAULT_VGAP)
            .addComponent(showInlays, 1)
            .addComponent(CommentLabel("Should the inline domain translation be displayed"))
            .addComponentToRightColumn(hideInlaysPanel)
            .addComponentFillVertically(JPanel(), 0)
            .panel
    }

    fun getPanel(): JPanel? {
        return mainPanel
    }

    fun getPreferredFocusedComponent(): JComponent {
        return showInlays
    }

    fun getDictionaryFile(): String {
        return dictionaryFile.text
    }

    fun setDictionaryFile(value: String?) {
        dictionaryFile.text = value
    }

    fun showInlays(): Boolean {
        return showInlays.isSelected
    }

    fun setShowInlays(value: Boolean) {
        showInlays.isSelected = value
        hideInlaysPanel.isVisible = value
    }

    fun getHideInlays(): String {
        return hideInlays.text
    }

    fun setHideInlays(value: String) {
        hideInlays.text = value
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
