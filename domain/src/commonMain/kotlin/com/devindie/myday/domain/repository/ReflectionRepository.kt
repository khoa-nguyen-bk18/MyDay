package com.devindie.myday.domain.repository

import com.devindie.myday.domain.model.reflection.IsoDate
import com.devindie.myday.domain.model.reflection.ReflectionDocument

interface ReflectionRepository {
    suspend fun generateMarkdown(sourceText: String, model: String, apiKey: String): Result<String>

    suspend fun shortenMarkdown(currentMarkdown: String, model: String, apiKey: String): Result<String>

    suspend fun reflectionFileExists(date: IsoDate, folder: String): Result<Boolean>

    suspend fun saveToVault(
        date: IsoDate,
        folder: String,
        markdown: String,
        replaceExistingFile: Boolean,
    ): Result<ReflectionDocument>
}
