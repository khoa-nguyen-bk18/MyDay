package com.devindie.myday

import com.devindie.myday.data.source.local.browse.BrowseCardPagerFactoryImpl
import com.devindie.myday.feature.browse.api.BrowseCardPagerFactory
import org.koin.dsl.module

/** Wires [BrowseCardPagerFactoryImpl] to the presentation port at the composition root. */
val browsePagingModule =
    module {
        single<BrowseCardPagerFactory> {
            val impl = get<BrowseCardPagerFactoryImpl>()
            BrowseCardPagerFactory { query -> impl.pages(query) }
        }
    }
