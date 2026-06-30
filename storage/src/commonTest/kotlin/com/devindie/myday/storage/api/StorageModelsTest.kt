package com.devindie.myday.storage.api

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class StorageModelsTest {
    @Test
    fun storageLocationToken_wrapsOpaqueString() {
        val token = StorageLocationToken("tree-uri-or-bookmark")
        assertEquals("tree-uri-or-bookmark", token.value)
    }

    @Test
    fun storageResult_successWrapsValue() {
        val result = StorageResult.Success("ok")
        assertEquals("ok", (result as StorageResult.Success).value)
    }

    @Test
    fun storageAccessMode_hasReadWrite() {
        assertTrue(StorageAccessMode.entries.contains(StorageAccessMode.ReadWrite))
    }
}
