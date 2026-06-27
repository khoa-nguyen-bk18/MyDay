package com.devindie.myday.domain.usecase.settings

import com.devindie.myday.domain.model.settings.SettingKey
import com.devindie.myday.domain.model.settings.SettingValue
import com.devindie.myday.domain.model.settings.defaultValue
import com.devindie.myday.domain.repository.SettingsRepository
import com.devindie.myday.domain.settings.SettingsCatalog
import com.devindie.myday.domain.usecase.UseCase

class GetSettingUseCase(
    private val repository: SettingsRepository,
    private val catalog: SettingsCatalog,
) : UseCase<SettingKey, SettingValue?> {
    override suspend fun invoke(parameters: SettingKey): SettingValue? {
        val definition = catalog.definition(parameters) ?: return null
        val kind = definition.defaultValue()
        return repository.getValue(parameters, kind) ?: kind
    }
}
