package org.hugopalma.domaintranslator.settings

import com.intellij.openapi.components.*
import java.util.Locale


@Service
@State(
    name = "org.hugopalma.domaintranslator.settings.AppSettings",
    storages = [Storage("DomainTranslatorPlugin.xml", roamingType = RoamingType.DEFAULT)],
    category = SettingsCategory.PLUGINS
)
class Settings : SimplePersistentStateComponent<Settings.State>(State()) {
    class State : BaseState() {
        var showInlays: Boolean = true

        var useSystemLanguage: Boolean = true

        var language: String = Locale.ENGLISH.language

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
