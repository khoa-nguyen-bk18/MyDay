package com.devindie.myday.feature.collection.api

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.devindie.myday.feature.collection.impl.CollectionScreen
import kotlinx.serialization.Serializable

/** Standalone route — Collection is no longer a main bottom-nav tab. */
@Serializable
data object CollectionRoute

fun NavGraphBuilder.collectionDestination(onNavigateToCardDetail: (Long) -> Unit) {
    composable<CollectionRoute> {
        CollectionScreen(
            modifier = Modifier.fillMaxSize(),
            onCardClick = { card -> onNavigateToCardDetail(card.id) },
        )
    }
}
