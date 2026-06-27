package com.devindie.myday.data.di

import com.devindie.myday.data.auth.UserRepositoryImpl
import com.devindie.myday.data.coroutines.DispatcherProvider
import com.devindie.myday.data.coroutines.IosDispatcherProvider
import com.devindie.myday.data.local.browse.getBrowseDatabaseBuilder
import com.devindie.myday.data.onboarding.OnboardingRepositoryImpl
import com.devindie.myday.data.onboarding.createOnboardingDataStore
import com.devindie.myday.data.settings.SettingsRepositoryImpl
import com.devindie.myday.data.settings.createSettingsDataStore
import com.devindie.myday.data.source.local.browse.BrowseCardLocalDataSource
import com.devindie.myday.data.source.local.browse.BrowseCardLocalDataSourceImpl
import com.devindie.myday.data.source.local.browse.BrowseCardPagerFactoryImpl
import com.devindie.myday.data.source.local.browse.BrowseDatabase
import com.devindie.myday.data.source.local.browse.CardDetailRepositoryImpl
import com.devindie.myday.data.source.local.browse.getBrowseDatabase
import com.devindie.myday.data.source.startup.AppStartupRepositoryImpl
import com.devindie.myday.domain.repository.AppStartupRepository
import com.devindie.myday.domain.repository.CardDetailRepository
import com.devindie.myday.domain.repository.OnboardingRepository
import com.devindie.myday.domain.repository.SettingsRepository
import com.devindie.myday.domain.repository.UserRepository
import eu.anifantakis.lib.ksafe.KSafe
import org.koin.core.module.Module
import org.koin.dsl.module

actual fun platformDataModule(): Module = module {
    single<DispatcherProvider> { IosDispatcherProvider() }
    single { KSafe() }
    includes(networkModule())
    single {
        getBrowseDatabase(
            builder = getBrowseDatabaseBuilder(),
            ioDispatcher = get<DispatcherProvider>().io,
        )
    }
    single { get<BrowseDatabase>().browseCardDao() }
    single { get<BrowseDatabase>().browseRemoteKeyDao() }
    single<BrowseCardLocalDataSource> {
        BrowseCardLocalDataSourceImpl(dao = get())
    }
    single {
        BrowseCardPagerFactoryImpl(
            database = get(),
            remoteDataSource = get(),
        )
    }
    single<CardDetailRepository> {
        CardDetailRepositoryImpl(
            localDataSource = get(),
            dispatchers = get(),
        )
    }
    single<UserRepository> {
        UserRepositoryImpl(tokenStore = get())
    }
    single<AppStartupRepository> {
        AppStartupRepositoryImpl(browseCardDao = get())
    }
    single { createOnboardingDataStore() }
    single<OnboardingRepository> { OnboardingRepositoryImpl(dataStore = get()) }
    single { createSettingsDataStore() }
    single<SettingsRepository> { SettingsRepositoryImpl(dataStore = get()) }
}
