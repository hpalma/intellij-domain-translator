package org.hugopalma.domaintranslator.settings

import com.intellij.openapi.ui.ComboBox
import com.intellij.ui.components.JBCheckBox
import com.intellij.ui.components.JBLabel
import com.intellij.ui.components.JBTextField
import com.intellij.util.ui.FormBuilder
import com.intellij.util.ui.JBFont
import com.intellij.util.ui.JBUI
import com.intellij.util.ui.UIUtil
import okio.Path
import org.jetbrains.kotlin.idea.base.util.onTextChange
import java.util.*
import javax.swing.JComponent
import javax.swing.JPanel
import javax.swing.plaf.LabelUI

class SettingsComponent {
    private var mainPanel: JPanel? = null
    private var hideInlaysPanel: JPanel
    private val showInlays = JBCheckBox("Display inlays", true)
    private val dictionaryFile = JBTextField()
    private val hideInlays = JBTextField()
    private val useIntellijLanguage = JBCheckBox("Use system language", true)
    private var languagesPanel: JPanel
    private val languages = ComboBox(getAllLanguages().toTypedArray())

    init {
        hideInlaysPanel = FormBuilder.createFormBuilder()
            .addLabeledComponent("Words to hide in inlay:", hideInlays, 1, false)
            .addComponent(CommentLabel("List of words that won't be translated in inlay separated by comma"))
            .panel
        showInlays.addChangeListener { hideInlays.isEnabled = showInlays.isSelected }

        languagesPanel = FormBuilder.createFormBuilder()
            .addLabeledComponent("Show translation in language", languages, 1, false)
            .panel
        useIntellijLanguage.addChangeListener { languages.isEnabled = !useIntellijLanguage.isSelected }

        dictionaryFile.onTextChange { event ->
            useIntellijLanguage.isEnabled = dictionaryFile.text.isEmpty()
            languages.isEnabled = dictionaryFile.text.isEmpty()
        }

        mainPanel = FormBuilder.createFormBuilder()
            .addLabeledComponent("Dictionary file:", dictionaryFile, 1, false)
            .addComponent(CommentLabel("Path to dictionary .csv file relative to module root. Defaults to \$MODULE_ROOT${Path.DIRECTORY_SEPARATOR}dictionary.csv"))
            .addVerticalGap(UIUtil.DEFAULT_VGAP)
            .addComponent(showInlays, 1)
            .addComponent(CommentLabel("Should the inline domain translation be displayed"))
            .addComponentToRightColumn(hideInlaysPanel)
            .addComponent(useIntellijLanguage)
            .addComponentToRightColumn(languagesPanel)
            .addComponentFillVertically(JPanel(), 0)
            .panel
    }

    private fun getAllLanguages(): List<Language> {
        return Locale.getAvailableLocales()
            .filter { locale -> locale.language.isNotEmpty() }
            .distinctBy { locale -> locale.displayLanguage }
            .sortedBy { locale -> locale.displayLanguage }
            .map { locale -> Language(locale) }
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
        hideInlaysPanel.isEnabled = value
    }

    fun getHideInlays(): String {
        return hideInlays.text
    }

    fun setHideInlays(value: String) {
        hideInlays.text = value
    }

    fun setUseSystemLanguage(value: Boolean) {
        useIntellijLanguage.isSelected = value
        languages.isEnabled = !value
    }

    fun getUseSystemLanguage(): Boolean {
        return useIntellijLanguage.isSelected
    }

    fun getSelectedLanguage(): Language? {
        return languages.selectedItem as Language?
    }

    fun setSelectedLanguage(language: Language?) {
        languages.selectedItem = language
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

class Language(val locale: Locale) {
    override fun toString(): String {
        return locale.displayLanguage + " (${locale.language})"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        return locale.language == (other as Language).locale.language
    }

    override fun hashCode(): Int {
        return locale.hashCode()
    }
}
