package com.devindie.myday.data.reflection

import com.devindie.myday.data.coroutines.DispatcherProvider
import com.devindie.myday.data.coroutines.runIoResult
import com.devindie.myday.domain.model.reflection.IsoDate
import com.devindie.myday.domain.model.reflection.ReflectionDocument
import com.devindie.myday.domain.model.reflection.ReflectionError
import com.devindie.myday.domain.reflection.EmbedLink
import com.devindie.myday.domain.repository.DailyNoteRepository
import com.devindie.myday.domain.repository.ReflectionRepository
import com.devindie.myday.storage.api.StorageLocationToken
import kotlinx.coroutines.withContext

class ReflectionRepositoryImpl(
    private val openRouter: OpenRouterReflectionDataSource,
    private val vaultLinkStore: VaultLinkStore,
    private val vaultNotes: VaultNoteDataSource,
    private val dailyNotes: DailyNoteRepository,
    private val dispatchers: DispatcherProvider,
) : ReflectionRepository {
    override suspend fun generateMarkdown(sourceText: String, model: String, apiKey: String): Result<String> =
        openRouter.generate(source = sourceText, model = model, apiKey = apiKey)

    override suspend fun shortenMarkdown(currentMarkdown: String, model: String, apiKey: String): Result<String> =
        openRouter.shorten(currentMarkdown = currentMarkdown, model = model, apiKey = apiKey)

    override suspend fun reflectionFileExists(date: IsoDate, folder: String): Result<Boolean> =
        withContext(dispatchers.io) {
            runIoResult {
                val token = requireVaultToken()
                val path = reflectionRelativePath(folder, date)
                vaultNotes.readTextIfExists(token, path).getOrThrow() != null
            }
        }

    override suspend fun saveToVault(
        date: IsoDate,
        folder: String,
        markdown: String,
        replaceExistingFile: Boolean,
    ): Result<ReflectionDocument> = withContext(dispatchers.io) {
        runIoResult {
            val token = requireVaultToken()
            val reflectionPath = reflectionRelativePath(folder, date)

            if (!replaceExistingFile) {
                val exists = vaultNotes.readTextIfExists(token, reflectionPath).getOrThrow() != null
                if (exists) throw ReflectionError.AlreadyExists
            }

            vaultNotes.writeText(token, reflectionPath, markdown).getOrThrow()

            val dailyNote =
                dailyNotes.resolveAndRead(date).getOrThrow()
                    ?: throw ReflectionError.DailyNoteMissing
            val updatedBody = EmbedLink.appendBlock(dailyNote.body, folder, date)
            vaultNotes.writeText(token, dailyNote.ref.relativePath, updatedBody).getOrThrow()

            ReflectionDocument(
                date = date,
                markdown = markdown,
                relativePath = reflectionPath,
            )
        }
    }

    private suspend fun requireVaultToken(): StorageLocationToken {
        val link = vaultLinkStore.get() ?: throw ReflectionError.VaultNotLinked
        return StorageLocationToken(link.tokenValue)
    }
}

internal fun reflectionRelativePath(folder: String, date: IsoDate): String {
    val normalizedFolder = folder.trim('/').trim()
    return if (normalizedFolder.isEmpty()) {
        "$date.md"
    } else {
        "$normalizedFolder/$date.md"
    }
}
