package com.devindie.myday.domain.usecase.settings

import com.devindie.myday.domain.model.settings.SettingKey
import com.devindie.myday.domain.model.settings.SettingValue
import com.devindie.myday.domain.model.settings.SettingsError
import com.devindie.myday.domain.repository.SettingsRepository
import com.devindie.myday.domain.settings.SettingsCatalog

class UpdateSettingUseCase(
    private val repository: SettingsRepository,
    private val catalog: SettingsCatalog,
) {
    suspend operator fun invoke(key: SettingKey, value: SettingValue): Result<Unit> =
        runCatching {
            val definition =
                catalog.definition(key)
                    ?: throw SettingsError.UnknownSettingKey
            SettingsValidators.validate(definition, value)
            repository.setValue(key, value)
        }
}
