package com.devindie.myday.data.reflection

import com.devindie.myday.domain.model.reflection.ReflectionError
import com.devindie.myday.domain.model.reflection.VaultLink
import com.devindie.myday.domain.repository.VaultPickerPort
import com.devindie.myday.storage.api.StorageAccessMode
import com.devindie.myday.storage.api.StorageClient
import com.devindie.myday.storage.api.StorageError
import com.devindie.myday.storage.api.StoragePickRequest
import com.devindie.myday.storage.api.StorageResult

class StorageVaultPicker(private val storageClient: StorageClient) : VaultPickerPort {
    override suspend fun pickVault(): Result<VaultLink> =
        when (val result = storageClient.pickFolder(StoragePickRequest(StorageAccessMode.ReadWrite))) {
            is StorageResult.Success -> Result.success(VaultLink(result.value.value))
            is StorageResult.Cancelled -> Result.failure(ReflectionError.Cancelled)
            is StorageResult.Failure -> Result.failure(result.error.toReflectionError())
        }

    private fun StorageError.toReflectionError(): ReflectionError = when (this) {
        StorageError.PermissionDenied -> ReflectionError.VaultPermissionDenied
        StorageError.NotConfigured ->
            ReflectionError.Provider(code = null, message = "Storage picker is not configured on this device.")
        StorageError.NotFound ->
            ReflectionError.Provider(code = null, message = "Selected folder was not found.")
        is StorageError.InvalidPath ->
            ReflectionError.Provider(code = null, message = "Invalid vault path: $relativePath")
        is StorageError.Io ->
            ReflectionError.Provider(code = null, message = message)
    }
}
