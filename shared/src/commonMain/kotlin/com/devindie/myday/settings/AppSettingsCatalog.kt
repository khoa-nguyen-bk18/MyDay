package com.devindie.myday.settings

import com.devindie.myday.domain.model.settings.SettingsSection
import com.devindie.myday.domain.settings.SettingsCatalog
import com.devindie.myday.feature.browse.api.BrowseSettings

class AppSettingsCatalog : SettingsCatalog {
    override val sections =
        listOf(
            SettingsSection(
                id = "appearance",
                title = "Appearance",
                definitions = AppSettings.appearanceDefinitions(),
            ),
            SettingsSection(
                id = "browse",
                title = "Browse",
                definitions = BrowseSettings.definitions(),
            ),
        )
}
