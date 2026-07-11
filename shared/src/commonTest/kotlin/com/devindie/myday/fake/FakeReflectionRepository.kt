package com.devindie.myday.fake

import com.devindie.myday.domain.model.reflection.IsoDate
import com.devindie.myday.domain.model.reflection.ReflectionDocument
import com.devindie.myday.domain.repository.ReflectionRepository

class FakeReflectionRepository(
    var generateResult: Result<String> = Result.success("# Daily Reflection\n\nGenerated."),
    var shortenResult: Result<String> = Result.success("# Daily Reflection\n\nShorter."),
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
    override suspend fun generateMarkdown(sourceText: String, model: String, apiKey: String): Result<String> =
        generateResult

    override suspend fun shortenMarkdown(currentMarkdown: String, model: String, apiKey: String): Result<String> =
        shortenResult

    override suspend fun reflectionFileExists(date: IsoDate, folder: String): Result<Boolean> = existsResult

    override suspend fun saveToVault(
        date: IsoDate,
        folder: String,
        markdown: String,
        replaceExistingFile: Boolean,
    ): Result<ReflectionDocument> = saveResult
}
