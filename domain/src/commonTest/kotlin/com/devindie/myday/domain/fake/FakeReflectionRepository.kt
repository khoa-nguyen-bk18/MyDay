package com.devindie.myday.domain.fake

import com.devindie.myday.domain.model.reflection.IsoDate
import com.devindie.myday.domain.model.reflection.ReflectionDocument
import com.devindie.myday.domain.repository.ReflectionRepository

class FakeReflectionRepository(
    var generateResult: Result<String> = Result.success("# Reflection\n\nGenerated."),
    var shortenResult: Result<String> = Result.success("# Reflection\n\nShorter."),
    var existsResult: Result<Boolean> = Result.success(false),
    var saveResult: Result<ReflectionDocument> =
        Result.success(
            ReflectionDocument(
                date = "2026-07-11",
                markdown = "# Reflection",
                relativePath = "reflections/2026-07-11.md",
            ),
        ),
) : ReflectionRepository {
    var lastGenerateArgs: Triple<String, String, String>? = null
    var lastShortenArgs: Triple<String, String, String>? = null
    var lastSaveArgs: SaveArgs? = null

    data class SaveArgs(val date: IsoDate, val folder: String, val markdown: String, val replaceExistingFile: Boolean)

    override suspend fun generateMarkdown(sourceText: String, model: String, apiKey: String): Result<String> {
        lastGenerateArgs = Triple(sourceText, model, apiKey)
        return generateResult
    }

    override suspend fun shortenMarkdown(currentMarkdown: String, model: String, apiKey: String): Result<String> {
        lastShortenArgs = Triple(currentMarkdown, model, apiKey)
        return shortenResult
    }

    override suspend fun reflectionFileExists(date: IsoDate, folder: String): Result<Boolean> = existsResult

    override suspend fun saveToVault(
        date: IsoDate,
        folder: String,
        markdown: String,
        replaceExistingFile: Boolean,
    ): Result<ReflectionDocument> {
        lastSaveArgs = SaveArgs(date, folder, markdown, replaceExistingFile)
        return saveResult
    }
}
