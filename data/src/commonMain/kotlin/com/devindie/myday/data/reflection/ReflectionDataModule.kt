package com.devindie.myday.data.reflection

import com.devindie.myday.data.di.NetworkQualifiers
import com.devindie.myday.data.network.client.HttpClientFactory
import com.devindie.myday.domain.reflection.ReflectionInjection
import com.devindie.myday.domain.repository.AiKeyRepository
import com.devindie.myday.domain.repository.DailyNoteRepository
import com.devindie.myday.domain.repository.DraftRepository
import com.devindie.myday.domain.repository.ReflectionPrefsRepository
import com.devindie.myday.domain.repository.ReflectionRepository
import com.devindie.myday.domain.repository.ReflectionSchedulerPort
import com.devindie.myday.storage.api.StorageClient
import io.ktor.client.HttpClient
import org.koin.core.module.Module
import org.koin.core.qualifier.named
import org.koin.dsl.module

fun reflectionDataModule(): Module = module {
    single(named(ReflectionInjection.CLOCK)) { { reflectionEpochMillis() } }
    single(named(ReflectionInjection.TODAY_ISO)) { { reflectionTodayIso() } }
    single(named(ReflectionInjection.MINUTE_OF_DAY)) { { reflectionMinuteOfDay() } }

    single<HttpClient>(named(NetworkQualifiers.OPENROUTER_HTTP_CLIENT)) {
        get<HttpClientFactory>().createOpenRouterClient()
    }

    single<OpenRouterKeyStore> { KSafeOpenRouterKeyStore(ksafe = get()) }
    single { VaultLinkStore(dataStore = get(named(ReflectionInjection.VAULT_LINK_DATASTORE)), dispatchers = get()) }
    single { DraftLocalDataSource(dataStore = get(named(ReflectionInjection.DRAFT_DATASTORE))) }
    single { VaultNoteDataSource(storageClient = get<StorageClient>()) }
    single {
        OpenRouterReflectionDataSource(
            httpClient = get(named(NetworkQualifiers.OPENROUTER_HTTP_CLIENT)),
            dispatchers = get(),
        )
    }

    single<ReflectionPrefsRepository> {
        ReflectionPrefsRepositoryImpl(
            dataStore = get(named(ReflectionInjection.REFLECTION_PREFS_DATASTORE)),
            dispatchers = get(),
        )
    }
    single<DraftRepository> {
        DraftRepositoryImpl(
            localDataSource = get(),
            dispatchers = get(),
        )
    }
    single<AiKeyRepository> {
        AiKeyRepositoryImpl(
            keyStore = get(),
            dispatchers = get(),
        )
    }
    single<DailyNoteRepository> {
        DailyNoteRepositoryImpl(
            vaultLinkStore = get(),
            vaultNotes = get(),
            dispatchers = get(),
        )
    }
    single<ReflectionRepository> {
        ReflectionRepositoryImpl(
            openRouter = get(),
            vaultLinkStore = get(),
            vaultNotes = get(),
            dailyNotes = get(),
            dispatchers = get(),
        )
    }
    single<ReflectionSchedulerPort> { NoOpReflectionScheduler() }
}
