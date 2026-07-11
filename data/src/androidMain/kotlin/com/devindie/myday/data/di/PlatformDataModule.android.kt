package com.devindie.myday.data.di

import android.content.Context
import com.devindie.myday.data.auth.UserRepositoryImpl
import com.devindie.myday.data.coroutines.AndroidDispatcherProvider
import com.devindie.myday.data.coroutines.DispatcherProvider
import com.devindie.myday.data.local.browse.getBrowseDatabaseBuilder
import com.devindie.myday.data.onboarding.OnboardingRepositoryImpl
import com.devindie.myday.data.onboarding.createOnboardingDataStore
import com.devindie.myday.data.reflection.AndroidReflectionScheduler
import com.devindie.myday.data.reflection.createDraftDataStore
import com.devindie.myday.data.reflection.createReflectionPrefsDataStore
import com.devindie.myday.data.reflection.createVaultLinkDataStore
import com.devindie.myday.data.reflection.reflectionDataModule
import com.devindie.myday.data.settings.SettingsRepositoryImpl
import com.devindie.myday.data.settings.createSettingsDataStore
import com.devindie.myday.data.source.local.browse.BrowseCardLocalDataSource
import com.devindie.myday.data.source.local.browse.BrowseCardLocalDataSourceImpl
import com.devindie.myday.data.source.local.browse.BrowseCardPagerFactoryImpl
import com.devindie.myday.data.source.local.browse.BrowseDatabase
import com.devindie.myday.data.source.local.browse.CardDetailRepositoryImpl
import com.devindie.myday.data.source.local.browse.getBrowseDatabase
import com.devindie.myday.data.source.startup.AppStartupRepositoryImpl
import com.devindie.myday.domain.reflection.ReflectionInjection
import com.devindie.myday.domain.repository.AppStartupRepository
import com.devindie.myday.domain.repository.CardDetailRepository
import com.devindie.myday.domain.repository.OnboardingRepository
import com.devindie.myday.domain.repository.ReflectionSchedulerPort
import com.devindie.myday.domain.repository.SettingsRepository
import com.devindie.myday.domain.repository.UserRepository
import eu.anifantakis.lib.ksafe.KSafe
import org.koin.core.module.Module
import org.koin.core.qualifier.named
import org.koin.dsl.module

actual fun platformDataModule(): Module = module {
    single<DispatcherProvider> { AndroidDispatcherProvider() }
    single { KSafe(get<Context>()) }
    includes(networkModule())
    single {
        getBrowseDatabase(
            builder = getBrowseDatabaseBuilder(get<Context>()),
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
    single { createOnboardingDataStore(get<Context>()) }
    single<OnboardingRepository> { OnboardingRepositoryImpl(dataStore = get()) }
    single { createSettingsDataStore(get<Context>()) }
    single<SettingsRepository> { SettingsRepositoryImpl(dataStore = get()) }
    single(named(ReflectionInjection.REFLECTION_PREFS_DATASTORE)) {
        createReflectionPrefsDataStore(get<Context>())
    }
    single(named(ReflectionInjection.VAULT_LINK_DATASTORE)) { createVaultLinkDataStore(get<Context>()) }
    single(named(ReflectionInjection.DRAFT_DATASTORE)) { createDraftDataStore(get<Context>()) }
    includes(reflectionDataModule())
    single<ReflectionSchedulerPort> { AndroidReflectionScheduler(context = get()) }
}
