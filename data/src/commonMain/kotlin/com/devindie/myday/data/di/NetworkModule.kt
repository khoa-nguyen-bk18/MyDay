package com.devindie.myday.data.di

import com.devindie.myday.data.auth.KSafeTokenStore
import com.devindie.myday.data.auth.TokenRefreshDataSource
import com.devindie.myday.data.auth.TokenStore
import com.devindie.myday.data.network.NetworkConfig
import com.devindie.myday.data.network.client.HttpClientFactory
import com.devindie.myday.data.source.remote.browse.BrowseCardRemoteDataSource
import com.devindie.myday.data.source.remote.browse.FakeBrowseCardRemoteDataSource
import com.devindie.myday.data.source.remote.browse.KtorBrowseCardRemoteDataSource
import io.ktor.client.HttpClient
import org.koin.core.module.Module
import org.koin.core.qualifier.named
import org.koin.dsl.module

fun networkModule(networkConfig: NetworkConfig = NetworkConfig()): Module = module {
    single { networkConfig }
    single { HttpClientFactory(networkConfig = get(), tokenStore = get()) }
    single<HttpClient>(named(NetworkQualifiers.REFRESH_HTTP_CLIENT)) {
        get<HttpClientFactory>().createRefreshClient()
    }
    single {
        TokenRefreshDataSource(
            refreshClient = get(named(NetworkQualifiers.REFRESH_HTTP_CLIENT)),
            networkConfig = get(),
        )
    }
    single<HttpClient>(named(NetworkQualifiers.AUTHENTICATED_HTTP_CLIENT)) {
        get<HttpClientFactory>().createAuthenticatedClient(tokenRefreshDataSource = get())
    }
    single<TokenStore> { KSafeTokenStore(ksafe = get()) }
    single<BrowseCardRemoteDataSource> {
        val config = get<NetworkConfig>()
        if (config.useFakeRemote) {
            FakeBrowseCardRemoteDataSource()
        } else {
            KtorBrowseCardRemoteDataSource(
                httpClient = get(named(NetworkQualifiers.AUTHENTICATED_HTTP_CLIENT)),
                networkConfig = config,
            )
        }
    }
}
