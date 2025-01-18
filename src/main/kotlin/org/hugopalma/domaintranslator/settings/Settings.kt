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

        val wordsToHideInlays = mutableListOf<String>()

        var hideInlays: String = ""
            set(value) {
                field = value

                wordsToHideInlays.clear()
                wordsToHideInlays.addAll(
                    value.split(",")
                        .map { it.trim().lowercase() })
            }

        var dictionaryFile: String? = null
            set(value) {
                field = if (value?.isEmpty() == true) null else value
            }
    }
}
