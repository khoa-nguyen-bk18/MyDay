package com.devindie.myday.domain.usecase.settings

import com.devindie.myday.domain.model.settings.SettingKey
import com.devindie.myday.domain.model.settings.SettingValue
import com.devindie.myday.domain.model.settings.defaultValue
import com.devindie.myday.domain.repository.SettingsRepository
import com.devindie.myday.domain.settings.SettingsCatalog
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ObserveSettingUseCase(private val repository: SettingsRepository, private val catalog: SettingsCatalog) {
    operator fun invoke(key: SettingKey): Flow<SettingValue?> {
        val definition = catalog.definition(key)
        if (definition == null) {
            return repository.observeValue(key, SettingValue.BooleanValue(false))
        }
        val kind = definition.defaultValue()
        return repository.observeValue(key, kind).map { stored -> stored ?: kind }
    }
}
