package com.devindie.myday.domain.model.settings

data class SettingsSection(
    val id: String,
    val title: String,
    val definitions: List<SettingDefinition>,
)
