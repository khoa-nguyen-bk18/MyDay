package com.devindie.myday.analytics.api.compose

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import com.devindie.myday.analytics.api.AnalyticsClient
import org.koin.compose.koinInject

@Composable
fun TrackScreen(
    screenName: String,
    screenClass: String? = null,
    client: AnalyticsClient = koinInject(),
) {
    LaunchedEffect(screenName, screenClass) {
        client.logScreen(screenName, screenClass)
    }
}
