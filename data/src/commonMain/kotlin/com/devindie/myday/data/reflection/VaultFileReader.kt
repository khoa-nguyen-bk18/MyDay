package com.devindie.myday.data.reflection

/** In-memory or vault-backed text reads for Obsidian config resolution and tests. */
interface VaultFileReader {
    suspend fun readTextOrNull(relativePath: String): String?
}
