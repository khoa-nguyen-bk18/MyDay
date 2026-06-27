package com.devindie.myday.settings

import com.devindie.myday.domain.settings.SettingsCatalog
import org.koin.dsl.module

fun settingsCatalogModule() =
    module {
        single<SettingsCatalog> { AppSettingsCatalog() }
    }
