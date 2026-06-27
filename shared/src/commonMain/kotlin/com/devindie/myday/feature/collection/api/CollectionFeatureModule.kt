package com.devindie.myday.feature.collection.api

import com.devindie.myday.feature.collection.impl.CollectionViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val collectionFeatureModule =
    module {
        viewModelOf(::CollectionViewModel)
    }
