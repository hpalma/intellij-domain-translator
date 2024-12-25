package org.hugopalma.domaintranslator.settings

import com.intellij.openapi.components.*


@Service
@State(
    name = "org.hugopalma.domaintranslator.settings.AppSettings",
    storages = [Storage("DomainTranslatorPlugin.xml", roamingType = RoamingType.DEFAULT)],
    category = SettingsCategory.PLUGINS
)
class Settings : SimplePersistentStateComponent<Settings.State>(State()) {
    class State : BaseState() {
        var showInlays: Boolean = true
        var dictionaryFile: String? = null
            set(value) {
                field = if (value?.isEmpty() == true) null else value
            }
    }
}
