package com.devindie.myday.feature.browse.api

import com.devindie.myday.feature.browse.impl.BrowseViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val browseFeatureModule =
    module {
        viewModelOf(::BrowseViewModel)
    }
