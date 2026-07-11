package com.devindie.myday.data.reflection

import com.devindie.myday.data.coroutines.DispatcherProvider
import com.devindie.myday.domain.model.reflection.DailyNoteContent
import com.devindie.myday.domain.model.reflection.IsoDate
import com.devindie.myday.domain.model.reflection.ReflectionError
import com.devindie.myday.domain.model.reflection.VaultLink
import com.devindie.myday.domain.repository.DailyNoteRepository
import com.devindie.myday.storage.api.StorageLocationToken
import kotlinx.coroutines.withContext

class DailyNoteRepositoryImpl(
    private val vaultLinkStore: VaultLinkStore,
    private val vaultNotes: VaultNoteDataSource,
    private val dispatchers: DispatcherProvider,
) : DailyNoteRepository {
    override suspend fun getVaultLink(): VaultLink? = vaultLinkStore.get()

    override suspend fun setVaultLink(link: VaultLink) {
        vaultLinkStore.set(link)
    }

    override suspend fun clearVaultLink() {
        vaultLinkStore.clear()
    }

    override suspend fun resolveAndRead(date: IsoDate): Result<DailyNoteContent?> = withContext(dispatchers.io) {
        val link = vaultLinkStore.get() ?: return@withContext Result.failure(ReflectionError.VaultNotLinked)
        val token = StorageLocationToken(link.tokenValue)
        val ref = ObsidianDailyNoteResolver(vaultNotes.fileReader(token)).resolve(date)
        vaultNotes.readTextIfExists(token, ref.relativePath).fold(
            onSuccess = { body ->
                if (body == null) {
                    Result.success(null)
                } else {
                    Result.success(
                        DailyNoteContent(
                            ref = ref,
                            body = body,
                            contentHash = dailyNoteContentHash(body),
                        ),
                    )
                }
            },
            onFailure = { Result.failure(it) },
        )
    }
}
