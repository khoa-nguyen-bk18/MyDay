package com.devindie.myday.data.source.local.browse

import com.devindie.myday.domain.model.browse.BrowseCategory
import com.devindie.myday.domain.model.browse.CollectibleCard
internal fun BrowseCardEntity.toDomain(): CollectibleCard = CollectibleCard(
    id = id,
    name = name,
    setName = setName,
    condition = condition,
    priceDisplay = formatPriceCents(priceCents),
    quantity = quantity,
    category = BrowseCategory.entries.firstOrNull { it.name == category } ?: BrowseCategory.All,
)
