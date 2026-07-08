package com.devindie.myday.domain.fake

import com.devindie.myday.domain.model.settings.BooleanSettingDefinition
import com.devindie.myday.domain.model.settings.SettingKey
import com.devindie.myday.domain.model.settings.SettingsSection
import com.devindie.myday.domain.model.settings.TextSettingDefinition
import com.devindie.myday.domain.settings.SettingsCatalog

class FakeSettingsCatalog : SettingsCatalog {
    override val sections =
        listOf(
            SettingsSection(
                id = "general",
                title = "General",
                definitions =
                listOf(
                    BooleanSettingDefinition(
                        key = SettingKey("general.notifications"),
                        title = "Notifications",
                        description = null,
                        default = true,
                    ),
                    TextSettingDefinition(
                        key = SettingKey("general.nickname"),
                        title = "Nickname",
                        description = null,
                        default = "Player",
                        maxLength = 20,
                    ),
                ),
            ),
        )
}
