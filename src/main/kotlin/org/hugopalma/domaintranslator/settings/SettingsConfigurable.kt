package org.hugopalma.domaintranslator.settings

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.options.Configurable
import javax.swing.JComponent


class SettingsConfigurable : Configurable {
    private var settingsComponent: SettingsComponent? = null

    override fun getPreferredFocusedComponent(): JComponent {
        return settingsComponent!!.getPreferredFocusedComponent()
    }

    override fun createComponent(): JComponent? {
        settingsComponent = SettingsComponent()
        return settingsComponent!!.getPanel()
    }

    override fun isModified(): Boolean {
        val state = getState()
        return settingsComponent!!.showInlays() != state.showInlays ||
                settingsComponent!!.getDictionaryFile() != state.dictionaryFile ||
                settingsComponent!!.getHideInlays() != state.hideInlays
    }

    override fun reset() {
        val state = getState()
        settingsComponent!!.setDictionaryFile(state.dictionaryFile)
        settingsComponent!!.setShowInlays(state.showInlays)
        settingsComponent!!.setHideInlays(state.hideInlays)
    }

    override fun disposeUIResources() {
        settingsComponent = null
    }

    override fun apply() {
        val state = getState()
        state.dictionaryFile = settingsComponent!!.getDictionaryFile()
        state.showInlays = settingsComponent!!.showInlays()
        state.hideInlays = settingsComponent!!.getHideInlays()
    }

    override fun getDisplayName(): String {
        return "Domain Translator"
    }

    private fun getState(): Settings.State {
        return ApplicationManager.getApplication().getService(Settings::class.java).state
    }
}
