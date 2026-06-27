package com.devindie.myday.feature.browse.impl

import com.devindie.myday.domain.model.browse.BrowseCategory

data class BrowseScreenUiState(val searchQuery: String = "", val selectedCategory: BrowseCategory = BrowseCategory.All)
