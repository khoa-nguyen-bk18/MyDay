package com.devindie.myday.feature.dailyreflection.api

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.devindie.myday.core.navigation.MainRoute
import com.devindie.myday.feature.dailyreflection.impl.DailyReflectionScreen

fun NavGraphBuilder.reflectDestination() {
    composable<MainRoute.Reflect> {
        DailyReflectionScreen(modifier = Modifier.fillMaxSize())
    }
}
