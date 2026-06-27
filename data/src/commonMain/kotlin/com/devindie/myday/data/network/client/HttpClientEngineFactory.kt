package com.devindie.myday.data.network.client

import io.ktor.client.engine.HttpClientEngine

internal expect fun createPlatformHttpClientEngine(): HttpClientEngine
